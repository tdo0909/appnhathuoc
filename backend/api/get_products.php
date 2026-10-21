<?php
require_once 'config.php';

try {
    $sql = "SELECT p.product_code AS id, p.name, p.price, p.old_price AS oldPrice,
                   p.discount_percent AS discountPercent, p.rating, p.review_count AS reviewCount,
                   c.category_code AS categoryId, c.name AS categoryName,
                   p.description, p.ingredient, p.usage_info AS `usage`,
                   p.specification, p.origin, p.is_bestseller AS isBestSeller,
                   p.is_promotion AS isPromotion, p.image_res_id AS imageResId
            FROM products p
            JOIN categories c ON p.category_id = c.id
            WHERE 1=1";

    $params = array();

    if (!empty($_GET['category_id'])) {
        $sql .= " AND c.category_code = :cat_id";
        $params[':cat_id'] = $_GET['category_id'];
    }

    if (isset($_GET['is_bestseller']) && $_GET['is_bestseller'] == '1') {
        $sql .= " AND p.is_bestseller = 1";
    }

    if (isset($_GET['is_promotion']) && $_GET['is_promotion'] == '1') {
        $sql .= " AND p.is_promotion = 1";
    }

    if (!empty($_GET['search'])) {
        $sql .= " AND (LOWER(p.name) LIKE :search OR LOWER(c.name) LIKE :search OR LOWER(p.description) LIKE :search)";
        $params[':search'] = '%' . strtolower(trim($_GET['search'])) . '%';
    }

    if (!empty($_GET['sort'])) {
        $sort = $_GET['sort'];
        if ($sort == 'price_asc') {
            $sql .= " ORDER BY p.price ASC";
        } else if ($sort == 'price_desc') {
            $sql .= " ORDER BY p.price DESC";
        } else if ($sort == 'rating') {
            $sql .= " ORDER BY p.rating DESC";
        } else {
            $sql .= " ORDER BY p.id ASC";
        }
    } else {
        $sql .= " ORDER BY p.id ASC";
    }

    $stmt = $conn->prepare($sql);
    $stmt->execute($params);
    $products = $stmt->fetchAll();

    // Convert numeric strings to proper types
    foreach ($products as &$p) {
        $p['price'] = (float)$p['price'];
        $p['oldPrice'] = (float)$p['oldPrice'];
        $p['discountPercent'] = (int)$p['discountPercent'];
        $p['rating'] = (float)$p['rating'];
        $p['reviewCount'] = (int)$p['reviewCount'];
        $p['isBestSeller'] = (bool)$p['isBestSeller'];
        $p['isPromotion'] = (bool)$p['isPromotion'];
        $p['imageResId'] = (int)$p['imageResId'];
    }

    echo json_encode(array(
        "status" => true,
        "data" => $products
    ), JSON_UNESCAPED_UNICODE);
} catch (PDOException $e) {
    echo json_encode(array(
        "status" => false,
        "message" => $e->getMessage()
    ));
}
?>