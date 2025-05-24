    package com.princez1.foodapp.activity;

    import android.content.Intent;
    import android.os.Bundle;
    import android.util.Log;
    import android.widget.Toast;

    import com.princez1.foodapp.databinding.ActivitySignupBinding;

    public class SignupActivity extends BaseActivity {

        ActivitySignupBinding binding;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState); // gọi đến onCreate của BaseActivity

            binding=ActivitySignupBinding.inflate(getLayoutInflater()); // khởi tạo binding
            setContentView(binding.getRoot()); // đặt layout cho activity

            setVariable();
        }
        private void setVariable() {
            binding.signupBtn.setOnClickListener(v -> {
                String email=binding.userEdt.getText().toString().trim();
                String password=binding.passEdt.getText().toString().trim();
                if(password.length()<6){
                    Toast.makeText(SignupActivity.this, "your password must be 6 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(SignupActivity.this, task -> {
                    if (task.isSuccessful()) {
                        Log.i(TAG, "createUserWithEmail:success");
                        Toast.makeText(SignupActivity.this, "Signup Successful. Please Login.", Toast.LENGTH_SHORT).show();
                        // Chuyển người dùng đến MainActivity hoặc LoginActivity sau khi đăng ký thành công
                        // Ví dụ: Chuyển đến LoginActivity để họ đăng nhập lại
                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK); // Xóa stack activity trước đó
                        startActivity(intent);
                        finish(); // Đóng SignupActivity
                    } else {
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(SignupActivity.this, "Authentication failed: " + task.getException().getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
            });
        }
    }