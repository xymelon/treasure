package com.xycoding.treasure.view;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatDialog;

/**
 * Created by xuyang on 2016/11/10.
 */
public class QuickActionDialog extends AppCompatDialog {

    public QuickActionDialog(Context context) {
        super(context);
        init();
    }

    public QuickActionDialog(Context context, int theme) {
        super(context, theme);
        init();
    }

    protected QuickActionDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init();
    }

    private void init() {
        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

            }
        });
    }
}
