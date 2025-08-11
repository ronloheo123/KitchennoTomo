package com.example.kitchennotomo;

import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {


    EditText FoodName,FoodCategory;
    TextView IdText;
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
            IdText = findViewById(R.id.ID);
            FoodName= findViewById(R.id.FoodName);
            insert= findViewById(R.id.btnInsert);
            update= findViewById(R.id.btnUpdate);
            delete=findViewById(R.id.btnDelete);
            view=findViewById(R.id.btnView);
            DB = new DBHelper(this);
            //INSERT
            insert.setOnClickListener(v -> {
            String FoodNameTXT = FoodName.getText().toString();
            String FoodCategoryTXT = FoodCategory.getText().toString();
            Boolean checkinsertdata= DB.insertFood(FoodNameTXT,FoodCategoryTXT);
            if(checkinsertdata==true)
                Toast.makeText(MainActivity.this,"New Entry Interted",Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(MainActivity.this,"New Entry Not Inserted",Toast.LENGTH_SHORT).show();
        });


            update.setOnClickListener(v -> {

            String FoodNameTXT = FoodName.getText().toString();
            String FoodCategoryTXT = FoodCategory.getText().toString();
            String IdTXT= IdText.getText().toString();
            int idnumber=Integer.parseInt(IdTXT);
            Boolean checkupdatedata= DB.updateFood(idnumber,FoodNameTXT,FoodCategoryTXT);
            if(checkupdatedata==true)
                Toast.makeText(MainActivity.this,"Update successful",Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(MainActivity.this,"Update failed",Toast.LENGTH_SHORT).show();
        });


        delete.setOnClickListener(v -> {
            String idTXT = IdText.getText().toString().trim();
            int idNumber = Integer.parseInt(idTXT);

            boolean checkdeletedata = DB.deleteFood(idNumber);
            if (checkdeletedata) {
                Toast.makeText(MainActivity.this, "Delete Succesful", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Delete Fail", Toast.LENGTH_SHORT).show();
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor res = DB.getData();
                if(res.getCount()==0){
                    Toast.makeText(MainActivity.this,"No Entry Exists",Toast.LENGTH_SHORT).show();
                    return;
                }
                StringBuffer buffer= new StringBuffer();
                while(res.moveToNext()){
                    buffer.append("ID :"+res.getInt(0)+"\n");
                    buffer.append("FoodName :"+res.getString(1)+"\n");
                    buffer.append("FoodCategory :"+res.getString(2)+"\n\n");
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setCancelable(true);
                builder.setTitle("Food Entries");
                builder.show();

            }
        });


    }
}