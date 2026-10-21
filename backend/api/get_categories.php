<?php
require_once 'config.php';

try {
    $stmt = $conn->prepare("SELECT category_code AS id, name, icon_res_id AS iconResId FROM categories ORDER BY id ASC");
    $stmt->execute();
    $categories = $stmt->fetchAll();

    echo json_encode(array(
        "status" => true,
        "data" => $categories
    ), JSON_UNESCAPED_UNICODE);
} catch (PDOException $e) {
    echo json_encode(array(
        "status" => false,
        "message" => $e->getMessage()
    ));
}
?>