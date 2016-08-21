package com.example.gcs.faster5.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by khacpham on 7/16/16.
 */
public class PrefUtils {

    public static final String PREF_NAME = "MyPrefs";

    public static final String KEY_FIRST_USE = "first_use";
    public static final String KEY_LOGGED_IN = "logged_in";
    public static final String KEY_ACCESS_TOKEN_FB = "access_token";
    public static final String KEY_NAME = "name";
    public static final String KEY_MONEY = "money";
    public static final String KEY_URL_AVATAR = "url";
    public static final String KEY_LOCATION = "localtion";
    public static final String KEY_USER_ID = "id";
    public static final String KEY_ENEMY_ID = "enemy_id";
    public static final String KEY_ENEMY_NAME = "enemy_name";
    public static final String KEY_ENEMY_AVATAR = "enemy_avatar";
    public static final String KEY_ENEMY_MONEY = "enemy_money";
    public static final String KEY_ENEMY_LOCATION = "enemy_location";
    public static final String KEY_ROOM_ID = "room_id";


    private static PrefUtils _instance;

    SharedPreferences prefs;

    private PrefUtils(Context context) {
        prefs = context.getSharedPreferences(
                PREF_NAME, Context.MODE_PRIVATE);

    }

    public static PrefUtils getInstance(Context context) {
        if (_instance == null) {
            _instance = new PrefUtils(context);
        }
        return _instance;
    }

    public void set(String key, String value) {
        prefs.edit().putString(key, value).apply();
    }

    public String get(String key, String defValue) {
        return prefs.getString(key, defValue);
    }

    public void set(String key, int value) {
        prefs.edit().putInt(key, value).apply();
    }

    public int get(String key, int defValue) {
        return prefs.getInt(key, defValue);
    }

    public void set(String key, boolean value) {
        prefs.edit().putBoolean(key, value).apply();
    }

    public boolean get(String key, boolean defValue) {
        return prefs.getBoolean(key, defValue);
    }

    public void set(String key, long value) {
        prefs.edit().putLong(key, value).apply();
    }

    public long get(String key, long defValue) {
        return prefs.getLong(key, defValue);
    }
}
