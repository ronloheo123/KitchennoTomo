package com.example.kitchennotomo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_USER = 0;
    private static final int TYPE_BOT  = 1;
    private final List<ChatMsg> items = new ArrayList<>();

    public void add(ChatMsg m) { items.add(m); notifyItemInserted(items.size()-1); }
    public void setAll(List<ChatMsg> ms){ items.clear(); items.addAll(ms); notifyDataSetChanged(); }
    public List<ChatMsg> data(){ return items; }

    @Override public int getItemViewType(int position) {
        return items.get(position).role == ChatMsg.USER ? TYPE_USER : TYPE_BOT;
    }
    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        View v = inf.inflate(type == TYPE_USER ? R.layout.item_msg_user : R.layout.item_msg_bot, parent, false);
        return new VH(v);
    }
    @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int pos) {
        ChatMsg m = items.get(pos);
        ((VH) h).tv.setText(m.text);
    }
    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        final TextView tv; VH(View v){ super(v); tv = v.findViewById(R.id.tvText); }
    }
}
