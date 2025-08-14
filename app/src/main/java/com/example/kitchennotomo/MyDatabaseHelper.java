package com.example.kitchennotomo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private final Context context;
    private static final String DATABASE_NAME = "FoodRecipe.db";
    private static final int DATABASE_VERSION = 2;
    private static final String TABLE_NAME = "Food";

    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_FOOD_NAME = "foodname";
    private static final String COLUMN_FOOD_CATEGORY = "foodcategory";

    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_FOOD_NAME + " TEXT, "
                + COLUMN_FOOD_CATEGORY + " TEXT"
                + ");";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /** Thêm công thức mới */
    public long addRecipe(String foodname, String foodcategory) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_FOOD_NAME, foodname);
        cv.put(COLUMN_FOOD_CATEGORY, foodcategory);
        long result = -1;
        try {
            result = db.insertOrThrow(TABLE_NAME, null, cv);
            Toast.makeText(context, "Add Successfully!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(context, "DB error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            db.close();
        }
        return result;
    }

    /** Đọc toàn bộ dữ liệu */
    public Cursor readAllData() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    /** Cập nhật công thức */
    public void updateData(String row_id, String foodname, String foodcategory) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_FOOD_NAME, foodname);
        cv.put(COLUMN_FOOD_CATEGORY, foodcategory);
        long result = db.update(TABLE_NAME, cv, "_id=?", new String[]{row_id});
        if (result == -1) {
            Toast.makeText(context, "Failed to Update", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully Updated", Toast.LENGTH_SHORT).show();
        }
    }

    /** Xóa 1 công thức */
    public void deleteOneRow(String row_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        long results = db.delete(TABLE_NAME, "_id=?", new String[]{row_id});
        if (results == -1) {
            Toast.makeText(context, "Failed to Delete", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Successfully Deleted", Toast.LENGTH_SHORT).show();
        }
    }

    /** Xóa toàn bộ dữ liệu */
    public void deleteAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }
}
