package com.example.appnhathuoc.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.appnhathuoc.data.DataRepository;
import com.example.appnhathuoc.model.CartItem;
import com.example.appnhathuoc.model.Order;
import com.example.appnhathuoc.model.Product;

import java.util.ArrayList;
import java.util.List;

public class OrderManager {

    private static final String PREF_NAME = "medcare_orders_pref";
    private static final String KEY_ORDERS_DATA = "orders_data";

    private static OrderManager instance;
    private final SharedPreferences prefs;

    private OrderManager(Context context) {
        prefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized OrderManager getInstance(Context context) {
        if (instance == null) {
            instance = new OrderManager(context);
        }
        return instance;
    }

    public List<Order> getOrders() {
        List<Order> savedOrders = getSavedOrders();
        return savedOrders.isEmpty() ? getDemoOrders() : savedOrders;
    }

    public List<Order> getSavedOrders() {
        List<Order> list = new ArrayList<>();
        String raw = prefs.getString(KEY_ORDERS_DATA, "");
        if (raw == null || raw.trim().isEmpty()) {
            return list;
        }

        // Format: orderId|date|totalAmount|status|fullName|phone|address|paymentMethod|p1:q1,p2:q2#orderId2...
        String[] ordersArr = raw.split("#");
        for (String orderStr : ordersArr) {
            if (!orderStr.trim().isEmpty()) {
                String[] parts = orderStr.split("\\|");
                if (parts.length >= 9) {
                    String oId = parts[0];
                    String date = parts[1];
                    double total = Double.parseDouble(parts[2]);
                    String status = parts[3];
                    String name = parts[4];
                    String phone = parts[5];
                    String addr = parts[6];
                    String payMethod = parts[7];
                    String itemsStr = parts[8];

                    List<CartItem> cartItems = new ArrayList<>();
                    String[] itemTokens = itemsStr.split(",");
                    for (String tok : itemTokens) {
                        String[] pair = tok.split(":");
                        if (pair.length == 2) {
                            Product p = DataRepository.getProductById(pair[0]);
                            int q = Integer.parseInt(pair[1]);
                            if (p != null) cartItems.add(new CartItem(p, q));
                        }
                    }

                    list.add(new Order(oId, date, cartItems, total, status, name, phone, addr, payMethod));
                }
            }
        }

        return list;
    }

    public void addOrder(Order newOrder) {
        List<Order> list = getSavedOrders();
        list.add(0, newOrder);
        saveOrders(list);
    }

    private void saveOrders(List<Order> list) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            Order o = list.get(i);
            sb.append(o.getOrderId()).append("|")
              .append(o.getDate()).append("|")
              .append(o.getTotalAmount()).append("|")
              .append(o.getStatus()).append("|")
              .append(o.getFullName()).append("|")
              .append(o.getPhone()).append("|")
              .append(o.getAddress()).append("|")
              .append(o.getPaymentMethod()).append("|");

            StringBuilder itemSb = new StringBuilder();
            if (o.getItems() != null) {
                for (CartItem ci : o.getItems()) {
                    if (ci.getProduct() != null) {
                        itemSb.append(ci.getProduct().getId()).append(":").append(ci.getQuantity()).append(",");
                    }
                }
            }
            sb.append(itemSb.toString()).append("#");
        }
        prefs.edit().putString(KEY_ORDERS_DATA, sb.toString()).apply();
    }

    public List<Order> getDemoOrders() {
        List<Order> list = new ArrayList<>();
        List<Product> allP = DataRepository.getProducts();

        list.add(createDemoOrder("MC982103", "12/05/2024", "Nguyễn Văn An", "0901234567", "123 Lê Lợi, Q.1, TP.HCM", "COD", "Đã giao", allP.get(0), 2, allP.get(1), 1));
        list.add(createDemoOrder("MC871239", "10/05/2024", "Trần Thị Mai", "0912345678", "45 Nguyễn Huệ, Q.1, TP.HCM", "Chuyển khoản", "Đang giao", allP.get(2), 1, allP.get(5), 1));
        list.add(createDemoOrder("MC765102", "08/05/2024", "Lê Hoàng Nam", "0923456789", "78 Trần Hưng Đạo, Q.5, TP.HCM", "COD", "Đang chuẩn bị", allP.get(3), 3, allP.get(8), 2));
        list.add(createDemoOrder("MC654321", "05/05/2024", "Phạm Thu Trang", "0934567890", "12 Đinh Tiên Hoàng, Q.Bình Thạnh, TP.HCM", "COD", "Chờ xác nhận", allP.get(6), 1, allP.get(7), 1));
        list.add(createDemoOrder("MC543210", "01/05/2024", "Vũ Minh Khang", "0945678901", "99 CMT8, Q.3, TP.HCM", "Chuyển khoản", "Đã giao", allP.get(4), 2, allP.get(9), 1));
        list.add(createDemoOrder("MC432109", "28/04/2024", "Hoàng Ngọc Anh", "0956789012", "15 Ba Tháng Hai, Q.10, TP.HCM", "COD", "Đã hủy", allP.get(10), 1, allP.get(11), 1));
        list.add(createDemoOrder("MC321098", "25/04/2024", "Đặng Quốc Bảo", "0967890123", "202 Điện Biên Phủ, Q.3, TP.HCM", "COD", "Đã giao", allP.get(12), 2, allP.get(13), 1));
        list.add(createDemoOrder("MC210987", "20/04/2024", "Bùi Khánh Linh", "0978901234", "50 Phạm Ngọc Thạch, Q.3, TP.HCM", "Chuyển khoản", "Đã giao", allP.get(14), 1, allP.get(15), 1));
        list.add(createDemoOrder("MC109876", "15/04/2024", "Đỗ Đức Minh", "0989012345", "88 Nguyễn Thị Minh Khai, Q.1, TP.HCM", "COD", "Đã giao", allP.get(16), 2, allP.get(17), 2));
        list.add(createDemoOrder("MC098765", "10/04/2024", "Nông Văn Hùng", "0990123456", "33 Võ Văn Tần, Q.3, TP.HCM", "COD", "Đã giao", allP.get(18), 3, allP.get(19), 1));

        return list;
    }

    private Order createDemoOrder(String oId, String date, String name, String phone, String addr, String payMethod, String status, Product p1, int q1, Product p2, int q2) {
        List<CartItem> items = new ArrayList<>();
        items.add(new CartItem(p1, q1));
        items.add(new CartItem(p2, q2));
        double total = p1.getPrice() * q1 + p2.getPrice() * q2 + 15000;
        return new Order(oId, date, items, total, status, name, phone, addr, payMethod);
    }
}