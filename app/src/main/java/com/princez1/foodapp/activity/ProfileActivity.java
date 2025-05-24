package com.princez1.foodapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.princez1.foodapp.adapter.OrderHistoryAdapter;
import com.princez1.foodapp.databinding.ActivityProfileBinding;
import com.princez1.foodapp.domain.Order;
import com.princez1.foodapp.domain.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;// khai báo binding
    private FirebaseAuth mAuth;// khai báo mAuth là FirebaseAuth
    private DatabaseReference userRef;// tham chiếu đến node Users trong Firebase Realtime Database
    private DatabaseReference ordersRef;// tham chiếu đến node Orders trong Firebase Realtime Database
    private FirebaseUser currentUser;// người dùng hiện tại
    private OrderHistoryAdapter orderHistoryAdapter;// adapter cho RecyclerView
    private ArrayList<Order> orderList;// danh sách chứa các đối tượng Order

    private static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // thiết lập giao diện
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // thiết lập toolbar
        setSupportActionBar(binding.toolbarProfile);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);// hiển thị nút back
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // kiểm tra người dùng đã đăng nhập hay chưa
        if (currentUser == null) {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // lấy tham chiếu đến node Users và Orders trong Firebase Realtime Database
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders").child(currentUser.getUid());

        // thiết lập RecyclerView
        setupRecyclerView();
        // tải thông tin người dùng
        loadUserProfile();
        // tải lịch sử đơn hàng
        loadOrderHistory();

        binding.updateProfileBtn.setOnClickListener(v -> updateUserProfile());
    }

    private void setupRecyclerView() {
        orderList = new ArrayList<>();
        orderHistoryAdapter = new OrderHistoryAdapter(orderList);
        binding.ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.ordersRecyclerView.setAdapter(orderHistoryAdapter);
        binding.ordersRecyclerView.setNestedScrollingEnabled(false);
    }

    private void loadUserProfile() {
        binding.profileEmailEdt.setText(currentUser.getEmail());
        if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
            binding.profileNameEdt.setText(currentUser.getDisplayName());
        } else {
            binding.profileNameEdt.setText("Not updated yet");
        }

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User user = snapshot.getValue(User.class);
                    if (user != null) {
                        if (user.getName() != null && !user.getName().isEmpty()){
                            binding.profileNameEdt.setText(user.getName());
                        }
                        if (user.getPhone() != null) {
                            binding.profilePhoneEdt.setText(user.getPhone());
                        }
                        if (user.getAddress() != null) {
                            binding.profileAddressEdt.setText(user.getAddress());
                        }
                    }
                } else {
                    if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
                        binding.profileNameEdt.setText(currentUser.getDisplayName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Error loading information:" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserProfile() {
        String name = binding.profileNameEdt.getText().toString().trim();
        String phone = binding.profilePhoneEdt.getText().toString().trim();
        String address = binding.profileAddressEdt.getText().toString().trim();

        if (name.isEmpty()) {
            binding.profileNameLayout.setError("Name cannot be empty");
            binding.profileNameEdt.requestFocus();
            return;
        } else {
            binding.profileNameLayout.setError(null);
        }

        if (TextUtils.isEmpty(phone)) {
            binding.profilePhoneLayout.setError("Phone number cannot be empty");
            binding.profilePhoneEdt.requestFocus();
            return;
        } else if (!isValidVietnamesePhoneNumber(phone)) {
            binding.profilePhoneLayout.setError("Invalid phone number (must be 10 digits, starting with 0)");
            binding.profilePhoneEdt.requestFocus();
            return;
        } else {
            binding.profilePhoneLayout.setError(null);
        }
        if (address.isEmpty()) {
            binding.profileAddressLayout.setError("Address cannot be empty");
            binding.profileAddressEdt.requestFocus();
            return;
        } else {
            binding.profileAddressLayout.setError(null);
        }


        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("name", name);
        userUpdates.put("phone", phone);
        userUpdates.put("address", address);

        userRef.updateChildren(userUpdates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ProfileActivity.this, "Information updated successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private boolean isValidVietnamesePhoneNumber(String phone) {
        if (phone == null) {
            return false;
        }
        return phone.matches("^0\\d{9}$");
    }


    private void loadOrderHistory() {
        binding.progressBarOrders.setVisibility(View.VISIBLE);
        binding.noOrdersTxt.setVisibility(View.GONE);
        binding.ordersRecyclerView.setVisibility(View.GONE);

        ordersRef.orderByChild("orderDateTimestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orderList.clear();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Order order = snapshot.getValue(Order.class);
                        if (order != null) {
                            orderList.add(order);
                        }
                    }
                    Collections.reverse(orderList);
                    orderHistoryAdapter.notifyDataSetChanged();
                    binding.ordersRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    binding.noOrdersTxt.setVisibility(View.VISIBLE);
                }
                binding.progressBarOrders.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                binding.progressBarOrders.setVisibility(View.GONE);
                Toast.makeText(ProfileActivity.this, "Error loading order history: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "loadOrderHistory:onCancelled", databaseError.toException());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}