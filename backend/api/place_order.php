<?php
require_once 'config.php';

$data = json_decode(file_get_contents("php://input"), true);

if (!$data) {
    echo json_encode(array("status" => false, "message" => "Dữ liệu không hợp lệ"));
    exit();
}

$fullName = isset($data['full_name']) ? trim($data['full_name']) : '';
$phone = isset($data['phone']) ? trim($data['phone']) : '';
$address = isset($data['address']) ? trim($data['address']) : '';
$paymentMethod = isset($data['payment_method']) ? trim($data['payment_method']) : 'COD';
$items = isset($data['items']) ? $data['items'] : array();
$subtotal = isset($data['subtotal']) ? (float)$data['subtotal'] : 0.0;
$shippingFee = isset($data['shipping_fee']) ? (float)$data['shipping_fee'] : 0.0;
$totalAmount = isset($data['total_amount']) ? (float)$data['total_amount'] : 0.0;

if (empty($fullName) || empty($phone) || empty($address) || empty($items)) {
    echo json_encode(array("status" => false, "message" => "Thiếu thông tin đơn hàng"));
    exit();
}

try {
    $conn->beginTransaction();

    $orderCode = "MC" . rand(100000, 999999);

    $stmt = $conn->prepare("INSERT INTO orders (order_code, full_name, phone, shipping_address, payment_method, subtotal, shipping_fee, total_amount, status) VALUES (:code, :name, :phone, :addr, :pay, :sub, :ship, :total, 'Chờ xác nhận')");
    $stmt->execute(array(
        ':code' => $orderCode,
        ':name' => $fullName,
        ':phone' => $phone,
        ':addr' => $address,
        ':pay' => $paymentMethod,
        ':sub' => $subtotal,
        ':ship' => $shippingFee,
        ':total' => $totalAmount
    ));

    $orderId = $conn->lastInsertId();

    $stmtItem = $conn->prepare("INSERT INTO order_items (order_id, product_name, price, quantity, subtotal) VALUES (:order_id, :pname, :price, :qty, :subtotal)");

    foreach ($items as $item) {
        $pName = isset($item['product_name']) ? $item['product_name'] : (isset($item['name']) ? $item['name'] : 'Sản phẩm');
        $price = isset($item['price']) ? (float)$item['price'] : 0;
        $qty = isset($item['quantity']) ? (int)$item['quantity'] : 1;
        $itemSubtotal = $price * $qty;

        $stmtItem->execute(array(
            ':order_id' => $orderId,
            ':pname' => $pName,
            ':price' => $price,
            ':qty' => $qty,
            ':subtotal' => $itemSubtotal
        ));
    }

    $conn->commit();

    echo json_encode(array(
        "status" => true,
        "message" => "Đặt hàng thành công",
        "order_code" => $orderCode,
        "order_id" => $orderId
    ), JSON_UNESCAPED_UNICODE);

} catch (Exception $e) {
    $conn->rollBack();
    echo json_encode(array("status" => false, "message" => "Lỗi đặt hàng: " . $e->getMessage()));
}
?>