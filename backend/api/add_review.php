<?php
require_once 'config.php';

$data = json_decode(file_get_contents("php://input"), true);

$product_code = isset($data['product_code']) ? trim($data['product_code']) : '';
$user_phone = isset($data['phone']) ? trim($data['phone']) : '';
$rating = isset($data['rating']) ? (int)$data['rating'] : 5;
$comment = isset($data['comment']) ? trim($data['comment']) : '';

if (empty($product_code) || empty($comment)) {
    echo json_encode(array("status" => false, "message" => "Vui lòng nhập nội dung đánh giá"));
    exit();
}

try {
    // 1. Get product_id
    $stmt_p = $conn->prepare("SELECT id FROM products WHERE product_code = :code LIMIT 1");
    $stmt_p->execute([':code' => $product_code]);
    $p = $stmt_p->fetch();

    if (!$p) {
        echo json_encode(array("status" => false, "message" => "Sản phẩm không tồn tại"));
        exit();
    }
    $product_id = (int)$p['id'];

    // 2. Get user_id
    $user_id = 1; // Default fallback user
    if (!empty($user_phone)) {
        $stmt_u = $conn->prepare("SELECT id FROM users WHERE phone = :phone LIMIT 1");
        $stmt_u->execute([':phone' => $user_phone]);
        $u = $stmt_u->fetch();
        if ($u) {
            $user_id = (int)$u['id'];
        }
    }

    // 3. Insert review
    $stmt_ins = $conn->prepare("INSERT INTO reviews (product_id, user_id, rating, comment, is_hidden) VALUES (:pid, :uid, :rating, :comment, 0)");
    $stmt_ins->execute([
        ':pid' => $product_id,
        ':uid' => $user_id,
        ':rating' => $rating,
        ':comment' => $comment
    ]);

    // 4. Update average rating and review_count in products table
    $stmt_avg = $conn->prepare("SELECT AVG(rating) as avg_rating, COUNT(*) as total_reviews FROM reviews WHERE product_id = :pid AND is_hidden = 0");
    $stmt_avg->execute([':pid' => $product_id]);
    $avg_data = $stmt_avg->fetch();

    $new_avg = round((float)$avg_data['avg_rating'], 1);
    $new_count = (int)$avg_data['total_reviews'];

    $stmt_upd = $conn->prepare("UPDATE products SET rating = :avg_rating, review_count = :rev_count WHERE id = :pid");
    $stmt_upd->execute([':avg_rating' => $new_avg, ':rev_count' => $new_count, ':pid' => $product_id]);

    echo json_encode(array(
        "status" => true,
        "message" => "Gửi đánh giá thành công! Cảm ơn nhận xét của bạn.",
        "new_rating" => $new_avg,
        "new_review_count" => $new_count
    ), JSON_UNESCAPED_UNICODE);

} catch (PDOException $e) {
    echo json_encode(array("status" => false, "message" => "Lỗi gửi đánh giá: " . $e->getMessage()));
}
?>