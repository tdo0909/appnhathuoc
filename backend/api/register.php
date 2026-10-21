<?php
require_once 'config.php';

$data = json_decode(file_get_contents("php://input"), true);
$fullName = isset($data['full_name']) ? trim($data['full_name']) : (isset($_POST['full_name']) ? trim($_POST['full_name']) : '');
$phone = isset($data['phone']) ? trim($data['phone']) : (isset($_POST['phone']) ? trim($_POST['phone']) : '');
$email = isset($data['email']) ? trim($data['email']) : (isset($_POST['email']) ? trim($_POST['email']) : '');
$password = isset($data['password']) ? trim($data['password']) : (isset($_POST['password']) ? trim($_POST['password']) : '');

if (empty($fullName) || empty($phone) || empty($password)) {
    echo json_encode(array("status" => false, "message" => "Vui lòng nhập đầy đủ họ tên, số điện thoại và mật khẩu"));
    exit();
}

try {
    // Check if phone already exists
    $checkStmt = $conn->prepare("SELECT id FROM users WHERE phone = :phone LIMIT 1");
    $checkStmt->execute(array(':phone' => $phone));
    if ($checkStmt->fetch()) {
        echo json_encode(array("status" => false, "message" => "Số điện thoại này đã được đăng ký tài khoản!"));
        exit();
    }

    // Insert new customer into MySQL
    $stmt = $conn->prepare("INSERT INTO users (full_name, phone, email, password, is_locked) VALUES (:name, :phone, :email, :pass, 0)");
    $stmt->execute(array(
        ':name' => $fullName,
        ':phone' => $phone,
        ':email' => $email,
        ':pass' => $password
    ));

    $userId = $conn->lastInsertId();

    echo json_encode(array(
        "status" => true,
        "message" => "Đăng ký tài khoản thành công!",
        "user" => array(
            "id" => (int)$userId,
            "name" => $fullName,
            "phone" => $phone,
            "email" => $email
        )
    ), JSON_UNESCAPED_UNICODE);

} catch (PDOException $e) {
    echo json_encode(array("status" => false, "message" => "Lỗi đăng ký: " . $e->getMessage()));
}
?>