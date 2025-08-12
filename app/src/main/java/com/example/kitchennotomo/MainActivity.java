package com.example.kitchennotomo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.Callbacks {

    // dữ liệu mẫu (sau này thay bằng DB)
    private final List<Recipe> all = new ArrayList<>();
    private RecipeAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 1) System splash (Android 12+)
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);

        // 2) Dùng layout splash riêng của bạn trước
        setContentView(R.layout.splash); // <-- splash.xml của bạn (đừng nhầm với activity_main)

        // (tuỳ chọn) animate logo/text trong splash.xml ở đây
        // ImageView logo = findViewById(R.id.ivLogo);
        // logo.startAnimation(AnimationUtils.loadAnimation(this, R.anim.fade_in_up));

        // 3) Sau 800–1200ms chuyển sang UI chính
        new Handler(Looper.getMainLooper()).postDelayed(this::initHomeUi, 900);
    }

    private void initHomeUi() {
        // 3.1) Thay layout thành màn chính
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // 3.2) Inset cho id @id/main (phải có trong activity_main.xml)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
            return insets;
        });

        // 3.3) Dữ liệu mẫu
        all.clear();
        all.add(new Recipe("カレーライス", "ぶたにく、玉ねぎ、じゃがいも、人参、塩、砂糖…"));
        all.add(new Recipe("唐揚げ", "鶏肉、玉ねぎ、塩、味噌、醤油、小麦粉、卵…"));
        all.add(new Recipe("唐揚", "鶏肉、塩、醤油、生姜…"));

        // 3.4) RecyclerView + Adapter
        RecyclerView rv = findViewById(R.id.rvRecipes);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecipeAdapter(this);
        rv.setAdapter(adapter);
        adapter.setItems(all);

        // 3.5) Search
        EditText et = findViewById(R.id.etSearch);
        et.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void onTextChanged(CharSequence s, int st, int b, int c) {}
            @Override public void afterTextChanged(Editable s) {
                String q = s.toString().trim();
                if (q.isEmpty()) { adapter.setItems(all); return; }
                List<Recipe> filtered = new ArrayList<>();
                for (Recipe r : all) {
                    if (r.title.contains(q) || r.ingredientsSummary.contains(q)) filtered.add(r);
                }
                adapter.setItems(filtered);
            }
        });

        // 3.6) FAB chat
        ImageButton fab = findViewById(R.id.fabChat);
        findViewById(R.id.fabChat).setOnClickListener(v ->
                startActivity(new android.content.Intent(this, ChatActivity.class))
        );
    }

    // Callbacks từ adapter
    @Override public void onRecipeClick(Recipe r, int pos) { /* TODO: mở chi tiết */ }
    @Override public void onAddClick() { /* TODO: mở màn thêm công thức */ }
}
