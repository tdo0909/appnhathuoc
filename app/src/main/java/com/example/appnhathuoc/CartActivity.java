package com.example.appnhathuoc;

import com.example.appnhathuoc.utils.BottomNavigationHelper;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appnhathuoc.adapter.CartAdapter;
import com.example.appnhathuoc.model.CartItem;
import com.example.appnhathuoc.utils.CartManager;

import java.text.DecimalFormat;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private ImageView btnBack;
    private TextView btnClearAll, tvSubtotal, tvShippingFee, tvTotalAmount;
    private Button btnCheckout, btnShopNow;
    private RecyclerView rvCartItems;
    private LinearLayout layoutBottom, layoutEmpty;

    private CartAdapter adapter;
    private CartManager cartManager;
    private List<CartItem> cartItems;
    private DecimalFormat priceFormatter = new DecimalFormat("#,###đ");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        BottomNavigationHelper.attach(this, R.id.navigation_category);

        cartManager = CartManager.getInstance(this);

        initViews();
        setupRecyclerView();
        setupListeners();
        refreshCart();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back_cart);
        btnClearAll = findViewById(R.id.btn_clear_cart_all);
        tvSubtotal = findViewById(R.id.tv_cart_subtotal);
        tvShippingFee = findViewById(R.id.tv_cart_shipping_fee);
        tvTotalAmount = findViewById(R.id.tv_cart_total_amount);
        btnCheckout = findViewById(R.id.btn_proceed_checkout);
        btnShopNow = findViewById(R.id.btn_shop_now);
        rvCartItems = findViewById(R.id.rv_cart_items);
        layoutBottom = findViewById(R.id.layout_cart_bottom);
        layoutEmpty = findViewById(R.id.layout_empty_cart);
    }

    private void setupRecyclerView() {
        cartItems = cartManager.getCartItems();
        adapter = new CartAdapter(cartItems, new CartAdapter.OnCartItemChangeListener() {
            @Override
            public void onQuantityChange(CartItem item, int newQuantity) {
                if (item.getProduct() != null) {
                    cartManager.updateQuantity(item.getProduct().getId(), newQuantity);
                    refreshCart();
                }
            }

            @Override
            public void onDeleteItem(CartItem item) {
                if (item.getProduct() != null) {
                    cartManager.removeProduct(item.getProduct().getId());
                    refreshCart();
                    Toast.makeText(CartActivity.this, "Đã xóa sản phẩm khỏi giỏ hàng", Toast.LENGTH_SHORT).show();
                }
            }
        });
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        rvCartItems.setAdapter(adapter);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnClearAll.setOnClickListener(v -> {
            if (cartManager.getCartCount() == 0) return;
            new AlertDialog.Builder(CartActivity.this)
                    .setTitle("Xóa giỏ hàng")
                    .setMessage("Bạn có chắc chắn muốn xóa tất cả sản phẩm trong giỏ hàng?")
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        cartManager.clearCart();
                        refreshCart();
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        btnShopNow.setOnClickListener(v -> finish());

        btnCheckout.setOnClickListener(v -> {
            if (cartManager.getCartCount() == 0) {
                Toast.makeText(CartActivity.this, "Giỏ hàng đang trống", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(new Intent(CartActivity.this, CheckoutActivity.class));
        });
    }

    private void refreshCart() {
        cartItems = cartManager.getCartItems();
        adapter.updateList(cartItems);

        if (cartItems.isEmpty()) {
            layoutEmpty.setVisibility(View.VISIBLE);
            rvCartItems.setVisibility(View.GONE);
            layoutBottom.setVisibility(View.GONE);
        } else {
            layoutEmpty.setVisibility(View.GONE);
            rvCartItems.setVisibility(View.VISIBLE);
            layoutBottom.setVisibility(View.VISIBLE);

            double subtotal = cartManager.getTotalAmount();
            double shippingFee = subtotal >= 300000 ? 0 : 15000;
            double total = subtotal + shippingFee;

            tvSubtotal.setText(priceFormatter.format(subtotal));
            if (shippingFee == 0) {
                tvShippingFee.setText("Miễn phí");
            } else {
                tvShippingFee.setText(priceFormatter.format(shippingFee));
            }
            tvTotalAmount.setText(priceFormatter.format(total));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCart();
    }
}