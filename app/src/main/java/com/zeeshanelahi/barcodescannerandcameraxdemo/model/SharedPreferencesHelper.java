package com.zeeshanelahi.barcodescannerandcameraxdemo.model;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesHelper {
    private static final String PREFERENCES_NAME = "MyChatPreferences"; // Tên của SharedPreferences
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private static SharedPreferencesHelper instance;

    // Phương thức getInstance để lấy đối tượng duy nhất của lớp
    public static synchronized SharedPreferencesHelper getInstance(Context context) {
        if (instance == null) {
            instance = new SharedPreferencesHelper(context.getApplicationContext());
        }
        return instance;
    }

    // Khởi tạo SharedPreferencesHelper -> set thành private để không thể tạo đối tượng dùng trực tiếp
    // Mà phải dùng thông qua instance duy nhất của lớp.
    private SharedPreferencesHelper(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    // Ghi giá trị String vào SharedPreferences
    public void saveString(String key, String value) {
        editor.putString(key, value);
        editor.apply();
    }

    // Đọc giá trị String từ SharedPreferences
    public String getString(String key, String defaultValue) {
        return sharedPreferences.getString(key, defaultValue);
    }

    // Ghi giá trị boolean vào SharedPreferences
    public void saveBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        editor.apply();
    }

    // Đọc giá trị boolean từ SharedPreferences
    public boolean getBoolean(String key, boolean defaultValue) {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    // Ghi giá trị int vào SharedPreferences
    public void saveInt(String key, int value) {
        editor.putInt(key, value);
        editor.apply();
    }

    // Đọc giá trị int từ SharedPreferences
    public int getInt(String key, int defaultValue) {
        return sharedPreferences.getInt(key, defaultValue);
    }

    // Xóa giá trị từ SharedPreferences dựa trên key
    public void removeValue(String key) {
        editor.remove(key);
        editor.apply();
    }

}
