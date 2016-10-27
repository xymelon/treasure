package com.xycoding.treasure.utils;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import com.xycoding.treasure.R;

/**
 * Created by xuyang on 2016/8/1.
 */
public class ViewUtils {

    public static Dialog createLoadingDialog(@NonNull Context context) {
        return createLoadingDialog(context, true);
    }

    public static Dialog createLoadingDialog(@NonNull Context context, boolean cancelable) {
        return new AlertDialog
                .Builder(context, R.style.Dialog_NoTitleAndTransparent)
                .setView(R.layout.layout_loading)
                .setCancelable(cancelable)
                .create();
    }

    public static Dialog createSheetDialog(@NonNull Context context, @NonNull View contentView) {
        Dialog dialog = new AlertDialog.Builder(context, R.style.Dialog_NoTitleAndTransparent_Sheet)
                .setView(contentView)
                .create();
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.gravity = Gravity.BOTTOM;
        lp.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE;
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(lp);
        return dialog;
    }

}
