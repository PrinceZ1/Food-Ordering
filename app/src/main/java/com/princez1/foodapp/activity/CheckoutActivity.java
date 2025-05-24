package com.princez1.foodapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.princez1.foodapp.databinding.ActivityCheckoutBinding;
import com.princez1.foodapp.helper.ManagmentCart;
import com.princez1.foodapp.domain.Foods;
import com.princez1.foodapp.domain.Order;
import com.princez1.foodapp.activity.CartActivity;
import java.util.ArrayList; // Nếu chưa có
import java.util.UUID;

public class CheckoutActivity extends AppCompatActivity {

    private ActivityCheckoutBinding binding;
    private ManagmentCart managmentCart;
    private FirebaseAuth mAuth;
    private double tax;

    private DatabaseReference ordersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        managmentCart = new ManagmentCart(this);
        mAuth = FirebaseAuth.getInstance();
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");


        loadUserProfile();

        binding.placeOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (currentUser != null) {
                        String userId = currentUser.getUid();
                        String orderId = ordersRef.child(userId).push().getKey();

                        if (orderId == null) {
                            Toast.makeText(CheckoutActivity.this, "Cannot create order, please try again.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String name = binding.nameEdt.getText().toString().trim();
                        String address = binding.addressEdt.getText().toString().trim();
                        String phone = binding.phoneEdt.getText().toString().trim();
                        String email = binding.emailEdt.getText().toString().trim();
                        String note = binding.noteEdt.getText().toString().trim();
                        ArrayList<Foods> cartItems = managmentCart.getListCart();
                        double percentTax = 0.02;
                        double delivery = 10;

                        tax = Math.round(managmentCart.getTotalFee() * percentTax * 100.0) /100;
                        double totalAmount = Math.round((managmentCart.getTotalFee() + tax + delivery) * 100) / 100;

                        long orderDateTimestamp = System.currentTimeMillis();
                        String status = "Pending";

                        Order newOrder = new Order(orderId, userId, name, address, phone, email, note, cartItems, totalAmount, orderDateTimestamp, status);

                        ordersRef.child(userId).child(orderId).setValue(newOrder)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(CheckoutActivity.this, "Order placed successfully!", Toast.LENGTH_LONG).show();
                                    managmentCart.clearCart();

                                    Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(CheckoutActivity.this, "Order failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    } else {
                        Toast.makeText(CheckoutActivity.this, "You need to log in to place an order.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
                binding.nameEdt.setText(currentUser.getDisplayName());
            }
            if (currentUser.getEmail() != null && !currentUser.getEmail().isEmpty()) {
                binding.emailEdt.setText(currentUser.getEmail());
            }
        }
    }

    private boolean validateInput() {
        String name = binding.nameEdt.getText().toString().trim();
        String address = binding.addressEdt.getText().toString().trim();
        String phone = binding.phoneEdt.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            binding.nameLayout.setError("Please enter your full name");
            binding.nameEdt.requestFocus();
            return false;
        } else {
            binding.nameLayout.setError(null);
        }

        if (TextUtils.isEmpty(address)) {
            binding.addressLayout.setError("Please enter your address");
            binding.addressEdt.requestFocus();
            return false;
        } else {
            binding.addressLayout.setError(null);
        }

        if (TextUtils.isEmpty(phone)) {
            binding.phoneLayout.setError("Please enter your phone number");
            binding.phoneEdt.requestFocus();
            return false;
        } else if (phone.length() < 10) {
            binding.phoneLayout.setError("Invalid phone number");
            binding.phoneEdt.requestFocus();
            return false;
        } else {
            binding.phoneLayout.setError(null);
        }

        return true;
    }
}