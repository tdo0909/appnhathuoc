package com.example.appnhathuoc.api;

import android.os.Handler;
import android.os.Looper;

import com.example.appnhathuoc.R;
import com.example.appnhathuoc.data.DataRepository;
import com.example.appnhathuoc.model.Article;
import com.example.appnhathuoc.model.BannerItem;
import com.example.appnhathuoc.model.CartItem;
import com.example.appnhathuoc.model.Category;
import com.example.appnhathuoc.model.NotificationItem;
import com.example.appnhathuoc.model.Order;
import com.example.appnhathuoc.model.Product;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApiClient {

    // Google Firebase Realtime Database Cloud URL
    public static String BASE_URL = "https://appnhathuoc-6ba94-default-rtdb.asia-southeast1.firebasedatabase.app/";

    private static final ExecutorService executor = Executors.newFixedThreadPool(4);
    private static final Handler mainHandler = new Handler(Looper.getMainLooper());

    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String errorMessage);
    }

    private static String executeGet(String endpoint) throws Exception {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setConnectTimeout(6000);
        conn.setReadTimeout(6000);

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } else {
            throw new Exception("HTTP Error: " + responseCode);
        }
    }

    private static String executePut(String endpoint, String jsonBody) throws Exception {
        URL url = new URL(BASE_URL + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setDoOutput(true);
        conn.setConnectTimeout(6000);
        conn.setReadTimeout(6000);

        OutputStream os = conn.getOutputStream();
        os.write(jsonBody.getBytes("UTF-8"));
        os.flush();
        os.close();

        int responseCode = conn.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } else {
            throw new Exception("HTTP Error: " + responseCode);
        }
    }

    private static List<JSONObject> parseJsonContainerToList(String jsonStr) {
        List<JSONObject> list = new ArrayList<>();
        if (jsonStr == null || jsonStr.trim().isEmpty() || jsonStr.equals("null")) {
            return list;
        }
        try {
            String trimmed = jsonStr.trim();
            if (trimmed.startsWith("[")) {
                JSONArray arr = new JSONArray(trimmed);
                for (int i = 0; i < arr.length(); i++) {
                    if (!arr.isNull(i)) {
                        list.add(arr.getJSONObject(i));
                    }
                }
            } else if (trimmed.startsWith("{")) {
                JSONObject obj = new JSONObject(trimmed);
                Iterator<String> keys = obj.keys();
                while (keys.hasNext()) {
                    String k = keys.next();
                    if (!obj.isNull(k)) {
                        JSONObject child = obj.optJSONObject(k);
                        if (child != null) {
                            if (!child.has("id")) {
                                child.put("id", k);
                            }
                            list.add(child);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    private static String firstNonEmptyString(JSONObject object, String... fieldNames) {
        for (String fieldName : fieldNames) {
            Object value = object.opt(fieldName);
            if (value instanceof String) {
                String text = ((String) value).trim();
                if (!text.isEmpty() && !"null".equalsIgnoreCase(text)) {
                    return text;
                }
            } else if (value instanceof JSONObject) {
                JSONObject image = (JSONObject) value;
                String nestedUrl = firstNonEmptyString(image, "url", "downloadUrl", "download_url", "uri");
                if (!nestedUrl.isEmpty()) {
                    return nestedUrl;
                }
            }
        }
        return "";
    }

    public static void fetchBanners(ApiCallback<List<BannerItem>> callback) {
        executor.execute(() -> {
            try {
                String jsonStr = executeGet("banners.json");
                List<JSONObject> jsonList = parseJsonContainerToList(jsonStr);
                if (!jsonList.isEmpty()) {
                    List<BannerItem> list = new ArrayList<>();
                    int bannerIndex = 0;
                    for (JSONObject b : jsonList) {
                        BannerItem item = new BannerItem(
                                b.optString("id", "b1"),
                                b.optString("title", "Khuyến Mãi"),
                                b.optString("subtitle", "Ưu đãi đặc biệt"),
                                b.optString("actionText", "Xem ngay"),
                                getBannerBackground(bannerIndex++)
                        );
                        String imgUrl = b.optString("imageUrl", b.optString("image_url", b.optString("image", "")));
                        item.setImageUrl(imgUrl);
                        list.add(item);
                    }
                    mainHandler.post(() -> callback.onSuccess(list));
                } else {
                    mainHandler.post(() -> callback.onSuccess(DataRepository.getBanners()));
                }
            } catch (Exception e) {
                mainHandler.post(() -> callback.onSuccess(DataRepository.getBanners()));
            }
        });
    }

    private static int getBannerBackground(int position) {
        switch (position % 5) {
            case 1:
                return R.drawable.bg_banner_2;
            case 2:
                return R.drawable.bg_banner_3;
            case 3:
                return R.drawable.bg_banner_4;
            case 4:
                return R.drawable.bg_banner_5;
            default:
                return R.drawable.bg_banner_1;
        }
    }

    public static void fetchCategories(ApiCallback<List<Category>> callback) {
        executor.execute(() -> {
            try {
                String jsonStr = executeGet("categories.json");
                List<JSONObject> jsonList = parseJsonContainerToList(jsonStr);
                if (!jsonList.isEmpty()) {
                    List<Category> list = new ArrayList<>();
                    for (JSONObject c : jsonList) {
                        String code = firstNonEmptyString(c, "category_code", "categoryCode", "id");
                        int icon = getCategoryIconDrawable(code);
                        Category cat = new Category(code, c.optString("name", "Danh mục"), icon);
                        String imgUrl = firstNonEmptyString(c,
                                "iconUrl", "icon_url", "imageUrl", "image_url", "icon", "image", "photoUrl", "thumbnail");
                        cat.setIconUrl(imgUrl);
                        list.add(cat);
                    }
                    mainHandler.post(() -> callback.onSuccess(list));
                } else {
                    mainHandler.post(() -> callback.onSuccess(DataRepository.getCategories()));
                }
            } catch (Exception e) {
                mainHandler.post(() -> callback.onSuccess(DataRepository.getCategories()));
            }
        });
    }

    public static void fetchProducts(String categoryId, boolean isBestseller, boolean isPromotion, String search, String sort, ApiCallback<List<Product>> callback) {
        executor.execute(() -> {
            try {
                String jsonStr = executeGet("products.json");
                List<JSONObject> jsonList = parseJsonContainerToList(jsonStr);
                if (!jsonList.isEmpty()) {
                    List<Product> list = new ArrayList<>();
                    List<Product> allList = new ArrayList<>();

                    for (JSONObject p : jsonList) {
                        String catId = p.optString("categoryId", p.optString("category_id", ""));
                        
                        boolean bestseller = p.optBoolean("isBestSeller",
                                p.optBoolean("bestSeller",
                                p.optBoolean("is_bestseller",
                                p.optBoolean("bestseller",
                                p.optInt("isBestSeller", p.optInt("bestSeller", p.optInt("is_bestseller", 0))) == 1))));

                        boolean promotion = p.optBoolean("isPromotion",
                                p.optBoolean("promotion",
                                p.optBoolean("is_promotion",
                                p.optBoolean("promotion",
                                p.optInt("isPromotion", p.optInt("promotion", p.optInt("is_promotion", 0))) == 1))));

                        String name = p.optString("name", "");

                        Product prod = new Product(
                                p.optString("id", ""),
                                name,
                                p.optDouble("price", 0),
                                p.optDouble("oldPrice", p.optDouble("old_price", 0)),
                                p.optInt("discountPercent", p.optInt("discount_percent", 0)),
                                (float) p.optDouble("rating", 5.0),
                                p.optInt("reviewCount", p.optInt("review_count", 0)),
                                catId,
                                p.optString("categoryName", p.optString("category_name", "")),
                                R.drawable.ic_product_placeholder,
                                p.optString("description", ""),
                                p.optString("ingredient", ""),
                                p.optString("usage", ""),
                                p.optString("specification", ""),
                                p.optString("origin", ""),
                                bestseller,
                                promotion
                        );
                        prod.setImageUrl(p.optString("imageUrl", p.optString("image_url", p.optString("image", ""))));
                        prod.setBrand(p.optString("brand", ""));
                        prod.setTargetUser(p.optString("targetUser", p.optString("target_user", "")));
                        prod.setContraindication(p.optString("contraindication", ""));
                        prod.setStorage(p.optString("storage", ""));
                        prod.setRegNumber(p.optString("regNumber", p.optString("reg_number", "")));
                        allList.add(prod);

                        if (categoryId != null && !categoryId.isEmpty() && !catId.equalsIgnoreCase(categoryId)) {
                            continue;
                        }
                        if (isBestseller && !bestseller) {
                            continue;
                        }
                        if (isPromotion && !promotion) {
                            continue;
                        }
                        if (search != null && !search.trim().isEmpty() && !name.toLowerCase().contains(search.toLowerCase())) {
                            continue;
                        }

                        list.add(prod);
                    }

                    boolean isFilterApplied = (categoryId != null && !categoryId.isEmpty()) || isBestseller || isPromotion || (search != null && !search.trim().isEmpty());
                    if (list.isEmpty() && !isFilterApplied) {
                        list = allList;
                    }

                    List<Product> finalList = list;
                    mainHandler.post(() -> callback.onSuccess(finalList));
                } else {
                    mainHandler.post(() -> callback.onSuccess(DataRepository.getProducts()));
                }
            } catch (Exception e) {
                mainHandler.post(() -> callback.onSuccess(DataRepository.getProducts()));
            }
        });
    }

    public static void fetchArticles(ApiCallback<List<Article>> callback) {
        executor.execute(() -> {
            try {
                String jsonStr = executeGet("articles.json");
                List<JSONObject> jsonList = parseJsonContainerToList(jsonStr);
                if (!jsonList.isEmpty()) {
                    List<Article> list = new ArrayList<>();
                    for (JSONObject a : jsonList) {
                        String title = firstNonEmptyString(a, "title", "name");
                        Article art = new Article(
                                a.optString("id", ""),
                                title,
                                firstNonEmptyString(a, "summary", "excerpt", "description"),
                                firstNonEmptyString(a, "content", "body", "article"),
                                firstNonEmptyString(a, "author", "authorName", "author_name").isEmpty()
                                        ? "Dược sĩ tư vấn"
                                        : firstNonEmptyString(a, "author", "authorName", "author_name"),
                                firstNonEmptyString(a, "publishDate", "publish_date", "date", "createdAt", "created_at").isEmpty()
                                        ? "Hôm nay"
                                        : firstNonEmptyString(a, "publishDate", "publish_date", "date", "createdAt", "created_at"),
                                R.drawable.ic_product_placeholder
                        );
                        art.setImageUrl(firstNonEmptyString(a,
                                "imageUrl", "image_url", "thumbnailUrl", "thumbnail_url", "thumbnail", "coverImage", "featuredImage", "image", "photoUrl"));
                        list.add(art);
                    }
                    mainHandler.post(() -> callback.onSuccess(list));
                } else {
                    mainHandler.post(() -> callback.onSuccess(DataRepository.getArticles()));
                }
            } catch (Exception e) {
                mainHandler.post(() -> callback.onSuccess(DataRepository.getArticles()));
            }
        });
    }

    public static void fetchNotifications(ApiCallback<List<NotificationItem>> callback) {
        executor.execute(() -> {
            try {
                String jsonStr = executeGet("notifications.json");
                List<JSONObject> jsonList = parseJsonContainerToList(jsonStr);
                if (!jsonList.isEmpty()) {
                    List<NotificationItem> list = new ArrayList<>();
                    for (JSONObject n : jsonList) {
                        NotificationItem item = new NotificationItem(
                                n.optString("id", ""),
                                n.optString("title", ""),
                                n.optString("message", ""),
                                n.optString("time", "Hôm nay"),
                                n.optString("type", "SYSTEM"),
                                n.optBoolean("isRead", false)
                        );
                        item.setImageUrl(firstNonEmptyString(n,
                                "imageUrl", "image_url", "image", "photoUrl", "photo_url",
                                "thumbnailUrl", "thumbnail_url", "bannerUrl", "banner_url"));
                        list.add(item);
                    }
                    mainHandler.post(() -> callback.onSuccess(list));
                } else {
                    mainHandler.post(() -> callback.onSuccess(DataRepository.getNotifications()));
                }
            } catch (Exception e) {
                mainHandler.post(() -> callback.onSuccess(DataRepository.getNotifications()));
            }
        });
    }

    public static void login(String phone, String password, ApiCallback<JSONObject> callback) {
        executor.execute(() -> {
            try {
                String jsonStr = executeGet("users.json");
                List<JSONObject> jsonList = parseJsonContainerToList(jsonStr);
                if (!jsonList.isEmpty()) {
                    for (JSONObject u : jsonList) {
                        if (phone.equals(u.optString("phone")) && password.equals(u.optString("password"))) {
                            if (u.optInt("is_locked", 0) == 1) {
                                mainHandler.post(() -> callback.onError("Tài khoản của bạn đã bị khóa bởi Quản trị viên!"));
                                return;
                            }
                            JSONObject response = new JSONObject();
                            response.put("status", true);
                            JSONObject uObj = new JSONObject();
                            uObj.put("name", u.optString("full_name", u.optString("fullName", "Khách hàng")));
                            uObj.put("phone", phone);
                            response.put("user", uObj);
                            mainHandler.post(() -> callback.onSuccess(response));
                            return;
                        }
                    }
                    mainHandler.post(() -> callback.onError("Số điện thoại hoặc mật khẩu không chính xác"));
                } else {
                    mainHandler.post(() -> callback.onError("Không tìm thấy thông tin tài khoản"));
                }
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError("Lỗi kết nối Firebase: " + e.getMessage()));
            }
        });
    }

    public static void register(String fullName, String phone, String email, String password, ApiCallback<JSONObject> callback) {
        executor.execute(() -> {
            try {
                JSONObject userObj = new JSONObject();
                userObj.put("full_name", fullName);
                userObj.put("phone", phone);
                userObj.put("email", email);
                userObj.put("password", password);
                userObj.put("is_locked", 0);

                executePut("users/user_" + phone + ".json", userObj.toString());

                JSONObject response = new JSONObject();
                response.put("status", true);
                response.put("message", "Đăng ký thành công!");
                JSONObject uObj = new JSONObject();
                uObj.put("name", fullName);
                uObj.put("phone", phone);
                response.put("user", uObj);

                mainHandler.post(() -> callback.onSuccess(response));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError("Lỗi tạo tài khoản trên Firebase: " + e.getMessage()));
            }
        });
    }

    public static void placeOrder(JSONObject orderData, ApiCallback<JSONObject> callback) {
        executor.execute(() -> {
            try {
                String orderCode = "MC" + (100000 + new Random().nextInt(900000));
                orderData.put("orderId", orderCode);
                orderData.put("status", "Chờ xác nhận");

                executePut("orders/" + orderCode + ".json", orderData.toString());

                JSONObject response = new JSONObject();
                response.put("status", true);
                response.put("order_code", orderCode);
                mainHandler.post(() -> callback.onSuccess(response));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError("Lỗi lưu đơn hàng trên Firebase: " + e.getMessage()));
            }
        });
    }

    public static void checkDeliveredPurchase(String phone, String productId, String productName,
                                              ApiCallback<String> callback) {
        executor.execute(() -> {
            try {
                if (phone == null || phone.trim().isEmpty()) {
                    mainHandler.post(() -> callback.onSuccess(""));
                    return;
                }
                String jsonStr = executeGet("orders.json");
                List<JSONObject> orders = parseJsonContainerToList(jsonStr);
                for (JSONObject order : orders) {
                    String orderPhone = order.optString("phone", "").trim();
                    String status = order.optString("status", "").trim();
                    if (!phone.trim().equals(orderPhone)
                            || !("Đã giao".equalsIgnoreCase(status) || "Đã nhận".equalsIgnoreCase(status))
                            || !orderContainsProduct(order, productId, productName)) {
                        continue;
                    }
                    String orderId = firstNonEmptyString(order, "orderId", "order_id", "order_code", "id");
                    mainHandler.post(() -> callback.onSuccess(orderId));
                    return;
                }
                mainHandler.post(() -> callback.onSuccess(""));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError("Không kiểm tra được đơn hàng đã giao: " + e.getMessage()));
            }
        });
    }

    private static boolean orderContainsProduct(JSONObject order, String productId, String productName) {
        JSONArray items = order.optJSONArray("items");
        if (items == null) {
            JSONObject itemObject = order.optJSONObject("items");
            if (itemObject != null) {
                items = new JSONArray();
                for (java.util.Iterator<String> keys = itemObject.keys(); keys.hasNext();) {
                    JSONObject item = itemObject.optJSONObject(keys.next());
                    if (item != null) {
                        items.put(item);
                    }
                }
            }
        }
        if (items == null) return false;

        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.optJSONObject(i);
            if (item == null) continue;
            String itemId = firstNonEmptyString(item, "productId", "product_id", "productCode", "product_code", "id");
            String itemName = firstNonEmptyString(item, "product_name", "productName", "name");
            if ((productId != null && !productId.trim().isEmpty() && productId.equalsIgnoreCase(itemId))
                    || (productName != null && !productName.trim().isEmpty() && productName.equalsIgnoreCase(itemName))) {
                return true;
            }
        }
        return false;
    }

    public static void fetchOrders(String phone, ApiCallback<List<Order>> callback) {
        executor.execute(() -> {
            try {
                if (phone == null || phone.trim().isEmpty()) {
                    mainHandler.post(() -> callback.onSuccess(new ArrayList<>()));
                    return;
                }
                String jsonStr = executeGet("orders.json");
                List<JSONObject> jsonList = parseJsonContainerToList(jsonStr);
                if (!jsonList.isEmpty()) {
                    List<Order> list = new ArrayList<>();
                    for (JSONObject o : jsonList) {
                        String oPhone = o.optString("phone", "");
                        if (!oPhone.equals(phone.trim())) {
                            continue;
                        }

                        List<CartItem> cartItems = new ArrayList<>();
                        JSONArray itemsArr = o.optJSONArray("items");
                        if (itemsArr != null) {
                            for (int j = 0; j < itemsArr.length(); j++) {
                                JSONObject ci = itemsArr.getJSONObject(j);
                                Product prod = new Product(
                                        "p1",
                                        ci.optString("product_name", ci.optString("name", "Sản phẩm")),
                                        ci.optDouble("price", 0),
                                        0, 0, 5.0f, 0, "", "",
                                        R.drawable.ic_product_placeholder,
                                        "", "", "", "", "", false, false
                                );
                                cartItems.add(new CartItem(prod, ci.optInt("quantity", 1)));
                            }
                        }

                        list.add(new Order(
                                o.optString("orderId", "MC1000"),
                                o.optString("date", "Hôm nay"),
                                cartItems,
                                o.optDouble("total_amount", o.optDouble("totalAmount", 0)),
                                o.optString("status", "Chờ xác nhận"),
                                o.optString("full_name", o.optString("fullName", "Khách hàng")),
                                oPhone,
                                o.optString("address", ""),
                                o.optString("payment_method", o.optString("paymentMethod", "COD"))
                        ));
                    }
                    mainHandler.post(() -> callback.onSuccess(list));
                } else {
                    mainHandler.post(() -> callback.onSuccess(new ArrayList<>()));
                }
            } catch (Exception e) {
                mainHandler.post(() -> callback.onSuccess(new ArrayList<>()));
            }
        });
    }

    public static void fetchReviews(String productCode, ApiCallback<List<com.example.appnhathuoc.model.ReviewItem>> callback) {
        executor.execute(() -> {
            try {
                String jsonStr = executeGet("reviews/" + productCode + ".json");
                List<JSONObject> jsonList = parseJsonContainerToList(jsonStr);
                if (!jsonList.isEmpty()) {
                    List<com.example.appnhathuoc.model.ReviewItem> list = new ArrayList<>();
                    for (JSONObject r : jsonList) {
                        list.add(new com.example.appnhathuoc.model.ReviewItem(
                                r.optInt("id", 1),
                                r.optString("userName", "Khách hàng"),
                                r.optInt("rating", 5),
                                r.optString("comment", ""),
                                r.optString("date", "Gần đây")
                        ));
                    }
                    mainHandler.post(() -> callback.onSuccess(list));
                } else {
                    mainHandler.post(() -> callback.onSuccess(new ArrayList<>()));
                }
            } catch (Exception e) {
                mainHandler.post(() -> callback.onSuccess(new ArrayList<>()));
            }
        });
    }

    public static void submitReview(String productCode, String productName, String phone, int rating,
                                    String comment, ApiCallback<JSONObject> callback) {
        executor.execute(() -> {
            try {
                if (phone == null || phone.trim().isEmpty()) {
                    mainHandler.post(() -> callback.onError("Bạn cần đăng nhập để đánh giá sản phẩm."));
                    return;
                }
                if (rating < 1 || rating > 5 || comment == null || comment.trim().isEmpty()) {
                    mainHandler.post(() -> callback.onError("Vui lòng chọn số sao và nhập nội dung đánh giá."));
                    return;
                }

                String deliveredOrderId = findDeliveredOrderId(executeGet("orders.json"), phone, productCode, productName);
                if (deliveredOrderId.isEmpty()) {
                    mainHandler.post(() -> callback.onError("Chỉ khách hàng đã nhận sản phẩm mới có thể đánh giá."));
                    return;
                }

                JSONObject reviewObj = new JSONObject();
                reviewObj.put("userName", "Khách hàng");
                reviewObj.put("phone", phone);
                reviewObj.put("orderId", deliveredOrderId);
                reviewObj.put("rating", rating);
                reviewObj.put("comment", comment.trim());
                reviewObj.put("date", "Vừa xong");

                String revKey = "rev_" + System.currentTimeMillis();
                executePut("reviews/" + productCode + "/" + revKey + ".json", reviewObj.toString());

                JSONObject response = new JSONObject();
                response.put("status", true);
                response.put("message", "Gửi đánh giá thành công!");
                mainHandler.post(() -> callback.onSuccess(response));
            } catch (Exception e) {
                mainHandler.post(() -> callback.onError("Lỗi gửi đánh giá: " + e.getMessage()));
            }
        });
    }

    private static String findDeliveredOrderId(String json, String phone, String productId, String productName) {
        for (JSONObject order : parseJsonContainerToList(json)) {
            String status = order.optString("status", "").trim();
            if (phone.trim().equals(order.optString("phone", "").trim())
                    && ("Đã giao".equalsIgnoreCase(status) || "Đã nhận".equalsIgnoreCase(status))
                    && orderContainsProduct(order, productId, productName)) {
                return firstNonEmptyString(order, "orderId", "order_id", "order_code", "id");
            }
        }
        return "";
    }

    private static int getCategoryIconDrawable(String code) {
        if ("cat_thuoc".equals(code)) return R.drawable.ic_cat_medicine;
        if ("cat_vitamin".equals(code)) return R.drawable.ic_cat_vitamin;
        if ("cat_mypham".equals(code)) return R.drawable.ic_cat_cosmetics;
        if ("cat_canhan".equals(code)) return R.drawable.ic_cat_personal;
        if ("cat_mebe".equals(code)) return R.drawable.ic_cat_mom_baby;
        if ("cat_thietbi".equals(code)) return R.drawable.ic_cat_device;
        if ("cat_dinhduong".equals(code)) return R.drawable.ic_cat_nutrition;
        if ("cat_vesinh".equals(code)) return R.drawable.ic_cat_hygiene;
        return R.drawable.ic_cat_medicine;
    }
}
