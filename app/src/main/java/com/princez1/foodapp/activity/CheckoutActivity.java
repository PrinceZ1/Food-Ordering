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
import com.princez1.foodapp.domain.Foods; // Nếu chưa có
import com.princez1.foodapp.domain.Order; // Import lớp Order vừa tạo

import java.util.ArrayList; // Nếu chưa có
import java.util.UUID; // Để tạo ID đơn hàng ngẫu nhiên

public class CheckoutActivity extends AppCompatActivity {

    private ActivityCheckoutBinding binding;
    private ManagmentCart managmentCart;
    private FirebaseAuth mAuth;

    private DatabaseReference ordersRef; // Thêm dòng này

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        managmentCart = new ManagmentCart(this);
        mAuth = FirebaseAuth.getInstance();
        // Khởi tạo DatabaseReference cho node "Orders"
        ordersRef = FirebaseDatabase.getInstance().getReference("Orders");


        loadUserProfile();

        binding.placeOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (currentUser != null) {
                        String userId = currentUser.getUid();
                        String orderId = ordersRef.child(userId).push().getKey(); // Tạo orderId duy nhất

                        if (orderId == null) {
                            Toast.makeText(CheckoutActivity.this, "Không thể tạo đơn hàng, vui lòng thử lại.", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        String name = binding.nameEdt.getText().toString().trim();
                        String address = binding.addressEdt.getText().toString().trim();
                        String phone = binding.phoneEdt.getText().toString().trim();
                        String email = binding.emailEdt.getText().toString().trim();
                        String note = binding.noteEdt.getText().toString().trim();
                        ArrayList<Foods> cartItems = managmentCart.getListCart();
                        double totalAmount = managmentCart.getTotalFee(); // Giả sử bạn có phương thức này hoặc tính lại
                        long orderDateTimestamp = System.currentTimeMillis();
                        String status = "Pending"; // Trạng thái ban đầu của đơn hàng

                        Order newOrder = new Order(orderId, userId, name, address, phone, email, note, cartItems, totalAmount, orderDateTimestamp, status);

                        // Lưu đơn hàng vào Firebase Realtime Database
                        ordersRef.child(userId).child(orderId).setValue(newOrder)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(CheckoutActivity.this, "Đặt hàng thành công!", Toast.LENGTH_LONG).show();
                                    managmentCart.clearCart(); // Xóa giỏ hàng

                                    Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(CheckoutActivity.this, "Đặt hàng thất bại: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                });
                    } else {
                        Toast.makeText(CheckoutActivity.this, "Bạn cần đăng nhập để đặt hàng.", Toast.LENGTH_SHORT).show();
                        // Có thể chuyển người dùng đến LoginActivity
                    }
                }
            }
        });
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Tự động điền tên và email nếu người dùng đã đăng nhập
            if (currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
                binding.nameEdt.setText(currentUser.getDisplayName());
            }
            if (currentUser.getEmail() != null && !currentUser.getEmail().isEmpty()) {
                binding.emailEdt.setText(currentUser.getEmail());
            }
            // Bạn có thể lấy SĐT và địa chỉ từ Firebase Realtime Database/Firestore nếu đã lưu trước đó
        }
    }

    private boolean validateInput() {
        String name = binding.nameEdt.getText().toString().trim();
        String address = binding.addressEdt.getText().toString().trim();
        String phone = binding.phoneEdt.getText().toString().trim();
        // String email = binding.emailEdt.getText().toString().trim(); // Email là tùy chọn

        if (TextUtils.isEmpty(name)) {
            binding.nameLayout.setError("Vui lòng nhập họ tên");
            binding.nameEdt.requestFocus();
            return false;
        } else {
            binding.nameLayout.setError(null);
        }

        if (TextUtils.isEmpty(address)) {
            binding.addressLayout.setError("Vui lòng nhập địa chỉ");
            binding.addressEdt.requestFocus();
            return false;
        } else {
            binding.addressLayout.setError(null);
        }

        if (TextUtils.isEmpty(phone)) {
            binding.phoneLayout.setError("Vui lòng nhập số điện thoại");
            binding.phoneEdt.requestFocus();
            return false;
        } else if (phone.length() < 10) { // Kiểm tra SĐT cơ bản
            binding.phoneLayout.setError("Số điện thoại không hợp lệ");
            binding.phoneEdt.requestFocus();
            return false;
        } else {
            binding.phoneLayout.setError(null);
        }

        // (Tùy chọn) Kiểm tra định dạng email nếu người dùng nhập
        // if (!TextUtils.isEmpty(email) && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        //     binding.emailLayout.setError("Email không hợp lệ");
        //     binding.emailEdt.requestFocus();
        //     return false;
        // } else {
        //     binding.emailLayout.setError(null);
        // }

        return true;
    }
}