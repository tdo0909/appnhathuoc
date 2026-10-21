package com.example.appnhathuoc;

import com.example.appnhathuoc.utils.BottomNavigationHelper;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appnhathuoc.adapter.ReviewAdapter;
import com.example.appnhathuoc.api.ApiClient;
import com.example.appnhathuoc.model.Product;
import com.example.appnhathuoc.model.ReviewItem;
import com.example.appnhathuoc.utils.CartManager;
import com.example.appnhathuoc.utils.UserManager;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    private ImageView btnBack, imgProduct, btnMinusQty, btnPlusQty;
    private FrameLayout btnCart;
    private TextView tvCartBadge, tvDiscount, tvName, tvRating, tvReviews, tvCategory, tvPrice, tvOldPrice, tvQty;
    private TextView tvSpec, tvOrigin, tvDescription, tvIngredient, tvUsage, tvContraindications;
    private Button btnAddToCart, btnBuyNow, btnWriteReview;
    private RecyclerView rvReviews;

    private Product product;
    private int quantity = 1;
    private CartManager cartManager;
    private UserManager userManager;
    private DecimalFormat priceFormatter = new DecimalFormat("#,###đ");
    private ReviewAdapter reviewAdapter;
    private List<ReviewItem> reviewList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        BottomNavigationHelper.attach(this, R.id.navigation_category);

        cartManager = CartManager.getInstance(this);
        userManager = UserManager.getInstance(this);

        product = (Product) getIntent().getSerializableExtra("PRODUCT");
        if (product == null) {
            finish();
            return;
        }

        initViews();
        bindProductData();
        setupListeners();
        setupReviews();
        updateCartBadge();
    }

    private void initViews() {
        btnBack = findViewById(R.id.btn_back_detail);
        btnCart = findViewById(R.id.btn_cart_detail);
        tvCartBadge = findViewById(R.id.tv_cart_badge_detail);
        imgProduct = findViewById(R.id.img_detail_product);
        tvDiscount = findViewById(R.id.tv_detail_discount);
        tvName = findViewById(R.id.tv_detail_name);
        tvRating = findViewById(R.id.tv_detail_rating);
        tvReviews = findViewById(R.id.tv_detail_reviews);
        tvCategory = findViewById(R.id.tv_detail_category);
        tvPrice = findViewById(R.id.tv_detail_price);
        tvOldPrice = findViewById(R.id.tv_detail_old_price);
        tvQty = findViewById(R.id.tv_detail_qty);
        btnMinusQty = findViewById(R.id.btn_minus_qty);
        btnPlusQty = findViewById(R.id.btn_plus_qty);
        tvSpec = findViewById(R.id.tv_detail_spec);
        tvOrigin = findViewById(R.id.tv_detail_origin);
        tvDescription = findViewById(R.id.tv_detail_description);
        tvIngredient = findViewById(R.id.tv_detail_ingredient);
        tvUsage = findViewById(R.id.tv_detail_usage);
        tvContraindications = findViewById(R.id.tv_detail_contraindications);
        btnAddToCart = findViewById(R.id.btn_add_to_cart_detail);
        btnBuyNow = findViewById(R.id.btn_buy_now_detail);
        btnWriteReview = findViewById(R.id.btn_write_review);
        rvReviews = findViewById(R.id.rv_product_reviews);
    }

    private void bindProductData() {
        if (product.getImageUrl() != null && !product.getImageUrl().trim().isEmpty()) {
            com.example.appnhathuoc.utils.ImageUtils.loadImage(imgProduct, product.getImageUrl(), product.getImageResId());
        } else {
            imgProduct.setImageResource(product.getImageResId());
        }
        tvName.setText(product.getName());
        tvPrice.setText(priceFormatter.format(product.getPrice()));

        if (product.getOldPrice() > product.getPrice()) {
            tvOldPrice.setVisibility(View.VISIBLE);
            tvOldPrice.setText(priceFormatter.format(product.getOldPrice()));
            tvOldPrice.setPaintFlags(tvOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            tvOldPrice.setVisibility(View.GONE);
        }

        if (product.getDiscountPercent() > 0) {
            tvDiscount.setVisibility(View.VISIBLE);
            tvDiscount.setText("-" + product.getDiscountPercent() + "%");
        } else {
            tvDiscount.setVisibility(View.GONE);
        }

        tvRating.setText(String.valueOf(product.getRating()));
        tvReviews.setText("(" + product.getReviewCount() + " đánh giá)");
        tvCategory.setText("Danh mục: " + product.getCategoryName());

        tvSpec.setText(product.getSpecification());
        tvOrigin.setText(product.getOrigin());
        tvDescription.setText(product.getDescription());
        tvIngredient.setText(product.getIngredient());
        tvUsage.setText(product.getUsage());
        if (tvContraindications != null && product.getContraindication() != null && !product.getContraindication().isEmpty()) {
            tvContraindications.setText(product.getContraindication());
        }
        tvQty.setText(String.valueOf(quantity));
    }

    private void setupReviews() {
        reviewAdapter = new ReviewAdapter(reviewList);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setAdapter(reviewAdapter);

        loadReviews();
        refreshReviewPermission();
    }

    private void loadReviews() {
        ApiClient.fetchReviews(product.getId(), new ApiClient.ApiCallback<List<ReviewItem>>() {
            @Override
            public void onSuccess(List<ReviewItem> result) {
                if (result != null && !result.isEmpty()) {
                    reviewList = result;
                    reviewAdapter.updateList(reviewList);
                } else {
                    reviewList.clear();
                    reviewAdapter.updateList(reviewList);
                }
            }

            @Override
            public void onError(String errorMessage) {}
        });
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());

        btnCart.setOnClickListener(v -> startActivity(new Intent(ProductDetailActivity.this, CartActivity.class)));

        btnMinusQty.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQty.setText(String.valueOf(quantity));
            }
        });

        btnPlusQty.setOnClickListener(v -> {
            quantity++;
            tvQty.setText(String.valueOf(quantity));
        });

        btnAddToCart.setOnClickListener(v -> {
            cartManager.addProduct(product, quantity);
            updateCartBadge();
            Toast.makeText(ProductDetailActivity.this, "Đã thêm " + quantity + " sản phẩm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        });

        btnBuyNow.setOnClickListener(v -> {
            cartManager.addProduct(product, quantity);
            startActivity(new Intent(ProductDetailActivity.this, CartActivity.class));
        });

        btnWriteReview.setOnClickListener(v -> {
            if (!userManager.isLoggedIn()) {
                Toast.makeText(this, "Đăng nhập và nhận sản phẩm để viết đánh giá.", Toast.LENGTH_LONG).show();
                startActivity(new Intent(this, LoginActivity.class));
                return;
            }
            checkReviewPermission(true);
        });
    }

    private void refreshReviewPermission() {
        if (!userManager.isLoggedIn()) {
            btnWriteReview.setEnabled(false);
            btnWriteReview.setText("Đăng nhập và nhận hàng để đánh giá");
            return;
        }
        checkReviewPermission(false);
    }

    private void checkReviewPermission(boolean openDialogWhenAllowed) {
        btnWriteReview.setEnabled(false);
        btnWriteReview.setText("Đang kiểm tra đơn hàng...");
        ApiClient.checkDeliveredPurchase(userManager.getUserPhone(), product.getId(), product.getName(),
                new ApiClient.ApiCallback<String>() {
                    @Override
                    public void onSuccess(String orderId) {
                        boolean eligible = orderId != null && !orderId.trim().isEmpty();
                        btnWriteReview.setEnabled(eligible);
                        btnWriteReview.setText(eligible ? "Viết đánh giá" : "Chỉ đánh giá sau khi nhận hàng");
                        if (openDialogWhenAllowed) {
                            if (eligible) {
                                showWriteReviewDialog();
                            } else {
                                Toast.makeText(ProductDetailActivity.this,
                                        "Bạn cần nhận sản phẩm trong đơn hàng trước khi đánh giá.",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    }

                    @Override
                    public void onError(String errorMessage) {
                        btnWriteReview.setEnabled(false);
                        btnWriteReview.setText("Chưa kiểm tra được đơn hàng");
                        if (openDialogWhenAllowed) {
                            Toast.makeText(ProductDetailActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void showWriteReviewDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Viết đánh giá sản phẩm");

        View view = getLayoutInflater().inflate(R.layout.dialog_write_review, null);
        RatingBar ratingBar = view.findViewById(R.id.rb_review_stars);
        EditText etComment = view.findViewById(R.id.et_review_comment);

        builder.setView(view);
        builder.setPositiveButton("Gửi đánh giá", (dialog, which) -> {
            int rating = Math.max(1, (int) ratingBar.getRating());
            String comment = etComment.getText().toString().trim();

            if (comment.isEmpty()) {
                Toast.makeText(ProductDetailActivity.this, "Vui lòng nhập nội dung đánh giá", Toast.LENGTH_SHORT).show();
                return;
            }

            String phone = userManager.getUserPhone();
            ApiClient.submitReview(product.getId(), product.getName(), phone, rating, comment,
                    new ApiClient.ApiCallback<org.json.JSONObject>() {
                @Override
                public void onSuccess(org.json.JSONObject result) {
                    Toast.makeText(ProductDetailActivity.this, "Gửi đánh giá thành công!", Toast.LENGTH_SHORT).show();
                    loadReviews();
                }

                @Override
                public void onError(String errorMessage) {
                    Toast.makeText(ProductDetailActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                }
            });
        });

        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void updateCartBadge() {
        int count = cartManager.getCartCount();
        if (count > 0) {
            tvCartBadge.setVisibility(View.VISIBLE);
            tvCartBadge.setText(String.valueOf(count));
        } else {
            tvCartBadge.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge();
        refreshReviewPermission();
    }
}