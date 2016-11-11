package com.xycoding.treasure.view;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatDialog;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xycoding.treasure.utils.DeviceUtils;

/**
 * Created by xuyang on 2016/11/10.
 */
public class QuickActionDialog extends AppCompatDialog {

    private int mScreenHeight;
    private int mStatusBarHeight;
    private OnShowListener mShowListener;

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
        mScreenHeight = DeviceUtils.getScreenHeight(getContext());
        mStatusBarHeight = DeviceUtils.getStatusBarHeight(getContext());
        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                if (mShowListener != null) {
                    mShowListener.onShow(dialog);
                }
            }
        });
    }

    @Override
    public void setOnShowListener(OnShowListener listener) {
        //prevent overlay custom listener
        mShowListener = listener;
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
    }

    public void showAtLocation(@NonNull View view) {
    }

    /**
     * Display the content view in a dialog at the specified location.
     *
     * @param screenX global (root) coordinates x
     * @param screenY global (root) coordinates Y
     */
    public void showAtLocation(int screenX, int screenY) {

    }

//    private void setParams(int screenX, int screenY) {
//        Window window = getWindow();
//        if (window != null) {
//            WindowManager.LayoutParams params = window.getAttributes();
//            params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
//            //calculate dialog position
//            int contentHeight = window.getDecorView().getHeight();
//            ImageView arrowView;
//            int dialogY;
//            boolean showBelow = mScreenHeight - screenY > contentHeight;
//            if (showBelow) {
//                arrowView = binding.ivArrowUp;
//                binding.ivArrowUp.setVisibility(View.VISIBLE);
//                binding.ivArrowDown.setVisibility(View.GONE);
//                dialogY = screenY - getStatusBarHeight(getApplicationContext()) + dp2px(getApplicationContext(), 2);
//            } else {
//                arrowView = binding.ivArrowDown;
//                binding.ivArrowUp.setVisibility(View.GONE);
//                binding.ivArrowDown.setVisibility(View.VISIBLE);
//                dialogY = screenY - getStatusBarHeight(getApplicationContext()) - contentHeight - dp2px(getApplicationContext(), 52);
//            }
//            params.y = dialogY;
//            window.setAttributes(params);
//            //计算箭头位置
//            LinearLayout.LayoutParams viewParams = (LinearLayout.LayoutParams) arrowView.getLayoutParams();
//            viewParams.leftMargin = screenX;
//            arrowView.setLayoutParams(viewParams);
//            //动画
//            binding.getRoot().setPivotX(screenX);
//            binding.getRoot().setPivotY(showBelow ? 0 : contentHeight);
//            AnimatorSet set = new AnimatorSet();
//            set.play(ObjectAnimator.ofFloat(binding.getRoot(), View.SCALE_X, 0.5f, 1f))
//                    .with(ObjectAnimator.ofFloat(binding.getRoot(), View.SCALE_Y, 0.5f, 1f));
//            set.setDuration(200);
//            set.setInterpolator(new DecelerateInterpolator());
//            set.start();
//        }
//    }

}
