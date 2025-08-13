package com.example.kitchennotomo;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private ChatAdapter adapter;
    private RecyclerView rv;
    private EditText et;
    private ImageButton btn;

    private final OkHttpClient http = new OkHttpClient();
    private final List<ChatMsg> history = new ArrayList<>();

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat);

        // Áp dụng WindowInsets nếu root có id="main"
        View root = findViewById(R.id.main);
        if (root != null) {
            ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
                Insets sb = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(sb.left, sb.top, sb.right, sb.bottom);
                return insets;
            });
        }

        rv  = findViewById(R.id.rvChat);
        et  = findViewById(R.id.etMsg);
        btn = findViewById(R.id.btnSend);

        adapter = new ChatAdapter();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        rv.setClipToPadding(false);

        btn.setOnClickListener(v -> send());
        et.setOnEditorActionListener((tv, actionId, e) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) { send(); return true; }
            return false;
        });

        ensureApiKey(); // hỏi key nếu chưa có

        // Lời chào ban đầu (tiếng Nhật)
        postBot("こんにちは！食材からレシピを提案できます。何がありますか？");
    }

    private void ensureApiKey() {
        String k = ApiKeyStore.get(this);
        if (!TextUtils.isEmpty(k)) return;

        final EditText input = new EditText(this);
        input.setHint("sk-xxxxxxxxxxxxxxxx");

        new AlertDialog.Builder(this)
                .setTitle("OpenAI APIキーを入力")
                .setMessage("このキーは端末内（SharedPreferences）にのみ保存されます。")
                .setView(input)
                .setCancelable(false)
                .setPositiveButton("保存", (d, w) -> {
                    String v = input.getText().toString().trim();
                    if (!TextUtils.isEmpty(v)) {
                        ApiKeyStore.save(this, v);
                    }
                })
                .show();
    }

    private void send() {
        String msg = et.getText().toString().trim();
        if (TextUtils.isEmpty(msg)) return;
        et.setText("");

        ChatMsg user = new ChatMsg(ChatMsg.USER, msg);
        history.add(user);
        adapter.add(user);
        rv.scrollToPosition(adapter.getItemCount() - 1);

        callOpenAI(); // gọi API sau khi đã thêm vào history
    }

    private void postBot(String text) {
        ChatMsg bot = new ChatMsg(ChatMsg.BOT, text);
        history.add(bot);
        runOnUiThread(() -> {
            adapter.add(bot);
            rv.scrollToPosition(adapter.getItemCount() - 1);
        });
    }

    private void callOpenAI() { callOpenAIWithRetry(0); }

    private void callOpenAIWithRetry(int attempt) {
        String apiKey = ApiKeyStore.get(this);
        if (TextUtils.isEmpty(apiKey)) {
            postBot("（エラー）APIキーが未設定です。メニューからキーを入力してください。");
            return;
        }

        try {
            // Body cho Chat Completions
            JSONObject body = new JSONObject();
            body.put("model", "gpt-4o-mini");
            body.put("max_tokens", 256);   // giới hạn độ dài trả lời
            body.put("temperature", 0.6);

            // Chỉ gửi 8 lượt gần nhất để giảm tokens
            JSONArray messages = new JSONArray();
            messages.put(new JSONObject()
                    .put("role", "system")
                    .put("content", "You are a friendly cooking assistant. Answer concisely and suggest recipes from available ingredients in Japanese."));
            int start = Math.max(0, history.size() - 8);
            for (int i = start; i < history.size(); i++) {
                ChatMsg m = history.get(i);
                messages.put(new JSONObject()
                        .put("role", m.role == ChatMsg.USER ? "user" : "assistant")
                        .put("content", m.text));
            }
            body.put("messages", messages);

            MediaType JSON = MediaType.get("application/json; charset=utf-8");
            Request req = new Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .post(RequestBody.create(body.toString(), JSON))
                    .build();

            http.newCall(req).enqueue(new Callback() {
                @Override public void onFailure(Call call, IOException e) {
                    postBot("（エラー）接続に失敗しました。もう一度お試しください。");
                }

                @Override public void onResponse(Call call, Response resp) throws IOException {
                    String text = (resp.body() != null) ? resp.body().string() : "";

                    if (resp.code() == 429) {
                        // quota hết?
                        if (text.contains("insufficient_quota") || text.toLowerCase().contains("usage limit")) {
                            postBot("（エラー）ご利用上限に達しました。請求設定または使用制限を確認してください。");
                            return;
                        }
                        // rate limit: retry với backoff / Retry-After
                        long delayMs = 0;
                        String ra = resp.header("Retry-After");
                        if (ra != null) try { delayMs = Long.parseLong(ra) * 1000L; } catch (Exception ignore) {}
                        if (delayMs == 0) delayMs = (long) Math.min(8000, Math.pow(2, attempt) * 1000); // 1s, 2s, 4s...
                        if (attempt < 2) {
                            postBot("（エラー）リクエストが多すぎます。約 " + (delayMs/1000) + " 秒後に再試行します。");
                            long d = delayMs;
                            runOnUiThread(() ->
                                    new android.os.Handler(getMainLooper())
                                            .postDelayed(() -> callOpenAIWithRetry(attempt + 1), d));
                        } else {
                            postBot("（エラー）しばらくしてから、もう一度お試しください。");
                        }
                        return;
                    }

                    if (!resp.isSuccessful()) {
                        if (text.contains("insufficient_quota") || text.toLowerCase().contains("usage limit")) {
                            postBot("（エラー）ご利用上限に達しました。請求設定または使用制限を確認してください。");
                        } else {
                            postBot("（エラー）応答コード: " + resp.code());
                        }
                        return;
                    }

                    try {
                        JSONObject obj = new JSONObject(text);
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
            postBot("（エラー）リクエストの作成に失敗しました。");
        }
    }
}
