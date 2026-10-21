<?php
require_once 'config.php';

$phone = isset($_GET['phone']) ? trim($_GET['phone']) : '';

try {
    if (!empty($phone)) {
        $stmt = $conn->prepare("SELECT id, order_code AS orderId, DATE_FORMAT(created_at, '%d/%m/%Y %H:%i') AS date, full_name AS fullName, phone, shipping_address AS address, payment_method AS paymentMethod, subtotal, shipping_fee AS shippingFee, total_amount AS totalAmount, status FROM orders WHERE phone = :phone ORDER BY id DESC");
        $stmt->execute(array(':phone' => $phone));
    } else {
        $stmt = $conn->prepare("SELECT id, order_code AS orderId, DATE_FORMAT(created_at, '%d/%m/%Y %H:%i') AS date, full_name AS fullName, phone, shipping_address AS address, payment_method AS paymentMethod, subtotal, shipping_fee AS shippingFee, total_amount AS totalAmount, status FROM orders ORDER BY id DESC LIMIT 50");
        $stmt->execute();
    }

    $orders = $stmt->fetchAll();

    foreach ($orders as &$order) {
        $order['subtotal'] = (float)$order['subtotal'];
        $order['shippingFee'] = (float)$order['shippingFee'];
        $order['totalAmount'] = (float)$order['totalAmount'];

        $stmtItems = $conn->prepare("SELECT product_name AS name, price, quantity, subtotal FROM order_items WHERE order_id = :order_id");
        $stmtItems->execute(array(':order_id' => $order['id']));
        $rawItems = $stmtItems->fetchAll();

        $items = array();
        foreach ($rawItems as $ri) {
            $items[] = array(
                "product" => array(
                    "id" => "p1",
                    "name" => $ri['name'],
                    "price" => (float)$ri['price'],
                    "imageResId" => 2131230868
                ),
                "quantity" => (int)$ri['quantity']
            );
        }
        $order['items'] = $items;
    }

    echo json_encode(array(
        "status" => true,
        "data" => $orders
    ), JSON_UNESCAPED_UNICODE);

} catch (PDOException $e) {
    echo json_encode(array("status" => false, "message" => $e->getMessage()));
}
?>