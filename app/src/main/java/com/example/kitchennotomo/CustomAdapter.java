package com.example.kitchennotomo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    private final Context context;
    private final Activity activity;
    private final ArrayList<String> _id;
    private final ArrayList<String> foodname;
    private final ArrayList<String> foodcategory;

    CustomAdapter(Activity activity, Context context,
                  ArrayList<String> _id,
                  ArrayList<String> foodname,
                  ArrayList<String> foodcategory) {
        this.activity = activity;
        this.context = context;
        this._id = _id;
        this.foodname = foodname;
        this.foodcategory = foodcategory;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_recipe, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.food_id_txt.setText(_id.get(position));
        holder.foodname_txt.setText(foodname.get(position));
        holder.foodcategory_txt.setText(foodcategory.get(position));

        holder.mainLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, UpdateActivity.class);
            intent.putExtra("_id", _id.get(position));
            intent.putExtra("foodname", foodname.get(position));
            intent.putExtra("foodcategory", foodcategory.get(position));
            activity.startActivityForResult(intent, 1);
        });
    }

    @Override
    public int getItemCount() {
        return _id.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView food_id_txt, foodname_txt, foodcategory_txt;
        LinearLayout mainLayout;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            food_id_txt = itemView.findViewById(R.id.food_id_txt);
            foodname_txt = itemView.findViewById(R.id.foodname_txt);
            foodcategory_txt = itemView.findViewById(R.id.foodcategory_txt);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}
