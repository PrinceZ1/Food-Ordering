package com.princez1.foodapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.princez1.foodapp.databinding.ActivityCheckoutBinding;
import com.princez1.foodapp.helper.ManagmentCart;

public class CheckoutActivity extends AppCompatActivity {

    private ActivityCheckoutBinding binding;
    private ManagmentCart managmentCart;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        managmentCart = new ManagmentCart(this);
        mAuth = FirebaseAuth.getInstance();

        loadUserProfile();

        binding.placeOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    // Xử lý đơn hàng (ví dụ: lưu vào Firebase Database)
                    // String name = binding.nameEdt.getText().toString().trim();
                    // String address = binding.addressEdt.getText().toString().trim();
                    // String phone = binding.phoneEdt.getText().toString().trim();
                    // String email = binding.emailEdt.getText().toString().trim();
                    // String note = binding.noteEdt.getText().toString().trim();
                    // ArrayList<Foods> cartItems = managmentCart.getListCart();
                    // double totalAmount = managmentCart.getTotalFee();

                    // Hiện tại, chỉ hiển thị Toast và xóa giỏ hàng
                    Toast.makeText(CheckoutActivity.this, "Đặt hàng thành công!", Toast.LENGTH_LONG).show();

                    managmentCart.clearCart(); // Xóa giỏ hàng

                    // Chuyển về MainActivity và xóa các activity trung gian
                    Intent intent = new Intent(CheckoutActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // Đóng CheckoutActivity
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