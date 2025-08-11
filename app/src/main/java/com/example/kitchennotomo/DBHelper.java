package com.example.kitchennotomo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "Food.db";
    private static final int DB_VERSION = 1; //

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE IF NOT EXISTS Food (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +   // AUTOINCREMENT
                        "FoodName TEXT NOT NULL, " +
                        "CategoryFood TEXT" +
                        ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Food");
        onCreate(db);
    }

    // CREATE
    public boolean insertFood(String foodName, String categoryFood) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("FoodName", foodName);
        values.put("CategoryFood", categoryFood);
        long result = db.insert("Food", null, values);
        db.close();
        return result != -1;
    }

    // READ
    public Cursor getData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Food ORDER BY id DESC", null);
    }

    // UPDATE
    public boolean updateFood(int id, String newFoodName, String newCategoryFood) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (newFoodName != null) values.put("FoodName", newFoodName);
        if (newCategoryFood != null) values.put("CategoryFood", newCategoryFood);
        int rows = db.update("Food", values, "id=?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }

    // DELETE
    public boolean deleteFood(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete("Food", "id=?", new String[]{String.valueOf(id)});
        db.close();
        return rows > 0;
    }
}
