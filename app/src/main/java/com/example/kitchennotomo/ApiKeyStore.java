package com.example.kitchennotomo;

import android.content.Context;
import android.content.SharedPreferences;

public final class ApiKeyStore {
    private static final String PREF = "secrets";
    private static final String KEY  = "openai_api_key";

    private ApiKeyStore() {} // ngăn tạo instance

    public static void save(Context ctx, String value) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit().putString(KEY, value).apply();
    }

    public static String get(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        return sp.getString(KEY, "");
    }

    public static void clear(Context ctx) {
        SharedPreferences sp = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        sp.edit().remove(KEY).apply();
    }
}
