package com.example.appnhathuoc.data;

import android.os.Handler;
import android.os.Looper;

import com.example.appnhathuoc.R;
import com.example.appnhathuoc.api.ApiClient;
import com.example.appnhathuoc.model.BannerItem;
import com.example.appnhathuoc.model.Category;
import com.example.appnhathuoc.model.NotificationItem;
import com.example.appnhathuoc.model.Product;
import com.example.appnhathuoc.model.Store;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class DataRepository {

    public interface OnDataUpdatedListener {
        void onDataUpdated();
    }

    private static List<Category> categories;
    private static List<Product> products;
    private static List<BannerItem> banners;
    private static List<com.example.appnhathuoc.model.Article> articles;

    private static final List<OnDataUpdatedListener> listeners = new ArrayList<>();
    private static Handler syncHandler;
    private static Runnable syncRunnable;
    private static boolean isSyncing = false;

    public static void addDataUpdatedListener(OnDataUpdatedListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
        startRealtimeSync();
    }

    public static void removeDataUpdatedListener(OnDataUpdatedListener listener) {
        listeners.remove(listener);
    }

    public static void notifyDataUpdated() {
        for (OnDataUpdatedListener listener : new ArrayList<>(listeners)) {
            if (listener != null) {
                listener.onDataUpdated();
            }
        }
    }

    public static synchronized void startRealtimeSync() {
        if (isSyncing) return;
        isSyncing = true;
        if (syncHandler == null) {
            syncHandler = new Handler(Looper.getMainLooper());
        }
        syncRunnable = new Runnable() {
            @Override
            public void run() {
                // Poll Firebase Cloud every 3 seconds for real-time live updates
                ApiClient.fetchProducts(null, false, false, null, null, new ApiClient.ApiCallback<List<Product>>() {
                    @Override
                    public void onSuccess(List<Product> result) {
                        if (result != null) {
                            products = result;
                            notifyDataUpdated();
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {}
                });

                if (isSyncing) {
                    syncHandler.postDelayed(this, 3000);
                }
            }
        };
        syncHandler.postDelayed(syncRunnable, 3000);
    }

    public static void stopRealtimeSync() {
        isSyncing = false;
        if (syncHandler != null && syncRunnable != null) {
            syncHandler.removeCallbacks(syncRunnable);
        }
    }

    public static List<BannerItem> getBanners() {
        if (banners == null) {
            banners = new ArrayList<>();
            banners.add(new BannerItem("b1", "Chăm sóc sức khỏe toàn diện", "Sản phẩm chăm sóc thiết yếu cho cả gia đình", "Xem ngay", R.drawable.bg_banner_1));
            banners.add(new BannerItem("b2", "Vitamin & khoáng chất", "Lựa chọn sản phẩm theo nhu cầu mỗi ngày", "Khám phá", R.drawable.bg_banner_2));
            banners.add(new BannerItem("b3", "Thiết bị chăm sóc sức khỏe", "Theo dõi sức khỏe tiện lợi ngay tại nhà", "Tìm hiểu", R.drawable.bg_banner_3));
            banners.add(new BannerItem("b4", "Chăm sóc mẹ & bé", "Khám phá sản phẩm dành cho mẹ và bé", "Khám phá", R.drawable.bg_banner_4));
            banners.add(new BannerItem("b5", "Chăm sóc cá nhân", "Tìm sản phẩm cho nhu cầu chăm sóc hằng ngày", "Xem sản phẩm", R.drawable.bg_banner_5));
        }
        return banners;
    }

    public static void loadBannersFromApi(ApiClient.ApiCallback<List<BannerItem>> callback) {
        ApiClient.fetchBanners(new ApiClient.ApiCallback<List<BannerItem>>() {
            @Override
            public void onSuccess(List<BannerItem> result) {
                List<BannerItem> uniqueBanners = removeDuplicateBanners(result);
                if (!uniqueBanners.isEmpty()) {
                    banners = uniqueBanners;
                }
                callback.onSuccess(getBanners());
            }

            @Override
            public void onError(String errorMessage) {
                callback.onSuccess(getBanners());
            }
        });
    }

    private static List<BannerItem> removeDuplicateBanners(List<BannerItem> source) {
        List<BannerItem> unique = new ArrayList<>();
        Set<String> seenContent = new HashSet<>();
        Set<String> seenImages = new HashSet<>();
        if (source == null) {
            return unique;
        }

        for (BannerItem banner : source) {
            if (banner == null) {
                continue;
            }
            String title = banner.getTitle() == null ? "" : banner.getTitle().trim();
            String subtitle = banner.getSubtitle() == null ? "" : banner.getSubtitle().trim();
            String contentKey = (title + "|" + subtitle).toLowerCase(Locale.ROOT);
            String imageUrl = banner.getImageUrl() == null ? "" : banner.getImageUrl().trim();
            if ((title.isEmpty() && subtitle.isEmpty()) || !seenContent.add(contentKey)) {
                continue;
            }
            if (!imageUrl.isEmpty() && !seenImages.add(imageUrl.toLowerCase(Locale.ROOT))) {
                continue;
            }
            unique.add(banner);
        }
        return unique;
    }

    public static void loadCategoriesFromApi(ApiClient.ApiCallback<List<Category>> callback) {
        ApiClient.fetchCategories(new ApiClient.ApiCallback<List<Category>>() {
            @Override
            public void onSuccess(List<Category> result) {
                if (result != null && !result.isEmpty()) {
                    categories = result;
                }
                callback.onSuccess(getCategories());
            }

            @Override
            public void onError(String errorMessage) {
                callback.onSuccess(getCategories());
            }
        });
    }

    public static List<Category> getCategories() {
        if (categories == null) {
            categories = new ArrayList<>();
            categories.add(new Category("cat_thuoc", "Thuốc", R.drawable.ic_cat_medicine));
            categories.add(new Category("cat_vitamin", "Vitamin", R.drawable.ic_cat_vitamin));
            categories.add(new Category("cat_mypham", "Dược mỹ phẩm", R.drawable.ic_cat_cosmetics));
            categories.add(new Category("cat_canhan", "Chăm sóc cá nhân", R.drawable.ic_cat_personal));
            categories.add(new Category("cat_mebe", "Mẹ & bé", R.drawable.ic_cat_mom_baby));
            categories.add(new Category("cat_thietbi", "Thiết bị y tế", R.drawable.ic_cat_device));
            categories.add(new Category("cat_dinhduong", "Sản phẩm dinh dưỡng", R.drawable.ic_cat_nutrition));
            categories.add(new Category("cat_vesinh", "Vệ sinh cá nhân", R.drawable.ic_cat_hygiene));
        }
        return categories;
    }

    public static void loadProductsFromApi(String categoryId, boolean isBestseller, boolean isPromotion, String search, String sort, ApiClient.ApiCallback<List<Product>> callback) {
        ApiClient.fetchProducts(categoryId, isBestseller, isPromotion, search, sort, new ApiClient.ApiCallback<List<Product>>() {
            @Override
            public void onSuccess(List<Product> result) {
                if (result != null && !result.isEmpty()) {
                    products = result;
                }
                callback.onSuccess(result != null ? result : getProducts());
            }

            @Override
            public void onError(String errorMessage) {
                callback.onSuccess(getProducts());
            }
        });
    }

    public static List<Product> getProducts() {
        if (products == null) {
            products = new ArrayList<>();
            products.add(new Product("p1", "Vitamin C 1000mg Tăng Cường Đề Kháng", 150000, 180000, 17, 4.8f, 120, "cat_vitamin", "Vitamin", R.drawable.ic_product_placeholder, "Viên uống bổ sung Vitamin C hỗ trợ miễn dịch, giúp làm đẹp da và tăng cường sức đề kháng cho cơ thể.", "Vitamin C 1000mg, Zinc 10mg", "Uống 1 viên mỗi ngày sau bữa ăn sáng.", "Hộp 60 viên", "Mỹ", true, true));
            products.add(new Product("p2", "Canxi Nano & Vitamin D3 Bổ Xương", 220000, 260000, 15, 4.9f, 95, "cat_vitamin", "Vitamin", R.drawable.ic_product_placeholder, "Canxi dạng Nano dễ hấp thu kết hợp D3 và K2 hỗ trợ xương răng chắc khỏe, giảm nguy cơ loãng xương.", "Canxi Nano 500mg, Vitamin D3 400IU, Vitamin K2", "Người lớn uống 2 viên/ngày, trẻ em trên 6 tuổi uống 1 viên/ngày.", "Hộp 100 viên", "Đức", true, true));
            products.add(new Product("p3", "Omega 3 Dầu Cá Ngừ Đại Dương", 310000, 380000, 18, 4.7f, 150, "cat_vitamin", "Vitamin", R.drawable.ic_product_placeholder, "Bổ sung EPA và DHA giúp bổ mắt, tăng cường trí nhớ và bảo vệ sức khỏe tim mạch.", "Dầu cá tự nhiên 1000mg (EPA 180mg, DHA 120mg)", "Uống 1-2 viên/ngày cùng bữa ăn.", "Lọ 120 viên", "Úc", true, true));
            products.add(new Product("p4", "Kẽm Bio-Zinc Hỗ Trợ Tiêu Hóa", 135000, 160000, 15, 4.6f, 80, "cat_vitamin", "Vitamin", R.drawable.ic_product_placeholder, "Hỗ trợ cải thiện vị giác, giúp ăn ngon miệng và tăng khả năng miễn dịch tự nhiên.", "Zinc Picolinate 15mg", "Uống 1 viên/ngày sau bữa ăn.", "Hộp 60 viên", "Pháp", true, false));
            products.add(new Product("p5", "Men Vi Sinh Probiotics 10 Tỷ Lợi Khuẩn", 280000, 320000, 12, 4.9f, 210, "cat_thuoc", "Thuốc", R.drawable.ic_product_placeholder, "Bổ sung lợi khuẩn giúp cân bằng hệ vi sinh đường ruột, giảm rối loạn tiêu hóa và chướng bụng.", "Lactobacillus, Bifidobacterium (10 tỷ CFU)", "Uống 1 gói/ngày pha với nước nguội.", "Hộp 30 gói", "Hàn Quốc", true, true));
            products.add(new Product("p6", "Kem Chống Nắng Dịu Nhẹ SPF 50+", 350000, 420000, 16, 4.8f, 310, "cat_mypham", "Dược mỹ phẩm", R.drawable.ic_product_placeholder, "Chống nắng phổ rộng bảo vệ da khỏi tia UVA/UVB, kiểm soát dầu thừa và không gây bết dính.", "Zinc Oxide, Titanium Dioxide, Niacinamide", "Thoa đều lên da trước khi ra ngoài 20 phút.", "Tuýp 50ml", "Pháp", true, true));
            products.add(new Product("p7", "Nhiệt Kế Hồng Ngoại Đo Trán Điện Tử", 450000, 550000, 18, 4.9f, 180, "cat_thietbi", "Thiết bị y tế", R.drawable.ic_product_placeholder, "Đo nhiệt độ nhanh chóng trong 1 giây không cần tiếp xúc, độ chính xác cao.", "Linh kiện điện tử cao cấp, màn hình LCD", "Đặt cách trán 1-3cm và bấm nút đo.", "Hộp 1 máy + pin", "Nhật Bản", true, false));
            products.add(new Product("p8", "Máy Đo Huyết Áp Bắp Tay Tự Động", 890000, 1100000, 19, 4.9f, 260, "cat_thietbi", "Thiết bị y tế", R.drawable.ic_product_placeholder, "Đo huyết áp và nhịp tim chính xác, cảnh báo loạn nhịp tim, bộ nhớ lưu 90 kết quả.", "Máy đo, vòng quấn bắp tay", "Quấn vòng bắp tay ngang tim và nhấn nút Start.", "Bộ sản phẩm đầy đủ", "Nhật Bản", true, true));
        }
        return products;
    }

    public static Product getProductById(String productId) {
        if (productId == null) return null;
        for (Product p : getProducts()) {
            if (productId.equalsIgnoreCase(p.getId())) {
                return p;
            }
        }
        return null;
    }

    public static List<Product> getProductsByCategory(String categoryId) {
        List<Product> list = new ArrayList<>();
        if (categoryId == null) return list;
        for (Product p : getProducts()) {
            if (categoryId.equalsIgnoreCase(p.getCategoryId())) {
                list.add(p);
            }
        }
        return list;
    }

    public static List<Store> getStores() {
        List<Store> list = new ArrayList<>();
        list.add(new Store("s1", "Nhà Thuốc Bến Nghé", "123 Lê Lợi, P. Bến Nghé, Q.1, TP.HCM", "TP. Hồ Chí Minh", "028 3821 1234", "06:00 - 22:30"));
        list.add(new Store("s2", "Nhà Thuốc Nguyễn Huệ", "45 Nguyễn Huệ, P. Bến Nghé, Q.1, TP.HCM", "TP. Hồ Chí Minh", "028 3822 5678", "06:00 - 22:30"));
        list.add(new Store("s3", "Nhà Thuốc Hoàn Kiếm", "88 Lý Thường Kiệt, Q. Hoàn Kiếm, Hà Nội", "Hà Nội", "024 3942 1122", "06:00 - 22:30"));
        list.add(new Store("s4", "Nhà Thuốc Cầu Giấy", "150 Cầu Giấy, Q. Cầu Giấy, Hà Nội", "Hà Nội", "024 3767 3344", "06:00 - 22:30"));
        list.add(new Store("s5", "Nhà Thuốc Hải Châu", "200 Lê Duẩn, Q. Hải Châu, Đà Nẵng", "Đà Nẵng", "0236 3888 999", "06:00 - 22:00"));
        list.add(new Store("s6", "Nhà Thuốc Lĩnh Nam", "186 Lĩnh Nam, P. Vĩnh Hưng, Q. Hoàng Mai, Hà Nội", "Hà Nội", "024 3644 8899", "06:00 - 22:30"));
        return list;
    }

    public static List<com.example.appnhathuoc.model.Article> getArticles() {
        if (articles == null) {
            articles = new ArrayList<>();
            articles.add(new com.example.appnhathuoc.model.Article("a1", "Vitamin C có tác dụng gì đối với sức khỏe?", "Vitamin C đóng vai trò vô cùng quan trọng trong việc tăng cường hệ miễn dịch, sản sinh collagen và chống oxy hóa.", "Vitamin C (acid ascorbic) là một dưỡng chất thiết yếu giúp bảo vệ tế bào khỏi tổn thương do các gốc tự do. Ngoài ra, Vitamin C còn cải thiện sự hấp thu sắt từ thực phẩm và hỗ trợ hệ thống miễn dịch hoạt động hiệu quả để bảo vệ cơ thể khỏi bệnh tật.\n\nLiều lượng khuyên dùng hàng ngày là từ 75mg - 100mg đối với người trưởng thành. Bạn có thể bổ sung qua các loại quả họ cam quýt, dâu tây, ớt chuông hoặc viên uống bổ sung chất lượng.", "Dược sĩ Tư Vấn", "28/09/2024", R.drawable.ic_product_placeholder));
            articles.add(new com.example.appnhathuoc.model.Article("a2", "Cách bổ sung canxi đúng cách giúp xương chắc khỏe", "Bổ sung canxi đúng thời điểm và kết hợp với Vitamin D3, K2 sẽ tối ưu hóa khả năng hấp thụ canxi vào xương.", "Canxi là khoáng chất chiếm khoảng 1.5 - 2% trọng lượng cơ thể, tập trung chủ yếu ở xương và răng. Khi bổ sung canxi, nên uống vào buổi sáng sau ăn 1 tiếng, tránh uống buổi tối để ngăn ngừa nguy cơ lắng đọng gây sỏi thận. Đừng quên kết hợp vận động ngoài trời để tổng hợp Vitamin D3 tự nhiên.", "ThS. Dược sĩ Nguyễn Thị Mai", "27/09/2024", R.drawable.ic_product_placeholder));
            articles.add(new com.example.appnhathuoc.model.Article("a3", "Mẹo chăm sóc sức khỏe & phòng ngừa cảm cúm mùa lạnh", "Mùa lạnh đến gần, việc giữ ấm cơ thể, súc họng nước muối và bổ sung vi chất giúp cả gia đình phòng bệnh hiệu quả.", "Thay đổi thời tiết đột ngột dễ làm suy giảm sức đề kháng. Hãy duy trì thói quen súc miệng bằng nước muối sinh lý mỗi sáng, bổ sung đầy đủ Vitamin C & Kẽm, đồng thời uống đủ 2 lít nước ấm mỗi ngày.", "Bác sĩ Lê Hoàng Nam", "26/09/2024", R.drawable.ic_product_placeholder));
        }
        return articles;
    }

    public static void loadArticlesFromApi(ApiClient.ApiCallback<List<com.example.appnhathuoc.model.Article>> callback) {
        ApiClient.fetchArticles(new ApiClient.ApiCallback<List<com.example.appnhathuoc.model.Article>>() {
            @Override
            public void onSuccess(List<com.example.appnhathuoc.model.Article> result) {
                if (result != null && !result.isEmpty()) {
                    articles = result;
                }
                callback.onSuccess(getArticles());
            }

            @Override
            public void onError(String errorMessage) {
                callback.onSuccess(getArticles());
            }
        });
    }

    public static List<NotificationItem> getNotifications() {
        List<NotificationItem> list = new ArrayList<>();
        list.add(new NotificationItem("n1", "Ưu đãi cực HOT đến 30%", "Giảm ngay 30% cho các dòng Vitamin C và Thực phẩm chức năng hôm nay!", "10 phút trước", "PROMOTION", false));
        list.add(new NotificationItem("n2", "Đơn hàng MC982103 đã giao thành công", "Đơn hàng của bạn đã được giao thành công. Cảm ơn bạn đã mua sắm tại Nhà Thuốc!", "2 giờ trước", "ORDER", false));
        list.add(new NotificationItem("n3", "Nhà thuốc xin chào!", "Chào mừng bạn đến với Nhà Thuốc - Ứng dụng chăm sóc sức khỏe tận tâm.", "1 ngày trước", "SYSTEM", true));
        return list;
    }
}