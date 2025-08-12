package com.example.kitchennotomo;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class ChatActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: thay bằng layout chat thật
        setContentView(new android.widget.FrameLayout(this));
    }
}
