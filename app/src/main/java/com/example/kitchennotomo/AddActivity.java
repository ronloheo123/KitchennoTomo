package com.example.kitchennotomo;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class AddActivity extends AppCompatActivity {

    private EditText foodName, foodCategory;
    private Button addBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add); // layout chứa 2 EditText và nút ADD

        foodName = findViewById(R.id.foodname);
        foodCategory = findViewById(R.id.foodcategory);
        addBtn = findViewById(R.id.add_button);

        addBtn.setOnClickListener(v -> {
            String name = foodName.getText().toString().trim();
            String category = foodCategory.getText().toString().trim();

            if (name.isEmpty() || category.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            } else {
                // Lưu vào SQLite
                MyDatabaseHelper myDB = new MyDatabaseHelper(AddActivity.this);
                myDB.addRecipe(name, category);

                Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show();

                // Trả kết quả về MainActivity
                setResult(RESULT_OK);
                finish();
            }
        });
    }
}
