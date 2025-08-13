package com.example.kitchennotomo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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

    // Dữ liệu cho CustomAdapter
    private ArrayList<String> ids = new ArrayList<>();
    private ArrayList<String> names = new ArrayList<>();
    private ArrayList<String> categories = new ArrayList<>();
    private CustomAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash);

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

        // Dữ liệu mẫu
        ids.clear();
        names.clear();
        categories.clear();
        ids.add("1"); names.add("たまごチャーハン"); categories.add("中華");
        ids.add("2"); names.add("トマトスープ"); categories.add("スープ");

        // RecyclerView + CustomAdapter
        RecyclerView rv = findViewById(R.id.rvRecipes);
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
                String q = s.toString().trim();
                filterList(q);
            }
        });

        MaterialCardView add_button = findViewById(R.id.add_button);
        if (add_button != null) {
            add_button.setOnClickListener(v -> {
                startActivity(new android.content.Intent(this, AddActivity.class));
            });
        }
        // FAB chat
        ImageButton fab = findViewById(R.id.fabChat);
        if (fab != null) {
            fab.setOnClickListener(v ->
                    startActivity(new android.content.Intent(this, ChatActivity.class))
            );
        }
    }
    // Nút Thêm Recipe

    private void filterList(String query) {
        ArrayList<String> fIds = new ArrayList<>();
        ArrayList<String> fNames = new ArrayList<>();
        ArrayList<String> fCats = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            if (names.get(i).contains(query) || categories.get(i).contains(query)) {
                fIds.add(ids.get(i));
                fNames.add(names.get(i));
                fCats.add(categories.get(i));
            }
        }
        adapter = new CustomAdapter(this, this, fIds, fNames, fCats);
        RecyclerView rv = findViewById(R.id.rvRecipes);
        rv.setAdapter(adapter);
    }
}
