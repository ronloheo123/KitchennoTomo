package com.example.kitchennotomo;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {


    EditText id,FoodName,CategoryFood;
    Button insert,update,delete,view;

    DBHelper DB;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_main);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
            id = findViewById(R.id.ID);
            FoodName= findViewById(R.id.FoodName);
            insert= findViewById(R.id.btnInsert);
            update= findViewById(R.id.btnUpdate);
            delete=findViewById(R.id.btnDelete);
            view=findViewById(R.id.btnView);
            DB = new DBHelper(this);
            //INSERT
            insert.setOnClickListener(v -> {
            String FoodNameTXT = FoodName.getText().toString();
            String CategoryFoodTXT = CategoryFood.getText().toString();
            Boolean checkinsertdata= DB.insertFood(FoodNameTXT,CategoryFoodTXT);
            if(checkinsertdata==true)
                Toast.makeText(MainActivity.this,"New Entry Interted",Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(MainActivity.this,"New Entry Not Inserted",Toast.LENGTH_SHORT).show();
        });
            update.setOnClickListener(v -> {
            String FoodNameTXT = FoodName.getText().toString();
            String CategoryFoodTXT = CategoryFood.getText().toString();
            Boolean checkinsertdata= DB.updateFood(,FoodNameTXT,CategoryFoodTXT);
            if(checkinsertdata==true)
                Toast.makeText(MainActivity.this,"New Entry Interted",Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(MainActivity.this,"New Entry Not Inserted",Toast.LENGTH_SHORT).show();
        });

    }
}