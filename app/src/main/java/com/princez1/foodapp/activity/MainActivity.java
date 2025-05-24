package com.princez1.foodapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Firebase;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.princez1.foodapp.R;
import com.princez1.foodapp.adapter.BestFoodsAdapter;
import com.princez1.foodapp.adapter.CategoryAdapter;
import com.princez1.foodapp.databinding.ActivityMainBinding;
import com.princez1.foodapp.domain.Category;
import com.princez1.foodapp.domain.Foods;
import com.princez1.foodapp.domain.Location;
import com.princez1.foodapp.domain.Price;
import com.princez1.foodapp.domain.Time;
import android.content.Intent; // Nếu chưa có
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding binding;
//    int selectedLocationId = -1;
//    int selectedTimeId = -1;
//    int selectedPriceId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mAuth = FirebaseAuth.getInstance(); // Khởi tạo FirebaseAuth

        // ... (code hiện có của bạn để load user name, categories, foods)
        loadUserName(); // Gọi hàm load user name

        // THÊM SỰ KIỆN CLICK CHO userNameTxt
        binding.userNameTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                } else {
                    // Nếu người dùng chưa đăng nhập, có thể chuyển đến LoginActivity
                    Toast.makeText(MainActivity.this, "Vui lòng đăng nhập để xem thông tin.", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }
        });

        initLocation();
        initTime();
        initPrice();


        initBestFood();
        initCategory();
        setVariable();
//        binding.locationSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Location selected = (Location) parent.getItemAtPosition(position);
//                selectedLocationId = selected.getId();
//                filterFoods();
//            }
//            @Override public void onNothingSelected(AdapterView<?> parent) {}
//        });
//
//        binding.timeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Time selected = (Time) parent.getItemAtPosition(position);
//                selectedTimeId = selected.getId();
//                filterFoods();
//            }
//            @Override public void onNothingSelected(AdapterView<?> parent) {}
//        });
//
//        binding.priceSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                Price selected = (Price) parent.getItemAtPosition(position);
//                selectedPriceId = selected.getId();
//                filterFoods();
//            }
//            @Override public void onNothingSelected(AdapterView<?> parent) {}
//        });
    }

//    private void filterFoods() {
//        Intent intent = new Intent(MainActivity.this, ListFoodsActivity.class);
//        intent.putExtra("filter", true);
//        intent.putExtra("LocationId", selectedLocationId);
//        intent.putExtra("TimeId", selectedTimeId);
//        intent.putExtra("PriceId", selectedPriceId);
//        intent.putExtra("CategoryName", "Filtered Foods");
//        startActivity(intent);
//    }
    private void loadUserName() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String displayName = currentUser.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                binding.userNameTxt.setText(displayName);
            } else if (currentUser.getEmail() != null) {
                // Nếu không có DisplayName, thử lấy phần trước @ của email
                String email = currentUser.getEmail();
                binding.userNameTxt.setText(email.split("@")[0]);
            } else {
                binding.userNameTxt.setText("User"); // Tên mặc định
            }
            // Lấy tên chi tiết hơn từ Realtime Database nếu cần
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(currentUser.getUid());
            userRef.child("name").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists() && snapshot.getValue(String.class) != null && !snapshot.getValue(String.class).isEmpty()){
                        binding.userNameTxt.setText(snapshot.getValue(String.class));
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });

        } else {
            binding.userNameTxt.setText("Khách"); // Hoặc một giá trị nào đó cho người dùng chưa đăng nhập
            // Có thể ẩn hoặc thay đổi hành vi của userNameTxt nếu chưa đăng nhập
        }
    }
    private void setVariable() {
        binding.logoutBtn.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        });
        binding.searchBtn.setOnClickListener(v -> {
            String text = binding.searchEdt.getText().toString();
            if(!text.isEmpty()){
                Intent intent = new Intent(MainActivity.this, ListFoodsActivity.class);
                intent.putExtra("text", text);
                intent.putExtra("isSearch", true);
                startActivity(intent);
            }
        });
        binding.cartBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CartActivity.class)));

        binding.BtnViewAll.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ListFoodsActivity.class);
            intent.putExtra("ViewAll", true);
            intent.putExtra("CategoryName", "Today's best Foods");
            startActivity(intent);
        });


    }

    private void initBestFood() {
        DatabaseReference myRef = database.getReference("Foods");
        binding.progressBarBestFood.setVisibility(View.VISIBLE);
        ArrayList<Foods> list = new ArrayList<>();
        Query query = myRef.orderByChild("BestFood").equalTo(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(Foods.class));
                    }
                    if (list.size() > 0) {
                        binding.bestFoodView.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                        RecyclerView.Adapter adapter = new BestFoodsAdapter(list);
                        binding.bestFoodView.setAdapter(adapter);
                    }
                    binding.progressBarBestFood.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initCategory() {
        DatabaseReference myRef = database.getReference("Category");
        binding.progressBarCategory.setVisibility(View.VISIBLE);
        ArrayList<Category> list = new ArrayList<>();

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(Category.class));
                    }
                    if (list.size() > 0) {
                        binding.categoryView.setLayoutManager(new GridLayoutManager(MainActivity.this, 4));
                        RecyclerView.Adapter adapter = new CategoryAdapter(list);
                        binding.categoryView.setAdapter(adapter);
                    }
                    binding.progressBarCategory.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initLocation() {
        DatabaseReference myRef = database.getReference("Location");
        ArrayList<Location> list = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(Location.class));
                    }
                    ArrayAdapter<Location> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.sp_item, list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.locationSp.setAdapter(adapter);

                    // Thêm xử lý chọn
                    final boolean[] isFirstSelection = {true};
                    binding.locationSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (isFirstSelection[0]) {
                                isFirstSelection[0] = false;
                                return; // Bỏ qua lần chọn đầu
                            }
                            Location selected = list.get(position);
                            Intent intent = new Intent(MainActivity.this, ListFoodsActivity.class);
                            intent.putExtra("filter", true);
                            intent.putExtra("CategoryName", "Filtered by Location");
                            intent.putExtra("LocationId", selected.getId());
                            intent.putExtra("TimeId", -1);
                            intent.putExtra("PriceId", -1);
                            startActivity(intent);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


    private void initTime() {
        DatabaseReference myRef = database.getReference("Time");
        ArrayList<Time> list = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(Time.class));
                    }
                    ArrayAdapter<Time> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.sp_item, list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.timeSp.setAdapter(adapter);

                    final boolean[] isFirstSelection = {true};
                    binding.timeSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (isFirstSelection[0]) {
                                isFirstSelection[0] = false;
                                return;
                            }
                            Time selected = list.get(position);
                            Intent intent = new Intent(MainActivity.this, ListFoodsActivity.class);
                            intent.putExtra("filter", true);
                            intent.putExtra("CategoryName", "Filtered by Time");
                            intent.putExtra("LocationId", -1);
                            intent.putExtra("TimeId", selected.getId());
                            intent.putExtra("PriceId", -1);
                            startActivity(intent);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }


    private void initPrice() {
        DatabaseReference myRef = database.getReference("Price");
        ArrayList<Price> list = new ArrayList<>();
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(Price.class));
                    }
                    ArrayAdapter<Price> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.sp_item, list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.priceSp.setAdapter(adapter);

                    final boolean[] isFirstSelection = {true};
                    binding.priceSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (isFirstSelection[0]) {
                                isFirstSelection[0] = false;
                                return;
                            }
                            Price selected = list.get(position);
                            Intent intent = new Intent(MainActivity.this, ListFoodsActivity.class);
                            intent.putExtra("filter", true);
                            intent.putExtra("CategoryName", "Filtered by Price");
                            intent.putExtra("LocationId", -1);
                            intent.putExtra("TimeId", -1);
                            intent.putExtra("PriceId", selected.getId());
                            startActivity(intent);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

}