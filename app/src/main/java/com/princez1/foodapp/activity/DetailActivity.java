package com.princez1.foodapp.activity;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.princez1.foodapp.R;
import com.princez1.foodapp.databinding.ActivityDetailBinding;
import com.princez1.foodapp.domain.Foods;
import com.princez1.foodapp.helper.ManagmentCart;

public class DetailActivity extends BaseActivity {

    ActivityDetailBinding binding;
    private Foods object;
    private int num=1;

    private ManagmentCart managmentCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().setStatusBarColor(getResources().getColor(R.color.black));

        getIntentExtra();
        setVariable();
    }

    private void setVariable() {
        managmentCart = new ManagmentCart(this);

        binding.backBtn.setOnClickListener(v -> finish());

        Glide.with(DetailActivity.this)
                .load(object.getImagePath())
                .into(binding.pic);
        binding.priceTxt.setText("$" + object.getPrice());
        binding.titleTxt.setText(object.getTitle());
        binding.descriptionTxt.setText(object.getDescription());
        binding.rateTxt.setText(object.getStar() + "Rating");
        binding.ratingBar.setRating((float) object.getStar());
        binding.totalTxt.setText((num*object.getPrice()+"$"));

        binding.plusBtn.setOnClickListener(v -> {
            num=num+1;
            binding.numTxt.setText(num+"");
            binding.totalTxt.setText("$"+(num*object.getPrice()));
        });

        binding.minusBtn.setOnClickListener(v -> {
            if(num>1){
                num=num-1;
                binding.numTxt.setText(num+"");
                binding.totalTxt.setText("$"+(num*object.getPrice()));
            }
        });

        binding.addBtn.setOnClickListener(v -> {
            object.setNumberInCart(num);
            managmentCart.insertFood(object);
        });
    }

    private void getIntentExtra() {
        object = (Foods) getIntent().getSerializableExtra("object");
    }
}