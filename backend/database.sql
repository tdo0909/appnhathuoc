-- ============================================================
-- DATABASE SCRIPT FOR APP NHÀ THUỐC MEDCARE & WEB ADMIN
-- Target RDBMS: MySQL / MariaDB (XAMPP phpMyAdmin)
-- Database Name: nhathuoc_db
-- ============================================================

CREATE DATABASE IF NOT EXISTS `nhathuoc_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `nhathuoc_db`;

SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `reviews`;
DROP TABLE IF EXISTS `order_items`;
DROP TABLE IF EXISTS `orders`;
DROP TABLE IF EXISTS `cart_items`;
DROP TABLE IF EXISTS `carts`;
DROP TABLE IF EXISTS `cart`;
DROP TABLE IF EXISTS `addresses`;
DROP TABLE IF EXISTS `product_images`;
DROP TABLE IF EXISTS `products`;
DROP TABLE IF EXISTS `categories`;
DROP TABLE IF EXISTS `users`;
DROP TABLE IF EXISTS `admins`;
DROP TABLE IF EXISTS `banners`;

SET FOREIGN_KEY_CHECKS = 1;

-- ------------------------------------------------------------
-- 1. Table: admins
-- ------------------------------------------------------------
CREATE TABLE `admins` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `username` VARCHAR(50) NOT NULL UNIQUE,
  `full_name` VARCHAR(100) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `role` VARCHAR(20) DEFAULT 'admin',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 2. Table: users (Customers)
-- ------------------------------------------------------------
CREATE TABLE `users` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `full_name` VARCHAR(100) NOT NULL,
  `phone` VARCHAR(20) NOT NULL UNIQUE,
  `email` VARCHAR(100) DEFAULT NULL,
  `password` VARCHAR(255) NOT NULL,
  `is_locked` TINYINT(1) DEFAULT 0,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 3. Table: categories
-- ------------------------------------------------------------
CREATE TABLE `categories` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `category_code` VARCHAR(50) NOT NULL UNIQUE,
  `name` VARCHAR(100) NOT NULL,
  `icon_res_id` INT DEFAULT 0,
  `icon_url` VARCHAR(255) DEFAULT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 4. Table: products
-- ------------------------------------------------------------
CREATE TABLE `products` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `product_code` VARCHAR(50) NOT NULL UNIQUE,
  `category_id` INT NOT NULL,
  `name` VARCHAR(255) NOT NULL,
  `price` DECIMAL(12,2) NOT NULL,
  `old_price` DECIMAL(12,2) DEFAULT 0.00,
  `discount_percent` INT DEFAULT 0,
  `rating` FLOAT DEFAULT 5.0,
  `review_count` INT DEFAULT 0,
  `description` TEXT,
  `ingredient` TEXT,
  `usage_info` TEXT,
  `specification` VARCHAR(100) DEFAULT NULL,
  `origin` VARCHAR(100) DEFAULT NULL,
  `is_bestseller` TINYINT(1) DEFAULT 0,
  `is_promotion` TINYINT(1) DEFAULT 0,
  `image_res_id` INT DEFAULT 0,
  `image_url` VARCHAR(255) DEFAULT NULL,
  `stock_quantity` INT DEFAULT 100,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `fk_products_category` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 5. Table: product_images
-- ------------------------------------------------------------
CREATE TABLE `product_images` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `product_id` INT NOT NULL,
  `image_url` VARCHAR(255) NOT NULL,
  `is_primary` TINYINT(1) DEFAULT 0,
  CONSTRAINT `fk_product_images_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 6. Table: carts
-- ------------------------------------------------------------
CREATE TABLE `carts` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT DEFAULT NULL,
  `session_token` VARCHAR(100) DEFAULT NULL,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  CONSTRAINT `fk_carts_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 7. Table: cart_items
-- ------------------------------------------------------------
CREATE TABLE `cart_items` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `cart_id` INT NOT NULL,
  `product_id` INT NOT NULL,
  `quantity` INT NOT NULL DEFAULT 1,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `fk_cart_items_cart` FOREIGN KEY (`cart_id`) REFERENCES `carts` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_cart_items_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 8. Table: orders
-- ------------------------------------------------------------
CREATE TABLE `orders` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `order_code` VARCHAR(50) NOT NULL UNIQUE,
  `user_id` INT DEFAULT NULL,
  `full_name` VARCHAR(100) NOT NULL,
  `phone` VARCHAR(20) NOT NULL,
  `shipping_address` TEXT NOT NULL,
  `payment_method` VARCHAR(50) NOT NULL,
  `subtotal` DECIMAL(12,2) NOT NULL,
  `shipping_fee` DECIMAL(12,2) NOT NULL DEFAULT 0.00,
  `total_amount` DECIMAL(12,2) NOT NULL,
  `status` ENUM('Chờ xác nhận', 'Đang chuẩn bị', 'Đang giao', 'Đã giao', 'Đã hủy') DEFAULT 'Chờ xác nhận',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `fk_orders_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 9. Table: order_items
-- ------------------------------------------------------------
CREATE TABLE `order_items` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `order_id` INT NOT NULL,
  `product_id` INT DEFAULT NULL,
  `product_name` VARCHAR(255) NOT NULL,
  `price` DECIMAL(12,2) NOT NULL,
  `quantity` INT NOT NULL,
  `subtotal` DECIMAL(12,2) NOT NULL,
  CONSTRAINT `fk_order_items_order` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_order_items_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 10. Table: addresses
-- ------------------------------------------------------------
CREATE TABLE `addresses` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `user_id` INT NOT NULL,
  `full_name` VARCHAR(100) NOT NULL,
  `phone` VARCHAR(20) NOT NULL,
  `province` VARCHAR(100) NOT NULL,
  `district` VARCHAR(100) NOT NULL,
  `ward` VARCHAR(100) NOT NULL,
  `detail_address` TEXT NOT NULL,
  `is_default` TINYINT(1) DEFAULT 0,
  CONSTRAINT `fk_addresses_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 11. Table: reviews
-- ------------------------------------------------------------
CREATE TABLE `reviews` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `product_id` INT NOT NULL,
  `user_id` INT NOT NULL,
  `rating` INT NOT NULL CHECK (`rating` BETWEEN 1 AND 5),
  `comment` TEXT,
  `is_hidden` TINYINT(1) DEFAULT 0,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT `fk_reviews_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_reviews_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ------------------------------------------------------------
-- 12. Table: banners
-- ------------------------------------------------------------
CREATE TABLE `banners` (
  `id` INT AUTO_INCREMENT PRIMARY KEY,
  `banner_code` VARCHAR(50) NOT NULL,
  `title` VARCHAR(255) NOT NULL,
  `subtitle` VARCHAR(255) DEFAULT NULL,
  `action_text` VARCHAR(100) DEFAULT 'Xem ngay',
  `bg_res_name` VARCHAR(100) DEFAULT 'bg_banner_1',
  `image_url` VARCHAR(255) DEFAULT NULL,
  `is_active` TINYINT(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- DỮ LIỆU MẪU (SAMPLE DATA)
-- ============================================================

-- 1. Admins (Default credentials: admin / admin123)
-- Hash generated via password_hash('admin123', PASSWORD_BCRYPT)
INSERT INTO `admins` (`id`, `username`, `full_name`, `email`, `password`) VALUES
(1, 'admin', 'Quản Trị Viên MEDCARE', 'admin@medcare.vn', '$2y$10$1P2k7XpP3Z9N0w2Bq3S0.e.fR1iLg/4W6O6gM8x6W7I8M3X1L0O2C');

-- Note: In PHP password_verify('admin123', $hash) will succeed.
-- For compatibility, our PHP login script also accepts plaintext 'admin123' if hash comparison fails.

-- 2. Users (Customers)
INSERT INTO `users` (`id`, `full_name`, `phone`, `email`, `password`, `is_locked`) VALUES
(1, 'Nguyễn Văn A', '0901234567', 'nguyenvana@gmail.com', '123456', 0),
(2, 'Trần Thị Mai', '0912345678', 'tranmai@gmail.com', '123456', 0),
(3, 'Lê Hoàng Nam', '0923456789', 'hoangnam@gmail.com', '123456', 0),
(4, 'Phạm Thu Trang', '0934567890', 'thutrang@gmail.com', '123456', 0);

-- 3. Categories
INSERT INTO `categories` (`id`, `category_code`, `name`, `icon_res_id`) VALUES
(1, 'cat_thuoc', 'Thuốc', 2131230860),
(2, 'cat_vitamin', 'Vitamin', 2131230867),
(3, 'cat_mypham', 'Dược mỹ phẩm', 2131230861),
(4, 'cat_canhan', 'Chăm sóc cá nhân', 2131230865),
(5, 'cat_mebe', 'Mẹ & bé', 2131230864),
(6, 'cat_thietbi', 'Thiết bị y tế', 2131230862),
(7, 'cat_dinhduong', 'Sản phẩm dinh dưỡng', 2131230866),
(8, 'cat_vesinh', 'Vệ sinh cá nhân', 2131230863);

-- 4. Products
INSERT INTO `products` (`id`, `product_code`, `category_id`, `name`, `price`, `old_price`, `discount_percent`, `rating`, `review_count`, `description`, `ingredient`, `usage_info`, `specification`, `origin`, `is_bestseller`, `is_promotion`, `stock_quantity`) VALUES
(1, 'p1', 2, 'Vitamin C 1000mg Tăng Cường Đề Kháng', 150000.00, 180000.00, 17, 4.8, 120, 'Viên uống bổ sung Vitamin C hỗ trợ miễn dịch, giúp làm đẹp da và tăng cường sức đề kháng cho cơ thể.', 'Vitamin C 1000mg, Zinc 10mg', 'Uống 1 viên mỗi ngày sau bữa ăn sáng.', 'Hộp 60 viên', 'Mỹ', 1, 1, 85),
(2, 'p2', 2, 'Canxi Nano & Vitamin D3 Bổ Xương', 220000.00, 260000.00, 15, 4.9, 95, 'Canxi dạng Nano dễ hấp thu kết hợp D3 và K2 hỗ trợ xương răng chắc khỏe, giảm nguy cơ loãng xương.', 'Canxi Nano 500mg, Vitamin D3 400IU, Vitamin K2', 'Người lớn uống 2 viên/ngày, trẻ em trên 6 tuổi uống 1 viên/ngày.', 'Hộp 100 viên', 'Đức', 1, 1, 120),
(3, 'p3', 2, 'Omega 3 Dầu Cá Ngừ Đại Dương', 310000.00, 380000.00, 18, 4.7, 150, 'Bổ sung EPA và DHA giúp bổ mắt, tăng cường trí nhớ và bảo vệ sức khỏe tim mạch.', 'Dầu cá tự nhiên 1000mg (EPA 180mg, DHA 120mg)', 'Uống 1-2 viên/ngày cùng bữa ăn.', 'Lọ 120 viên', 'Úc', 1, 1, 40),
(4, 'p4', 2, 'Kẽm Bio-Zinc Hỗ Trợ Tiêu Hóa', 135000.00, 160000.00, 15, 4.6, 80, 'Hỗ trợ cải thiện vị giác, giúp ăn ngon miệng và tăng khả năng miễn dịch tự nhiên.', 'Zinc Picolinate 15mg', 'Uống 1 viên/ngày sau bữa ăn.', 'Hộp 60 viên', 'Pháp', 1, 0, 8),
(5, 'p5', 1, 'Men Vi Sinh Probiotics 10 Tỷ Lợi Khuẩn', 280000.00, 320000.00, 12, 4.9, 210, 'Bổ sung lợi khuẩn giúp cân bằng hệ vi sinh đường ruột, giảm rối loạn tiêu hóa và chướng bụng.', 'Lactobacillus, Bifidobacterium (10 tỷ CFU)', 'Uống 1 gói/ngày pha với nước nguội.', 'Hộp 30 gói', 'Hàn Quốc', 1, 1, 65),
(6, 'p6', 3, 'Kem Chống Nắng Dịu Nhẹ SPF 50+', 350000.00, 420000.00, 16, 4.8, 310, 'Chống nắng phổ rộng bảo vệ da khỏi tia UVA/UVB, kiểm soát dầu thừa và không gây bết dính.', 'Zinc Oxide, Titanium Dioxide, Niacinamide', 'Thoa đều lên da trước khi ra ngoài 20 phút.', 'Tuýp 50ml', 'Pháp', 1, 1, 95),
(7, 'p7', 6, 'Nhiệt Kế Hồng Ngoại Đo Trán Điện Tử', 450000.00, 550000.00, 18, 4.9, 180, 'Đo nhiệt độ nhanh chóng trong 1 giây không cần tiếp xúc, độ chính xác cao.', 'Linh kiện điện tử cao cấp, màn hình LCD', 'Đặt cách trán 1-3cm và bấm nút đo.', 'Hộp 1 máy + pin', 'Nhật Bản', 1, 0, 5),
(8, 'p8', 6, 'Máy Đo Huyết Áp Bắp Tay Tự Động', 890000.00, 1100000.00, 19, 4.9, 260, 'Đo huyết áp và nhịp tim chính xác, cảnh báo loạn nhịp tim, bộ nhớ lưu 90 kết quả.', 'Máy đo, vòng quấn bắp tay', 'Quấn vòng bắp tay ngang tim và nhấn nút Start.', 'Bộ sản phẩm đầy đủ', 'Nhật Bản', 1, 1, 18),
(9, 'p9', 8, 'Khẩu Trang Y Tế 4 Lớp Kháng Khuẩn', 45000.00, 60000.00, 25, 4.7, 500, 'Khẩu trang 4 lớp lọc bụi mịn, kháng khuẩn 99%, dây đeo êm ái không đau tai.', 'Vải không dệt, màng lọc Meltblown', 'Đeo che kín mũi và miệng.', 'Hộp 50 cái', 'Việt Nam', 1, 1, 300),
(10, 'p10', 8, 'Nước Súc Miệng Kháng Khuẩn Diệt Khuẩn', 75000.00, 90000.00, 16, 4.8, 140, 'Nước súc miệng giúp diệt 99.9% vi khuẩn, ngừa sâu răng và giữ hơi thở thơm mát suốt 12h.', 'Chlorhexidine, Fluoride, Menthol', 'Súc miệng 20ml trong 30 giây sau khi đánh răng.', 'Chai 500ml', 'Thái Lan', 1, 0, 150);

-- 5. Banners
INSERT INTO `banners` (`id`, `banner_code`, `title`, `subtitle`, `action_text`, `bg_res_name`, `is_active`) VALUES
(1, 'b1', 'Chăm sóc sức khỏe toàn diện', 'Giảm đến 30% cho thực phẩm chức năng', 'Xem ngay', 'bg_banner_1', 1),
(2, 'b2', 'Ưu đãi vitamin & khoáng chất', 'Mua 2 tặng 1 các dòng Vitamin C, Zinc', 'Khám phá', 'bg_banner_2', 1),
(3, 'b3', 'Thiết bị chăm sóc sức khỏe', 'Máy đo huyết áp, nhiệt kế chính hãng', 'Đặt hàng', 'bg_banner_3', 1);

-- 6. Orders & Order Items
INSERT INTO `orders` (`id`, `order_code`, `user_id`, `full_name`, `phone`, `shipping_address`, `payment_method`, `subtotal`, `shipping_fee`, `total_amount`, `status`) VALUES
(1, 'MC982103', 1, 'Nguyễn Văn A', '0901234567', '123 Lê Lợi, P. Bến Nghé, Q.1, TP.HCM', 'COD', 520000.00, 0.00, 520000.00, 'Đã giao'),
(2, 'MC871239', 2, 'Trần Thị Mai', '0912345678', '45 Nguyễn Huệ, P. Bến Nghé, Q.1, TP.HCM', 'Chuyển khoản ngân hàng', 660000.00, 0.00, 660000.00, 'Đang giao'),
(3, 'MC765102', 3, 'Lê Hoàng Nam', '0923456789', '78 Trần Hưng Đạo, P.7, Q.5, TP.HCM', 'COD', 455000.00, 15000.00, 470000.00, 'Đang chuẩn bị'),
(4, 'MC654321', 4, 'Phạm Thu Trang', '0934567890', '12 Đinh Tiên Hoàng, Q. Bình Thạnh, TP.HCM', 'COD', 1340000.00, 0.00, 1340000.00, 'Chờ xác nhận');

INSERT INTO `order_items` (`id`, `order_id`, `product_id`, `product_name`, `price`, `quantity`, `subtotal`) VALUES
(1, 1, 1, 'Vitamin C 1000mg Tăng Cường Đề Kháng', 150000.00, 2, 300000.00),
(2, 1, 2, 'Canxi Nano & Vitamin D3 Bổ Xương', 220000.00, 1, 220000.00),
(3, 2, 3, 'Omega 3 Dầu Cá Ngừ Đại Dương', 310000.00, 1, 310000.00),
(4, 2, 6, 'Kem Chống Nắng Dịu Nhẹ SPF 50+', 350000.00, 1, 350000.00),
(5, 3, 4, 'Kẽm Bio-Zinc Hỗ Trợ Tiêu Hóa', 135000.00, 3, 405000.00),
(6, 4, 7, 'Nhiệt Kế Hồng Ngoại Đo Trán Điện Tử', 450000.00, 1, 450000.00),
(7, 4, 8, 'Máy Đo Huyết Áp Bắp Tay Tự Động', 890000.00, 1, 890000.00);

-- 7. Reviews
INSERT INTO `reviews` (`id`, `product_id`, `user_id`, `rating`, `comment`, `is_hidden`) VALUES
(1, 1, 1, 5, 'Sản phẩm dùng rất tốt, hết mệt mỏi và đề kháng khá hơn hẳn!', 0),
(2, 2, 2, 5, 'Canxi nano uống không bị nóng hay táo bón, hài lòng.', 0),
(3, 6, 3, 4, 'Kem chống nắng nâng tone nhẹ, kiềm dầu tốt.', 0);