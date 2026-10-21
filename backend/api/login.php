<?php
require_once 'config.php';

$data = json_decode(file_get_contents("php://input"), true);
$phone = isset($data['phone']) ? trim($data['phone']) : (isset($_POST['phone']) ? trim($_POST['phone']) : '');
$password = isset($data['password']) ? trim($data['password']) : (isset($_POST['password']) ? trim($_POST['password']) : '');

if (empty($phone) || empty($password)) {
    echo json_encode(array("status" => false, "message" => "Vui lòng nhập số điện thoại và mật khẩu"));
    exit();
}

try {
    $stmt = $conn->prepare("SELECT id, full_name, phone, email, is_locked FROM users WHERE phone = :phone AND password = :password");
    $stmt->execute(array(':phone' => $phone, ':password' => $password));
    $user = $stmt->fetch();

    if ($user) {
        if ($user['is_locked'] == 1) {
            echo json_encode(array("status" => false, "message" => "Tài khoản của bạn đã bị khóa bởi quản trị viên!"));
            exit();
        }

        echo json_encode(array(
            "status" => true,
            "message" => "Đăng nhập thành công",
            "user" => array(
                "id" => (int)$user['id'],
                "name" => $user['full_name'],
                "phone" => $user['phone'],
                "email" => $user['email']
            )
        ), JSON_UNESCAPED_UNICODE);
    } else {
        echo json_encode(array("status" => false, "message" => "Số điện thoại hoặc mật khẩu không chính xác"));
    }
} catch (PDOException $e) {
    echo json_encode(array("status" => false, "message" => $e->getMessage()));
}
?>