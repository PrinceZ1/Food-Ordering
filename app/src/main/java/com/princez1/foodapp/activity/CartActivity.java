package com.princez1.foodapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.princez1.foodapp.adapter.CartAdapter;
import com.princez1.foodapp.databinding.ActivityCartBinding;
import com.princez1.foodapp.helper.ManagmentCart;

public class CartActivity extends BaseActivity {
    private ActivityCartBinding binding;
    private RecyclerView.Adapter adapter;
    private ManagmentCart managmentCart;
    private double discount = 0.0;
    
    // Định nghĩa các coupon với điều kiện
    private static final double WELCOME10_MIN_ORDER = 20.0; // Đơn hàng tối thiểu $20
    private static final double SAVE20_MIN_ORDER = 50.0;    // Đơn hàng tối thiểu $50

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        managmentCart = new ManagmentCart(this);
        calculateCart();
        setVariable();
        initList();
    }

    private void calculateCart(){
        double delivery = 10;
        double subtotal = managmentCart.getTotalFee();
        double discountedSubtotal = subtotal - discount;
        if (discountedSubtotal < 0) discountedSubtotal = 0;
        double total = Math.round((discountedSubtotal + delivery) * 100) / 100.0;

        binding.totalFeeTxt.setText("$" + subtotal);
        binding.discountTxt.setText("$" + discount);
        binding.deliveryTxt.setText("$" + delivery);
        binding.totalTxt.setText("$" + total);
    }

    private void setVariable(){
        binding.backBtn.setOnClickListener(v -> finish());
        
        binding.applyCouponBtn.setOnClickListener(v -> {
            String couponCode = binding.couponEdt.getText().toString().trim();
            if (couponCode.isEmpty()) {
                Toast.makeText(this, "Please enter coupon code", Toast.LENGTH_SHORT).show();
                return;
            }
            
            double currentTotal = managmentCart.getTotalFee();
            
            // Kiểm tra coupon code và điều kiện đơn hàng tối thiểu
            if (couponCode.equals("WELCOME10")) {
                if (currentTotal < WELCOME10_MIN_ORDER) {
                    Toast.makeText(this, 
                        String.format("Order minimum $%.2f required for this coupon", WELCOME10_MIN_ORDER), 
                        Toast.LENGTH_LONG).show();
                    discount = 0.0;
                } else {
                    discount = Math.round(currentTotal * 0.1 * 100.0) / 100.0; // 10% discount
                    Toast.makeText(this, "Coupon applied successfully!", Toast.LENGTH_SHORT).show();
                }
                calculateCart();
            } else if (couponCode.equals("SAVE20")) {
                if (currentTotal < SAVE20_MIN_ORDER) {
                    Toast.makeText(this, 
                        String.format("Order minimum $%.2f required for this coupon", SAVE20_MIN_ORDER), 
                        Toast.LENGTH_LONG).show();
                    discount = 0.0;
                } else {
                    discount = Math.round(currentTotal * 0.2 * 100.0) / 100.0; // 20% discount
                    Toast.makeText(this, "Coupon applied successfully!", Toast.LENGTH_SHORT).show();
                }
                calculateCart();
            } else {
                Toast.makeText(this, "Invalid coupon code", Toast.LENGTH_SHORT).show();
                discount = 0.0;
                calculateCart();
            }
        });
    }

    private void initList(){
        if(managmentCart.getListCart().isEmpty()){
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.scroviewCart.setVisibility(View.GONE);
        }else{
            binding.emptyTxt.setVisibility(View.GONE);
            binding.scroviewCart.setVisibility(View.VISIBLE);
        }

        LinearLayoutManager linearLayoutManager  = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        binding.cartView.setLayoutManager(linearLayoutManager);
        adapter = new CartAdapter(managmentCart.getListCart(), this, () -> calculateCart());
        binding.cartView.setAdapter(adapter);
    }
}