<?php
require_once 'config.php';

try {
    $stmt = $conn->prepare("SELECT banner_code AS id, title, subtitle, action_text AS actionText, bg_res_name AS bgResName FROM banners WHERE is_active = 1");
    $stmt->execute();
    $banners = $stmt->fetchAll();

    echo json_encode(array(
        "status" => true,
        "data" => $banners
    ), JSON_UNESCAPED_UNICODE);
} catch (PDOException $e) {
    echo json_encode(array(
        "status" => false,
        "message" => $e->getMessage()
    ));
}
?>