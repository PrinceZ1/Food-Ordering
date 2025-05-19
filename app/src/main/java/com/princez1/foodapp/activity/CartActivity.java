package com.princez1.foodapp.activity;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.princez1.foodapp.adapter.CartAdapter;
import com.princez1.foodapp.databinding.ActivityCartBinding;
import com.princez1.foodapp.helper.ManagmentCart;

public class CartActivity extends AppCompatActivity {
    private ActivityCartBinding binding;
    private RecyclerView.Adapter adapter;
    private ManagmentCart managmentCart;
    private double tax;

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
        double percentTax = 0.02;
        double delivery = 10;

        tax = Math.round(managmentCart.getTotalFee() * percentTax * 100.0) /100;

        double total = Math.round((managmentCart.getTotalFee() + tax + delivery) * 100) / 100;
        double itemTotal = Math.round(managmentCart.getTotalFee() * 100) / 100;

        binding.totalFeeTxt.setText("$" + itemTotal);
        binding.taxTxt.setText("$" + tax);
        binding.deliveryTxt.setText("$" + delivery);
        binding.totalTxt.setText("$" + total);

    }

    private void setVariable(){
        binding.backBtn.setOnClickListener(v -> finish());
    }

    private void initList(){
        if(managmentCart.getListCart().isEmpty()){
            binding.emptyTxt.setVisibility(View.VISIBLE);
            binding.scroviewCart.setVisibility(View.GONE);
        }else{
            binding.emptyTxt.setVisibility(View.GONE);
            binding.scroviewCart.setVisibility(View.VISIBLE);
        }

        LinearLayoutManager linearLayoutManager  = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.cartView.setLayoutManager(linearLayoutManager);
        adapter = new CartAdapter(managmentCart.getListCart(), this, () -> calculateCart());
        binding.cartView.setAdapter(adapter);
    }

}