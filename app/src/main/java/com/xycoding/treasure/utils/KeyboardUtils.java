package com.xycoding.treasure.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class KeyboardUtils {

    public static void hideSoftKeyboard(Context context, View view) {
        if (context != null && view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void toggleSoftKeyBoard(Context context, View view) {
        if (context != null && view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInputFromWindow(view.getWindowToken(), 0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void showSoftKeyBoard(Context context, View view) {
        if (context != null && view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.showSoftInput(view, InputMethodManager.SHOW_FORCED);
        }
    }
}
