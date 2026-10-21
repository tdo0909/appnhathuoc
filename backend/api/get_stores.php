<?php
require_once 'config.php';

try {
    $sql = "SELECT id, name, address, province, phone, openingHours FROM (
        SELECT 1 AS id, 'MEDCARE Nhà Thuốc Bến Nghé' AS name, '123 Lê Lợi, P. Bến Nghé, Q.1, TP.HCM' AS address, 'TP. Hồ Chí Minh' AS province, '028 3821 1234' AS phone, '06:00 - 22:30' AS openingHours
        UNION ALL SELECT 2, 'MEDCARE Nhà Thuốc Hai Bà Trưng', '248 Hai Bà Trưng, P. Tân Định, Q.1, TP.HCM', 'TP. Hồ Chí Minh', '028 3822 5678', '06:30 - 22:00'
        UNION ALL SELECT 3, 'MEDCARE Nhà Thuốc Nguyễn Trãi', '356 Nguyễn Trãi, P.8, Q.5, TP.HCM', 'TP. Hồ Chí Minh', '028 3838 9101', '06:00 - 22:00'
        UNION ALL SELECT 4, 'MEDCARE Nhà Thuốc Ba Tháng Hai', '120 Đường 3/2, P.12, Q.10, TP.HCM', 'TP. Hồ Chí Minh', '028 3865 1122', '06:30 - 22:30'
        UNION ALL SELECT 5, 'MEDCARE Nhà Thuốc Đinh Tiên Hoàng', '45 Đinh Tiên Hoàng, P.1, Q. Bình Thạnh, TP.HCM', 'TP. Hồ Chí Minh', '028 3510 3344', '06:00 - 22:00'
        UNION ALL SELECT 6, 'MEDCARE Nhà Thuốc Lĩnh Nam', '186 Lĩnh Nam, P. Vĩnh Hưng, Q. Hoàng Mai, Hà Nội', 'Hà Nội', '024 3644 8899', '06:00 - 22:30'
        UNION ALL SELECT 7, 'MEDCARE Nhà Thuốc Trương Định', '312 Trương Định, Q. Hoàng Mai, Hà Nội', 'Hà Nội', '024 3662 1122', '06:30 - 22:00'
        UNION ALL SELECT 8, 'MEDCARE Nhà Thuốc Kim Mã', '182 Kim Mã, Q. Ba Đình, Hà Nội', 'Hà Nội', '024 3845 5566', '06:30 - 22:00'
        UNION ALL SELECT 9, 'MEDCARE Nhà Thuốc Cầu Giấy', '235 Cầu Giấy, Q. Cầu Giấy, Hà Nội', 'Hà Nội', '024 3767 7788', '06:00 - 22:30'
        UNION ALL SELECT 10, 'MEDCARE Nhà Thuốc Tôn Đức Thắng', '85 Tôn Đức Thắng, Q. Đống Đa, Hà Nội', 'Hà Nội', '024 3851 3344', '06:00 - 22:00'
        UNION ALL SELECT 11, 'MEDCARE Nhà Thuốc Bạch Mai', '418 Bạch Mai, Q. Hai Bà Trưng, Hà Nội', 'Hà Nội', '024 3863 5566', '06:30 - 22:00'
        UNION ALL SELECT 12, 'MEDCARE Nhà Thuốc Nguyễn Văn Linh', '150 Nguyễn Văn Linh, Q. Thanh Khê, Đà Nẵng', 'Đà Nẵng', '0236 365 9900', '06:30 - 22:00'
        UNION ALL SELECT 13, 'MEDCARE Nhà Thuốc Mậu Thân', '88 Mậu Thân, Q. Ninh Kiều, Cần Thơ', 'Cần Thơ', '0292 383 1212', '06:00 - 22:00'
        UNION ALL SELECT 14, 'MEDCARE Nhà Thuốc Lê Hồng Phong', '102 Lê Hồng Phong, TP. Nha Trang', 'Khánh Hòa', '0258 387 3434', '06:30 - 22:00'
    ) AS stores_tbl";

    $stmt = $conn->prepare($sql);
    $stmt->execute();
    $stores = $stmt->fetchAll();

    echo json_encode(array("status" => true, "data" => $stores), JSON_UNESCAPED_UNICODE);
} catch (PDOException $e) {
    echo json_encode(array("status" => false, "message" => $e->getMessage()));
}
?>