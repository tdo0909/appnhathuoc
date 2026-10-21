package com.example.appnhathuoc.model;

import java.io.Serializable;

public class Product implements Serializable {
    private String id;
    private String name;
    private double price;
    private double oldPrice;
    private int discountPercent;
    private float rating;
    private int reviewCount;
    private String categoryId;
    private String categoryName;
    private int imageResId;
    private String imageUrl;
    private String description;
    private String ingredient;
    private String usage;
    private String specification;
    private String origin;
    private String brand;
    private String targetUser;
    private String contraindication;
    private String storage;
    private String regNumber;
    private boolean isBestSeller;
    private boolean isPromotion;

    public Product() {}

    public Product(String id, String name, double price, double oldPrice, int discountPercent,
                   float rating, int reviewCount, String categoryId, String categoryName,
                   int imageResId, String description, String ingredient, String usage,
                   String specification, String origin, boolean isBestSeller, boolean isPromotion) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.oldPrice = oldPrice;
        this.discountPercent = discountPercent;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.imageResId = imageResId;
        this.description = description;
        this.ingredient = ingredient;
        this.usage = usage;
        this.specification = specification;
        this.origin = origin;
        this.isBestSeller = isBestSeller;
        this.isPromotion = isPromotion;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getOldPrice() { return oldPrice; }
    public void setOldPrice(double oldPrice) { this.oldPrice = oldPrice; }

    public int getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(int discountPercent) { this.discountPercent = discountPercent; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public int getReviewCount() { return reviewCount; }
    public void setReviewCount(int reviewCount) { this.reviewCount = reviewCount; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getDescription() {
        return (description != null && !description.isEmpty()) ? description : "Sản phẩm chính hãng nhà thuốc, giúp hỗ trợ cải thiện và nâng cao sức khỏe toàn diện.";
    }
    public void setDescription(String description) { this.description = description; }

    public String getIngredient() {
        return (ingredient != null && !ingredient.isEmpty()) ? ingredient : "Hoạt chất chính chọn lọc cao cấp, tá dược vừa đủ.";
    }
    public void setIngredient(String ingredient) { this.ingredient = ingredient; }

    public String getUsage() {
        return (usage != null && !usage.isEmpty()) ? usage : "Sử dụng đúng theo hướng dẫn của nhà sản xuất hoặc chỉ định từ Dược sĩ/Bác sĩ chuyên khoa.";
    }
    public void setUsage(String usage) { this.usage = usage; }

    public String getSpecification() {
        return (specification != null && !specification.isEmpty()) ? specification : "Hộp tiêu chuẩn";
    }
    public void setSpecification(String specification) { this.specification = specification; }

    public String getOrigin() {
        return (origin != null && !origin.isEmpty()) ? origin : "Chính hãng";
    }
    public void setOrigin(String origin) { this.origin = origin; }

    public String getBrand() {
        return (brand != null && !brand.isEmpty()) ? brand : "Dược Phẩm Chính Hãng";
    }
    public void setBrand(String brand) { this.brand = brand; }

    public String getTargetUser() {
        return (targetUser != null && !targetUser.isEmpty()) ? targetUser : "Người trưởng thành, người có nhu cầu chăm sóc và bổ sung vi chất.";
    }
    public void setTargetUser(String targetUser) { this.targetUser = targetUser; }

    public String getContraindication() {
        return (contraindication != null && !contraindication.isEmpty()) ? contraindication : "Không sử dụng cho người mẫn cảm với bất kỳ thành phần nào của sản phẩm. Phụ nữ mang thai hoặc đang cho con bú nên tham khảo ý kiến Bác sĩ trước khi dùng.";
    }
    public void setContraindication(String contraindication) { this.contraindication = contraindication; }

    public String getStorage() {
        return (storage != null && !storage.isEmpty()) ? storage : "Bảo quản nơi khô ráo, thoáng mát, nhiệt độ dưới 30°C, tránh ánh nắng trực tiếp và xa tầm tay trẻ em.";
    }
    public void setStorage(String storage) { this.storage = storage; }

    public String getRegNumber() {
        return (regNumber != null && !regNumber.isEmpty()) ? regNumber : "SDK: VD-" + (10000 + (id != null ? Math.abs(id.hashCode() % 89999) : 12345)) + "-24";
    }
    public void setRegNumber(String regNumber) { this.regNumber = regNumber; }

    public boolean isBestSeller() { return isBestSeller; }
    public void setBestSeller(boolean bestSeller) { isBestSeller = bestSeller; }

    public boolean isPromotion() { return isPromotion; }
    public void setPromotion(boolean promotion) { isPromotion = promotion; }
}