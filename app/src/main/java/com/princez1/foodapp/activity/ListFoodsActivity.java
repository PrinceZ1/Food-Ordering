package com.princez1.foodapp.activity;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.princez1.foodapp.R;
import com.princez1.foodapp.adapter.FoodListAdapter;
import com.princez1.foodapp.databinding.ActivityListFoodsBinding;
import com.princez1.foodapp.domain.Foods;

import java.util.ArrayList;

public class ListFoodsActivity extends BaseActivity {
    ActivityListFoodsBinding binding;
    private RecyclerView.Adapter adapterListFood;
    private int categoryId;
    private String categoryName;
    private String searchText;
    private boolean isSeach;
    private boolean isFilter = false;
    private int locationId;
    private int timeId;
    private int priceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListFoodsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getIntentExtra();
        inistList();

    }



    private void inistList() {
        DatabaseReference myRef = database.getReference("Foods");
        binding.progressBar.setVisibility(View.VISIBLE);
        ArrayList<Foods> list = new ArrayList<>();

        Query query;
        if (isSeach) {
            query = myRef.orderByChild("Title").startAt(searchText).endAt(searchText + '\uf8ff');
        } else if (isViewAll) {
            query = myRef.orderByChild("BestFood").equalTo(true); // <-- LẤY MÓN ĂN TỐT NHẤT
        } else if (isFilter) {
            query = myRef; // Lấy toàn bộ rồi lọc thủ công
        }else {
            query = myRef.orderByChild("CategoryId").equalTo(categoryId);
        }
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (isFilter) {
                        for (DataSnapshot issue : snapshot.getChildren()) {
                            Foods food = issue.getValue(Foods.class);
                            if (food != null &&
                                    (locationId == -1 || food.getLocationId() == locationId) &&
                                    (timeId == -1 || food.getTimeId() == timeId) &&
                                    (priceId == -1 || food.getPriceId() == priceId)) {
                                list.add(food);
                            }
                        }
                    } else {
                        for (DataSnapshot issue : snapshot.getChildren()) {
                            list.add(issue.getValue(Foods.class));
                        }
                    }

                    if (list.size() > 0) {
                        binding.foodListView.setLayoutManager(new GridLayoutManager(ListFoodsActivity.this, 2));
                        adapterListFood = new FoodListAdapter(list);
                        binding.foodListView.setAdapter(adapterListFood);
                    }
                    binding.progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean isViewAll = false; // thêm biến cờ

    private void getIntentExtra() {
        categoryId = getIntent().getIntExtra("CategoryId", 0);
        categoryName = getIntent().getStringExtra("CategoryName");
        searchText = getIntent().getStringExtra("text");
        isSeach = getIntent().getBooleanExtra("isSearch", false);
        isViewAll = getIntent().getBooleanExtra("ViewAll", false); // <-- THÊM DÒNG NÀY
        isFilter = getIntent().getBooleanExtra("filter", false);
        locationId = getIntent().getIntExtra("LocationId", -1);
        timeId = getIntent().getIntExtra("TimeId", -1);
        priceId = getIntent().getIntExtra("PriceId", -1);

        binding.titleTxt.setText(categoryName);
        binding.backBtn.setOnClickListener(v -> finish());
    }
}