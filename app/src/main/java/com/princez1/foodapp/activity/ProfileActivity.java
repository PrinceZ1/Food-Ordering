package com.princez1.foodapp.activity;

import android.os.Bundle;
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
import com.princez1.foodapp.domain.User; // Bạn cần tạo lớp User này

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private DatabaseReference ordersRef;
    private FirebaseUser currentUser;
    private OrderHistoryAdapter orderHistoryAdapter;
    private ArrayList<Order> orderList;

    private static final String TAG = "ProfileActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbarProfile);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            // Should not happen if profile is accessed after login
            Toast.makeText(this, "Người dùng chưa đăng nhập.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Tham chiếu đến node "Users" và "Orders" trong Firebase
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders").child(currentUser.getUid());

        setupRecyclerView();
        loadUserProfile();
        loadOrderHistory();

        binding.updateProfileBtn.setOnClickListener(v -> updateUserProfile());
    }

    private void setupRecyclerView() {
        orderList = new ArrayList<>();
        orderHistoryAdapter = new OrderHistoryAdapter(orderList);
        binding.ordersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.ordersRecyclerView.setAdapter(orderHistoryAdapter);
        binding.ordersRecyclerView.setNestedScrollingEnabled(false); // To make scroll work smoothly inside NestedScrollView
    }

    private void loadUserProfile() {
        // Load basic info from FirebaseAuth
        binding.profileEmailEdt.setText(currentUser.getEmail());
        if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
            binding.profileNameEdt.setText(currentUser.getDisplayName());
        } else {
            binding.profileNameEdt.setText("Chưa cập nhật");
        }


        // Load additional info from Firebase Realtime Database (node "Users")
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
                    // User node doesn't exist, perhaps pre-fill with Firebase Auth display name
                    if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
                        binding.profileNameEdt.setText(currentUser.getDisplayName());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, "Lỗi tải thông tin: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserProfile() {
        String name = binding.profileNameEdt.getText().toString().trim();
        String phone = binding.profilePhoneEdt.getText().toString().trim();
        String address = binding.profileAddressEdt.getText().toString().trim();

        if (name.isEmpty()) {
            binding.profileNameLayout.setError("Tên không được để trống");
            return;
        } else {
            binding.profileNameLayout.setError(null);
        }
        // Add more validation for phone and address if needed

        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("name", name); // Firebase Auth không có sẵn trường tên riêng, ta lưu trong DB
        userUpdates.put("phone", phone);
        userUpdates.put("address", address);
        // userUpdates.put("email", currentUser.getEmail()); // Email thường không cho đổi hoặc cần quy trình xác thực riêng

        userRef.updateChildren(userUpdates)
                .addOnSuccessListener(aVoid -> Toast.makeText(ProfileActivity.this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(ProfileActivity.this, "Cập nhật thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }


    private void loadOrderHistory() {
        binding.progressBarOrders.setVisibility(View.VISIBLE);
        binding.noOrdersTxt.setVisibility(View.GONE);
        binding.ordersRecyclerView.setVisibility(View.GONE);

        ordersRef.orderByChild("orderDateTimestamp").addValueEventListener(new ValueEventListener() { // Sắp xếp theo ngày đặt
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
                    Collections.reverse(orderList); // Hiển thị đơn mới nhất lên đầu
                    orderHistoryAdapter.notifyDataSetChanged(); // Sử dụng notifyDataSetChanged hoặc các phương thức tối ưu hơn
                    binding.ordersRecyclerView.setVisibility(View.VISIBLE);
                } else {
                    binding.noOrdersTxt.setVisibility(View.VISIBLE);
                }
                binding.progressBarOrders.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                binding.progressBarOrders.setVisibility(View.GONE);
                Toast.makeText(ProfileActivity.this, "Lỗi tải lịch sử đơn hàng: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "loadOrderHistory:onCancelled", databaseError.toException());
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if any)
        }
        return super.onOptionsItemSelected(item);
    }
}