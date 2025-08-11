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
                        "FoodCategory TEXT" +
                        ");"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Food");
        onCreate(db);
    }

    // CREATE
    public boolean insertFood(String foodName, String FoodCategory) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("FoodName", foodName);
        values.put("FoodCategory", FoodCategory);
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
    public boolean updateFood(int id, String newFoodName, String newFoodCategory) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if (newFoodName != null) values.put("FoodName", newFoodName);
        if (newFoodCategory != null) values.put("FoodCategory", newFoodCategory);
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
