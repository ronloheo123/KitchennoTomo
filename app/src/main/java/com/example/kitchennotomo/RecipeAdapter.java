package com.example.kitchennotomo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_ADD  = 1;

    public interface Callbacks {
        void onRecipeClick(Recipe r, int pos);
        void onAddClick();
    }

    private final Callbacks callbacks;
    private final List<Recipe> data = new ArrayList<>();

    public RecipeAdapter(Callbacks callbacks) { this.callbacks = callbacks; }

    public void setItems(List<Recipe> items) {
        data.clear();
        if (items != null) data.addAll(items);
        notifyDataSetChanged();
    }

    @Override public int getItemViewType(int position) {
        return position < data.size() ? TYPE_ITEM : TYPE_ADD;
    }

    @NonNull @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_ADD) {
            View v = inf.inflate(R.layout.item_add_recipe, parent, false);
            return new AddVH(v);
        } else {
            View v = inf.inflate(R.layout.item_recipe, parent, false);
            return new ItemVH(v);
        }
    }

    @Override public void onBindViewHolder(@NonNull RecyclerView.ViewHolder h, int pos) {
        if (h instanceof ItemVH) {
            Recipe r = data.get(pos);
            ((ItemVH) h).bind(r);
            h.itemView.setOnClickListener(v -> callbacks.onRecipeClick(r, pos));
        } else {
            h.itemView.setOnClickListener(v -> callbacks.onAddClick());
        }
    }

    @Override public int getItemCount() { return data.size() + 1; } // +1 = ô “Thêm”

    static class ItemVH extends RecyclerView.ViewHolder {
        final TextView tvTitle, tvSubtitle;
        ItemVH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSubtitle = itemView.findViewById(R.id.tvSubtitle);
        }
        void bind(Recipe r) {
            tvTitle.setText(r.title);
            tvSubtitle.setText("材料: " + r.ingredientsSummary);
        }
    }
    static class AddVH extends RecyclerView.ViewHolder {
        AddVH(@NonNull View itemView) { super(itemView); }
    }
}
