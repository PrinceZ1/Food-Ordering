package com.princez1.foodapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.princez1.foodapp.databinding.ActivityLoginBinding;

public class LoginActivity extends BaseActivity {
    ActivityLoginBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // gọi đến onCreate của BaseActivity
        binding=ActivityLoginBinding.inflate(getLayoutInflater());// khởi tạo binding
        setContentView(binding.getRoot());// đặt layout cho activity

        setVariable();// thiết lập các trình xử lý sự kiện cho các nút

        // thêm event cho nút sigup
        binding.signupBtn.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, SignupActivity.class));// chuyển đến màn hình đăng ký
        });
    }

    private void setVariable() {
        // thêm event cho nút login
        binding.loginBtn.setOnClickListener(v -> {
            // lấy email và password từ EditText
            String email=binding.userEdt.getText().toString().trim();
            String password=binding.passEdt.getText().toString().trim();
            if(!email.isEmpty() && !password.isEmpty()){
                // đăng nhập bằng email và password
                mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(LoginActivity.this, task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));// chuyển đến màn hình chính
                        finishAffinity();// đóng activity hiện tại và các activity trước đó
                    }else{
                        Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }else{
                Toast.makeText(LoginActivity.this, "Please fill username and password", Toast.LENGTH_SHORT).show();
            }
        });
    }
}