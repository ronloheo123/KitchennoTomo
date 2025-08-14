package com.example.kitchennotomo;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {
    private ChatAdapter adapter;
    private RecyclerView rv;
    private EditText et;
    private ImageButton btnSend, btnSave;

    private final OkHttpClient http = new OkHttpClient();
    private final List<ChatMsg> history = new ArrayList<>();
    private String lastBotReply = ""; // Lưu câu trả lời gần nhất

    private MyDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        rv  = findViewById(R.id.rvChat);
        et  = findViewById(R.id.etMsg);
        btnSend = findViewById(R.id.btnSend);
        btnSave = findViewById(R.id.btnSave);

        dbHelper = new MyDatabaseHelper(this);

        adapter = new ChatAdapter();
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        rv.setClipToPadding(false);

        btnSend.setOnClickListener(v -> send());
        btnSave.setOnClickListener(v -> saveRecipesFromBotReply());

        et.setOnEditorActionListener((tv, actionId, e) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) { send(); return true; }
            return false;
        });

        ensureApiKey();
        postBot("Xin chào! Hãy nhập nguyên liệu, tôi sẽ gợi ý 3 món ăn.");
    }

    private void ensureApiKey() {
        String k = ApiKeyStore.get(this);
        if (!TextUtils.isEmpty(k)) return;

        final EditText input = new EditText(this);
        input.setHint("Nhập API key OpenRouter");

        new AlertDialog.Builder(this)
                .setTitle("Nhập API key")
                .setMessage("Key này sẽ được lưu cục bộ trên thiết bị.")
                .setView(input)
                .setCancelable(false)
                .setPositiveButton("Lưu", (d, w) -> {
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

        callDeepSeek();
    }

    private void postBot(String text) {
        lastBotReply = text; // Lưu câu trả lời gần nhất
        ChatMsg bot = new ChatMsg(ChatMsg.BOT, text);
        history.add(bot);
        runOnUiThread(() -> {
            adapter.add(bot);
            rv.scrollToPosition(adapter.getItemCount() - 1);
        });
    }

    private void callDeepSeek() {
        String apiKey = ApiKeyStore.get(this);
        if (TextUtils.isEmpty(apiKey)) {
            postBot("Lỗi: Chưa có API key.");
            return;
        }

        try {
            JSONObject body = new JSONObject();
            body.put("model", "deepseek/deepseek-r1:free");

            JSONArray messages = new JSONArray();
            messages.put(new JSONObject()
                    .put("role", "system")
                    .put("content",
                            "Bạn là trợ lý nấu ăn. Luôn trả lời theo định dạng sau và luôn luôn ra đúng 3 món:\n" +
                                    "Món 1: [Tên món 1]\nNguyên liệu cần: [Nguyên liệu 1], [Nguyên liệu 2], [Nguyên liệu 3]\nCách làm: [Mô tả]\n\n" +
                                    "Món 2: ...\n\n" +
                                    "Món 3: ...\n" +
                                    "Không thêm bất kỳ lời mở đầu hoặc kết thúc nào."
                    ));

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
                    .url("https://openrouter.ai/api/v1/chat/completions")
                    .header("Authorization", "Bearer " + apiKey)
                    .header("HTTP-Referer", "https://google.com")
                    .header("X-Title", "KitchennoTomo Android")
                    .post(RequestBody.create(body.toString(), JSON))
                    .build();

            http.newCall(req).enqueue(new Callback() {
                @Override public void onFailure(Call call, IOException e) {
                    postBot("Lỗi kết nối: " + e.getMessage());
                }

                @Override public void onResponse(Call call, Response resp) throws IOException {
                    String text = (resp.body() != null) ? resp.body().string() : "";
                    if (!resp.isSuccessful()) {
                        postBot("Lỗi API: " + resp.code());
                        return;
                    }
                    try {
                        JSONObject obj = new JSONObject(text);
                        String content = obj.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content")
                                .replaceAll("(?s)<think>.*?</think>", "")
                                .trim();
                        postBot(content);
                    } catch (Exception ex) {
                        postBot("Lỗi xử lý phản hồi.");
                    }
                }
            });

        } catch (Exception e) {
            postBot("Lỗi tạo request.");
        }
    }

    /** Parse và lưu vào DB */
    private void saveRecipesFromBotReply() {
        if (TextUtils.isEmpty(lastBotReply)) {
            Toast.makeText(this, "Chưa có dữ liệu để lưu", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] lines = lastBotReply.split("\n");
        String currentName = "";
        String currentDesc = "";

        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("Món") || line.startsWith("料理")) {
                currentName = line.replaceAll("Món \\d+: ", "").trim();
            } else if (line.startsWith("Cách làm") || line.startsWith("作り方")) {
                currentDesc = line.replace("Cách làm:", "").trim();
                if (!currentName.isEmpty() && !currentDesc.isEmpty()) {
                    dbHelper.addRecipe(currentName, currentDesc);
                    currentName = "";
                    currentDesc = "";
                }
            }
        }

        Toast.makeText(this, "Đã lưu công thức vào DB", Toast.LENGTH_SHORT).show();
    }
}
