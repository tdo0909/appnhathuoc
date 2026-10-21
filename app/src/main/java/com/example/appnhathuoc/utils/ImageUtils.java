package com.example.appnhathuoc.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ImageUtils {

    private static final ExecutorService executor = Executors.newFixedThreadPool(4);
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());
    private static final LruCache<String, Bitmap> memoryCache;

    static {
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        memoryCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public static void loadImage(ImageView imageView, String urlString, int placeholderResId) {
        if (imageView == null) return;

        if (urlString == null || urlString.trim().isEmpty()) {
            imageView.setTag(null);
            imageView.setImageResource(placeholderResId);
            return;
        }

        imageView.setTag(urlString);

        // Check memory cache
        Bitmap cachedBitmap = memoryCache.get(urlString);
        if (cachedBitmap != null) {
            imageView.setImageBitmap(cachedBitmap);
            return;
        }
        imageView.setImageResource(placeholderResId);
        imageView.setImageResource(placeholderResId);

        // Handle Base64 Data URLs
        if (urlString.startsWith("data:image/") || urlString.contains("base64,")) {
            executor.execute(() -> {
                try {
                    String base64Data = urlString;
                    if (urlString.contains("base64,")) {
                        base64Data = urlString.substring(urlString.indexOf("base64,") + 7);
                    }
                    byte[] decodedBytes = Base64.decode(base64Data, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                    if (bitmap != null) {
                        memoryCache.put(urlString, bitmap);
                        mainHandler.post(() -> {
                            if (urlString.equals(imageView.getTag())) {
                                imageView.setImageBitmap(bitmap);
                            }
                        });
                    }
                } catch (Exception e) {
                    // Keep placeholder
                }
            });
            return;
        }

        // Handle HTTP / HTTPS URLs
        executor.execute(() -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(6000);
                conn.setReadTimeout(6000);
                conn.setDoInput(true);
                conn.connect();

                InputStream is = conn.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                is.close();

                if (bitmap != null) {
                    memoryCache.put(urlString, bitmap);
                    mainHandler.post(() -> {
                        if (urlString.equals(imageView.getTag())) {
                            imageView.setImageBitmap(bitmap);
                        }
                    });
                }
            } catch (Exception e) {
                // Keep placeholder if failed
            }
        });
    }
}