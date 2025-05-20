package com.princez1.foodapp.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.widget.Toast;

import com.princez1.foodapp.adapter.CartAdapter;
import com.princez1.foodapp.databinding.ActivityCartBinding;
import com.princez1.foodapp.helper.ManagmentCart;

public class CartActivity extends BaseActivity {
    private ActivityCartBinding binding;
    private RecyclerView.Adapter adapter;
    private ManagmentCart managmentCart;
    private double tax;
    private double totalAmountFromCartScreen; // Biến để lưu tổng tiền cuối cùng từ màn hình giỏ hàng

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        managmentCart = new ManagmentCart(this);
        setVariable();
        calculateCart();

        initList();
    }

    private void calculateCart(){
        double percentTax = 0.02;
        double delivery = 10;

        tax = Math.round(managmentCart.getTotalFee() * percentTax * 100.0) /100;

        double total = Math.round((managmentCart.getTotalFee() + tax + delivery) * 100) / 100;
        double itemTotal = Math.round(managmentCart.getTotalFee() * 100) / 100;

        binding.totalFeeTxt.setText("$" + itemTotal);
        binding.taxTxt.setText("$" + tax);
        binding.deliveryTxt.setText("$" + delivery);
        binding.totalTxt.setText("$" + total);

        // Lưu lại tổng tiền cuối cùng từ màn hình giỏ hàng
        totalAmountFromCartScreen = total;
    }

    private void setVariable(){
        binding.backBtn.setOnClickListener(v -> finish());

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