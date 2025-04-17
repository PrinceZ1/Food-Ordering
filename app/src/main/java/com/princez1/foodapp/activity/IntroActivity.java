package com.princez1.foodapp.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.princez1.foodapp.R;
import com.princez1.foodapp.databinding.ActivityIntroBinding;

public class IntroActivity extends BaseActivity {
    ActivityIntroBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setVariable();
        getWindow().setStatusBarColor(Color.parseColor("#FFE485"));
    }

    private void setVariable() {
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser()!=null){
                    startActivity(new Intent(IntroActivity.this, MainActivity.class));
                } else {
                    startActivity(new Intent(IntroActivity.this, LoginActivity.class));

                }
            }
        });
        binding.loginBtn.setOnClickListener(v -> startActivity(new Intent(IntroActivity.this, SignupActivity.class)));
    }
}