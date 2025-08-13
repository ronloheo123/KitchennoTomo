package com.example.kitchennotomo;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AddActivity extends AppCompatActivity {
    EditText foodname, foodcategory;
    Button add_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
//        EdgeToEdge.enable(this);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
        foodname = findViewById(R.id.foodname);
        foodcategory = findViewById(R.id.foodcategory);
        add_button = findViewById(R.id.add_button);
        add_button.setOnClickListener(v -> {
            String name = foodname.getText().toString().trim();
            String cate = foodcategory.getText().toString().trim();
            if (name.isEmpty() || cate.isEmpty()) {
                Toast.makeText(AddActivity.this, "Nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            MyDatabaseHelper myDB = new MyDatabaseHelper(AddActivity.this);
            long id = myDB.addFood(name, cate);
            if (id != -1) finish(); // thêm xong thì quay về màn chính
        });
    }
}