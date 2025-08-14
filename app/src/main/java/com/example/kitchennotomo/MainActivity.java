package com.example.kitchennotomo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> ids = new ArrayList<>();
    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<String> categories = new ArrayList<>();
    private CustomAdapter adapter;
    private RecyclerView rv;
    private MyDatabaseHelper myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash);

        // Sau 0.9s mới vào UI chính
        new Handler(Looper.getMainLooper()).postDelayed(this::initHomeUi, 900);
    }

    private void initHomeUi() {
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        View root = findViewById(R.id.main);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
                return insets;
            });
        }

        // Khởi tạo DB
        myDB = new MyDatabaseHelper(this);

        // RecyclerView
        rv = findViewById(R.id.rvRecipes);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setClipToPadding(false);

        adapter = new CustomAdapter(this, this, ids, names, categories);
        rv.setAdapter(adapter);

        // Search
        EditText et = findViewById(R.id.etSearch);
        et.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {}
            @Override public void afterTextChanged(Editable s) {
                filterList(s.toString().trim());
            }
        });

        // Nút thêm recipe
        MaterialCardView addButton = findViewById(R.id.add_button);
        if (addButton != null) {
            addButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, AddActivity.class);
                startActivity(intent);
            });
        }

        // Nút chat
        ImageButton fab = findViewById(R.id.fabChat);
        if (fab != null) {
            fab.setOnClickListener(v ->
                    startActivity(new Intent(this, ChatActivity.class))
            );
        }

        // Load data ban đầu
        loadDataFromDatabase();
    }

    private void filterList(String query) {
        ArrayList<String> fIds = new ArrayList<>();
        ArrayList<String> fNames = new ArrayList<>();
        ArrayList<String> fCats = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            if (names.get(i).toLowerCase().contains(query.toLowerCase()) ||
                    categories.get(i).toLowerCase().contains(query.toLowerCase())) {
                fIds.add(ids.get(i));
                fNames.add(names.get(i));
                fCats.add(categories.get(i));
            }
        }
        rv.setAdapter(new CustomAdapter(this, this, fIds, fNames, fCats));
    }

    private void loadDataFromDatabase() {
        Cursor cursor = myDB.readAllData();

        ids.clear();
        names.clear();
        categories.clear();

        while (cursor.moveToNext()) {
            ids.add(cursor.getString(0));
            names.add(cursor.getString(1));
            categories.add(cursor.getString(2));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Chỉ load lại khi UI đã khởi tạo
        if (rv != null && adapter != null && myDB != null) {
            loadDataFromDatabase();
        }
    }
}
