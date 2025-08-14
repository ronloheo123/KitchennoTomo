package com.example.kitchennotomo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class UpdateActivity extends AppCompatActivity {

    EditText foodnameupdate, foodcategoryupdate;
    Button update_button, delete_button;
    String _id, foodname, foodcategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update);

        foodnameupdate = findViewById(R.id.foodnameupdate);
        foodcategoryupdate = findViewById(R.id.foodcategoryupdate);
        update_button = findViewById(R.id.update_button);
        delete_button = findViewById(R.id.delete_button);

        getAndSetIntentData();

        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setTitle(foodname);
        }

        update_button.setOnClickListener(v -> {
            MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateActivity.this);
            myDB.updateData(_id, foodnameupdate.getText().toString().trim(),
                    foodcategoryupdate.getText().toString().trim());
            Toast.makeText(this, "Updated successfully", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        });

        delete_button.setOnClickListener(v -> confirmDialog());
    }

    void getAndSetIntentData() {
        if (getIntent().hasExtra("_id") && getIntent().hasExtra("foodname") &&
                getIntent().hasExtra("foodcategory")) {
            _id = getIntent().getStringExtra("_id");
            foodname = getIntent().getStringExtra("foodname");
            foodcategory = getIntent().getStringExtra("foodcategory");
            foodnameupdate.setText(foodname);
            foodcategoryupdate.setText(foodcategory);
        } else {
            Toast.makeText(this, "No data", Toast.LENGTH_SHORT).show();
        }
    }

    void confirmDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete " + foodname + "?");
        builder.setMessage("Are you sure you want to delete " + foodname + "?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            MyDatabaseHelper myDB = new MyDatabaseHelper(UpdateActivity.this);
            myDB.deleteOneRow(_id);
            Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        });
        builder.setNegativeButton("No", (dialog, which) -> {});
        builder.create().show();
    }
}
