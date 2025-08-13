package com.example.kitchennotomo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class UpdateActivity extends AppCompatActivity {

    EditText foodnameupdate, foodcategoryupdate;
    Button update_button,delete_button;
    String _id,foodname,foodcategory;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update);
////        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
////            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
////            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
////            return insets;
//        });
        foodnameupdate = findViewById(R.id.foodnameupdate);
        foodcategoryupdate = findViewById(R.id.foodcategoryupdate);
        update_button = findViewById(R.id.update_button);
        delete_button=findViewById(R.id.delete_button);
        //frist we call this
        getandSetIntentData();

        //set actionbar foodname after  getandsetintentData method
        ActionBar ab= getSupportActionBar();
        if (ab != null) {
            ab.setTitle(foodname);
        }


        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyDatabaseHelper myDB= new MyDatabaseHelper(UpdateActivity.this);

                String newFoodName = foodnameupdate.getText().toString().trim();
                String newFoodCategory = foodcategoryupdate.getText().toString().trim();

                myDB.updateData(_id, newFoodName, newFoodCategory);
                finish(); // quay lại MainActivity
            }
        });
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog();
            }
        });



    }
    void getandSetIntentData(){
        if(getIntent().hasExtra("_id")&&getIntent().hasExtra("foodname")&&
                getIntent().hasExtra("foodcategory")){
            //Getting Data
            _id = getIntent().getStringExtra("_id");
            foodname = getIntent().getStringExtra("foodname");
            foodcategory = getIntent().getStringExtra("foodcategory");
            //Setting Intent Data
            foodnameupdate.setText(foodname);
            foodcategoryupdate.setText(foodcategory);
        }else{
            Toast.makeText(this,"No data",Toast.LENGTH_SHORT).show();
        }

    }
    void confirmDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete "+foodname+"?");
        builder.setMessage("Are you sure you want to delete "+foodname+"?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateActivity.this);
                myDB.deleteOneRow(_id);
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();

    }
}