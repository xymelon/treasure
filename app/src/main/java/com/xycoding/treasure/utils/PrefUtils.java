package com.xycoding.treasure.utils;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by xuyang on 2016/7/26.
 */
public class PrefUtils {

    private static Context sContext;

    public static void init(Context context) {
        sContext = context.getApplicationContext();
    }

    public static int getInt(String key, int defValue) {
        return PreferenceManager.getDefaultSharedPreferences(sContext).getInt(key, defValue);
    }

    public static void putInt(String key, int value) {
        PreferenceManager.getDefaultSharedPreferences(sContext).edit().putInt(key, value).apply();
    }

    public static float getFloat(String key, float defValue) {
        return PreferenceManager.getDefaultSharedPreferences(sContext).getFloat(key, defValue);
    }

    public static void putFloat(String key, float value) {
        PreferenceManager.getDefaultSharedPreferences(sContext).edit().putFloat(key, value).apply();
    }

    public static String getString(String key, String defValue) {
        return PreferenceManager.getDefaultSharedPreferences(sContext).getString(key, defValue);
    }

    public static void putString(String key, String value) {
        PreferenceManager.getDefaultSharedPreferences(sContext).edit().putString(key, value).apply();
    }

    public static long getLong(String key, long defValue) {
        return PreferenceManager.getDefaultSharedPreferences(sContext).getLong(key, defValue);
    }

    public static void putLong(String key, long value) {
        PreferenceManager.getDefaultSharedPreferences(sContext).edit().putLong(key, value).apply();
    }

    public static boolean getBoolean(String key, boolean defValue) {
        return PreferenceManager.getDefaultSharedPreferences(sContext).getBoolean(key, defValue);
    }

    public static void putBoolean(String key, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(sContext).edit().putBoolean(key, value).apply();
    }

    public static void remove(String key) {
        PreferenceManager.getDefaultSharedPreferences(sContext).edit().remove(key).apply();
    }

    public static boolean contains(String key) {
        return PreferenceManager.getDefaultSharedPreferences(sContext).contains(key);
    }
}
