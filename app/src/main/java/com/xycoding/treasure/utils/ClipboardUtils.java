package com.xycoding.treasure.utils;

import android.annotation.TargetApi;
import android.content.ClipData;
import android.content.Context;
import android.os.Build;

/**
 * Created by xuyang on 15/11/18.
 */
public class ClipboardUtils {

    private final static String LABEL_CLIPBOARD = "clipboard";

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static void copy(Context context, CharSequence text) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            clipboard.setText(text);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText(LABEL_CLIPBOARD, text);
            clipboard.setPrimaryClip(clip);
        }
    }

}
