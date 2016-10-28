package com.xycoding.treasure.utils;

import android.text.TextUtils;
import android.util.Log;

import com.xycoding.treasure.BuildConfig;

public class Logcat {

    /**
     * log.d
     * 
     * @param tag
     * @param msg
     */
    public static void d(String tag, String msg) {
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg)) {
            return;
        }
        if (BuildConfig.DEBUG) {
            Log.d(tag, msg);
        }
    }

    /**
     * log.e
     * 
     * @param tag
     * @param msg
     */
    public static void e(String tag, String msg) {
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg)) {
            return;
        }
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg);
        }
    }

    /**
     * log.e
     * 
     * @param tag
     * @param msg
     */
    public static void e(String tag, String msg, Throwable throwable) {
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg)) {
            return;
        }
        if (BuildConfig.DEBUG) {
            Log.e(tag, msg, throwable);
        }
    }

    /**
     * log.w
     * 
     * @param tag
     * @param msg
     */
    public static void w(String tag, String msg) {
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg)) {
            return;
        }
        if (BuildConfig.DEBUG) {
            Log.w(tag, msg);
        }
    }

    /**
     * Log.i
     * 
     * @param tag
     * @param msg
     */
    public static void i(String tag, String msg) {
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg)) {
            return;
        }
        if (BuildConfig.DEBUG) {
            Log.i(tag, msg);
        }
    }

    /**
     * log.v
     * 
     * @param tag
     * @param msg
     */
    public static void v(String tag, String msg) {
        if (TextUtils.isEmpty(tag) || TextUtils.isEmpty(msg)) {
            return;
        }
        if (BuildConfig.DEBUG) {
            Log.v(tag, msg);
        }
    }
}
