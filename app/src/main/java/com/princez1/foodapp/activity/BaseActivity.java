package com.princez1.foodapp.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.princez1.foodapp.R;


public class BaseActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseDatabase database;
    public String TAG="uilover";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        mAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        getWindow().setStatusBarColor(getResources().getColor(R.color.white));
    }
}