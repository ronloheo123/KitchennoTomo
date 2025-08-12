package com.example.kitchennotomo;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private ChatAdapter adapter;
    private RecyclerView rv;
    private EditText et;
    private final OkHttpClient http = new OkHttpClient();

    // Lưu lịch sử để gửi kèm cho model (ngắn gọn)
    private final List<ChatMsg> history = new ArrayList<>();

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        rv = findViewById(R.id.rvChat);
        et = findViewById(R.id.etMsg);
        ImageButton btn = findViewById(R.id.btnSend);

        adapter = new ChatAdapter();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        btn.setOnClickListener(v -> send());
        et.setOnEditorActionListener((tv, actionId, e) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) { send(); return true; }
            return false;
        });

        // Lời chào ban đầu
        postBot("こんにちは！食材からレシピを提案できます。何がありますか？");
    }

    private void send() {
        String msg = et.getText().toString().trim();
        if (TextUtils.isEmpty(msg)) return;
        et.setText("");
        ChatMsg user = new ChatMsg(ChatMsg.USER, msg);
        history.add(user);
        adapter.add(user);
        rv.scrollToPosition(adapter.getItemCount()-1);

        // Gọi OpenAI
        callOpenAI(msg);
    }

    private void postBot(String text){
        ChatMsg bot = new ChatMsg(ChatMsg.BOT, text);
        history.add(bot);
        runOnUiThread(() -> {
            adapter.add(bot);
            rv.scrollToPosition(adapter.getItemCount()-1);
        });
    }

    private void callOpenAI(String userMessage) {
        try {
            // --- Dùng Chat Completions (ổn định & dễ nhớ) ---
            JSONObject body = new JSONObject();
            body.put("model", "gpt-4o-mini"); // hoặc "gpt-5" với Responses API, tuỳ gói của bạn. :contentReference[oaicite:0]{index=0}

            JSONArray messages = new JSONArray();
            messages.put(new JSONObject()
                    .put("role","system")
                    .put("content","You are a friendly cooking assistant. Answer briefly and suggest recipes from available ingredients."));
            // nạp vài lượt gần nhất để giữ ngắn gọn
            int start = Math.max(0, history.size()-10);
            for (int i=start;i<history.size();i++){
                ChatMsg m = history.get(i);
                messages.put(new JSONObject()
                        .put("role", m.role==ChatMsg.USER ? "user":"assistant")
                        .put("content", m.text));
            }
            // lời mới nhất của user đã có trong history ở trên
            body.put("messages", messages);

            Request req = new Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .header("Authorization", "Bearer " + BuildConfig.OPENAI_API_KEY)
                    .header("Content-Type", "application/json")
                    .post(RequestBody.create(body.toString(), MediaType.parse("application/json")))
                    .build();

            http.newCall(req).enqueue(new Callback() {
                @Override public void onFailure(Call call, IOException e) {
                    postBot("（エラー）接続に失敗しました。もう一度お試しください。");
                }
                @Override public void onResponse(Call call, Response resp) throws IOException {
                    if (!resp.isSuccessful()) { postBot("（エラー）" + resp.code()); return; }
                    String json = resp.body().string();
                    try {
                        JSONObject obj = new JSONObject(json);
                        String content = obj.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                        postBot(content.trim());
                    } catch (Exception ex) {
                        postBot("（エラー）応答の解析に失敗しました。");
                    }
                }
            });
        } catch (Exception e) {
            postBot("（エラー）要求の作成に失敗しました。");
        }
    }
}
