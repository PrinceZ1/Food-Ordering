package com.princez1.foodapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.widget.Toast;

import com.princez1.foodapp.adapter.CartAdapter;
import com.princez1.foodapp.databinding.ActivityCartBinding;
import com.princez1.foodapp.helper.ManagmentCart;

import java.util.Arrays;
import java.util.List;

public class CartActivity extends BaseActivity {
    private ActivityCartBinding binding;
    private RecyclerView.Adapter adapter;
    private ManagmentCart managementCart;
    private double discount = 0.0;
    private String appliedCouponCode = null;
    private static class CouponRule {
        final String code;
        final double minOrder;
        final double percent;
        final double maxDiscount;

        CouponRule(String code, double minOrder, double percent, double maxDiscount) {
            this.code = code;
            this.minOrder = minOrder;
            this.percent = percent;
            this.maxDiscount = maxDiscount;
        }

        double calculateDiscount(double subtotal) {
            if (subtotal >= minOrder) {
                double discount = Math.round(subtotal * percent * 100.0) / 100.0;
                return Math.min(discount, maxDiscount);
            }
            return 0.0;
        }
    }
    private static final List<CouponRule> COUPON_RULES = Arrays.asList(
            new CouponRule("WELCOME10", 20, 0.10, 50),
            new CouponRule("SAVE20", 50, 0.20, 100),
            new CouponRule("VIP30", 100, 0.30, 150)
    );

    private double totalAmountFromCartScreen; 

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        managementCart = new ManagmentCart(this);
        calculateCart();

        setVariable();
        calculateCart();

        initList();
    }

    private void calculateCart() {
        double delivery = 10;
        double subtotal = managementCart.getTotalFee();
        double validatedDiscount = appliedCouponCode != null ? getValidatedDiscount(appliedCouponCode, subtotal) : 0.0;
        if (appliedCouponCode != null && validatedDiscount == 0.0) {
            Toast.makeText(this, "Coupon đã bị gỡ do không còn hợp lệ!", Toast.LENGTH_SHORT).show();
            appliedCouponCode = null;
        }
        discount = validatedDiscount;

        double discountedSubtotal = subtotal - discount;
        if (discountedSubtotal < 0) discountedSubtotal = 0;
        double total = Math.round((discountedSubtotal + delivery) * 100) / 100.0;

        binding.totalFeeTxt.setText("$" + subtotal);
        binding.discountTxt.setText("$" + discount);
        binding.deliveryTxt.setText("$" + delivery);
        binding.totalTxt.setText("$" + total);

        // Lưu lại tổng tiền cuối cùng từ màn hình giỏ hàng
        totalAmountFromCartScreen = total;
    }

    private void setVariable() {
        binding.backBtn.setOnClickListener(v -> finish());
        binding.applyCouponBtn.setOnClickListener(v -> handleCouponApplication());
    }

    private void handleCouponApplication() {
        String couponCode = binding.couponEdt.getText().toString().trim().toUpperCase();
        double subtotal = managementCart.getTotalFee();
        if (appliedCouponCode != null && !appliedCouponCode.equals(couponCode)) {
            Toast.makeText(this, "Coupon cũ đã bị thay thế bởi coupon mới!", Toast.LENGTH_SHORT).show();
        }

        appliedCouponCode = null;
        discount = 0.0;

        for (CouponRule rule : COUPON_RULES) {
            if (rule.code.equals(couponCode)) {
                if (subtotal >= rule.minOrder) {
                    discount = rule.calculateDiscount(subtotal);
                    appliedCouponCode = couponCode;
                    Toast.makeText(this, "Áp dụng thành công " + couponCode + "!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Đơn tối thiểu $" + rule.minOrder + " mới dùng được mã này!", Toast.LENGTH_SHORT).show();
                }
                calculateCart();
                return;
            }
        }
        Toast.makeText(this, "Mã coupon không hợp lệ!", Toast.LENGTH_SHORT).show();
        calculateCart();
    }

    private double getValidatedDiscount(String couponCode, double subtotal) {
        for (CouponRule rule : COUPON_RULES) {
            if (rule.code.equals(couponCode)) {
                if (subtotal >= rule.minOrder) {
                    return rule.calculateDiscount(subtotal);
                }
            }
        }
        return 0.0;

        // THÊM SỰ KIỆN CLICK CHO NÚT CHECKOUT
        binding.checkOutBtn.setOnClickListener(v -> {
            if (managmentCart.getListCart().isEmpty()) {
                Toast.makeText(CartActivity.this, "Giỏ hàng của bạn đang trống!", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                // Truyền tổng tiền cuối cùng (ĐÃ BAO GỒM PHÍ, THUẾ, COUPON (nếu có)) sang CheckoutActivity
                intent.putExtra("finalOrderAmount", totalAmountFromCartScreen);
                startActivity(intent);
            }
        });
    }


    private void initList(){
        if(managementCart.getListCart().isEmpty()){
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.scroviewCart.setVisibility(View.GONE);
        }else{
            binding.emptyTxt.setVisibility(View.GONE);
            binding.scroviewCart.setVisibility(View.VISIBLE);
        }

        LinearLayoutManager linearLayoutManager  = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.cartView.setLayoutManager(linearLayoutManager);
        adapter = new CartAdapter(managementCart.getListCart(), this, () -> calculateCart());
        binding.cartView.setAdapter(adapter);
    }
}