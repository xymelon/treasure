package com.xycoding.treasure.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.xycoding.treasure.R;
import com.xycoding.treasure.databinding.ActivityDialogBinding;
import com.xycoding.treasure.databinding.LayoutQuickActionDialogBinding;
import com.xycoding.treasure.databinding.LayoutSheetDialogBinding;
import com.xycoding.treasure.rx.RxViewWrapper;
import com.xycoding.treasure.utils.ViewUtils;

import java.lang.reflect.Field;

import rx.functions.Action1;

/**
 * Created by xuyang on 2016/10/28.
 */
public class DialogActivity extends BaseBindingActivity {

    private ActivityDialogBinding mBinding;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_dialog;
    }

    @Override
    protected void initControls(Bundle savedInstanceState) {
        mBinding = (ActivityDialogBinding) binding;
    }

    @Override
    protected void setListeners() {
        subscriptions.add(RxViewWrapper.clicks(mBinding.cardViewBottomSheet).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                showBottomSheetDialog();
            }
        }));
        subscriptions.add(RxViewWrapper.clicks(mBinding.cardViewCustomSheet).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                showCustomDialog();
            }
        }));
        subscriptions.add(RxViewWrapper.clicks(mBinding.cardViewQuickAction1).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                showQuickActionDialog(mBinding.cardViewQuickAction1);
            }
        }));
        subscriptions.add(RxViewWrapper.clicks(mBinding.cardViewQuickAction2).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                showQuickActionDialog(mBinding.cardViewQuickAction2);
            }
        }));
        subscriptions.add(RxViewWrapper.clicks(mBinding.cardViewQuickAction3).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                showQuickActionDialog(mBinding.cardViewQuickAction3);
            }
        }));
        subscriptions.add(RxViewWrapper.clicks(mBinding.cardViewQuickAction4).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                showQuickActionDialog(mBinding.cardViewQuickAction4);
            }
        }));
        subscriptions.add(RxViewWrapper.clicks(mBinding.cardViewQuickAction5).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                showQuickActionDialog(mBinding.cardViewQuickAction5);
            }
        }));
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    private void showBottomSheetDialog() {
        LayoutSheetDialogBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(this), R.layout.layout_sheet_dialog, null, false);
        BottomSheetDialog sheetDialog = new BottomSheetDialog(this);
        sheetDialog.setContentView(binding.getRoot());
        sheetDialog.show();
    }

    private void showCustomDialog() {
        LayoutSheetDialogBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(this), R.layout.layout_sheet_dialog, null, false);
        ViewUtils.createSheetDialog(this, binding.getRoot()).show();
    }

    private void showQuickActionDialog(View view) {
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        showQuickActionDialog(200, rect.bottom);
    }

    private void showQuickActionDialog(final int screenX, final int screenY) {
        final LayoutQuickActionDialogBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(this), R.layout.layout_quick_action_dialog, null, false);
        binding.tvTitle.setText("学术性词汇");
        binding.tvContent.setText("该标签表示某个单词属于学术词汇表，此类单词属于在英语环境中学习或撰写学术文章时需要掌握的重要词汇。");
        binding.tvContent.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                binding.tvContent.getViewTreeObserver().removeOnPreDrawListener(this);
                //字数超过一行时，靠左对齐，反之居中
                binding.tvContent.setGravity(binding.tvContent.getLineCount() > 1 ? Gravity.LEFT : Gravity.CENTER);
                return true;
            }
        });
        final Dialog actionDialog = new Dialog(this, R.style.Dialog_NoTitleAndTransparent_NotFullScreen);
        actionDialog.setContentView(binding.getRoot());
        actionDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Window window = actionDialog.getWindow();
                if (window != null) {
                    WindowManager.LayoutParams params = window.getAttributes();
                    params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                    //计算对话框显示位置
                    int contentHeight = binding.getRoot().getHeight();
                    int screenHeight = getScreenHeight(getApplicationContext());
                    ImageView arrowView;
                    int dialogY;
                    boolean showBelow = screenHeight - screenY > contentHeight;
                    if (showBelow) {
                        arrowView = binding.ivArrowUp;
                        binding.ivArrowUp.setVisibility(View.VISIBLE);
                        binding.ivArrowDown.setVisibility(View.GONE);
                        dialogY = screenY - getStatusBarHeight(getApplicationContext()) + dp2px(getApplicationContext(), 2);
                    } else {
                        arrowView = binding.ivArrowDown;
                        binding.ivArrowUp.setVisibility(View.GONE);
                        binding.ivArrowDown.setVisibility(View.VISIBLE);
                        dialogY = screenY - getStatusBarHeight(getApplicationContext()) - contentHeight - dp2px(getApplicationContext(), 52);
                    }
                    params.y = dialogY;
                    window.setAttributes(params);
                    //计算箭头位置
                    LinearLayout.LayoutParams viewParams = (LinearLayout.LayoutParams) arrowView.getLayoutParams();
                    viewParams.leftMargin = screenX;
                    arrowView.setLayoutParams(viewParams);
                    //动画
                    binding.getRoot().setPivotX(screenX);
                    binding.getRoot().setPivotY(showBelow ? 0 : contentHeight);
                    AnimatorSet set = new AnimatorSet();
                    set.play(ObjectAnimator.ofFloat(binding.getRoot(), View.SCALE_X, 0.5f, 1f))
                            .with(ObjectAnimator.ofFloat(binding.getRoot(), View.SCALE_Y, 0.5f, 1f));
                    set.setDuration(200);
                    set.setInterpolator(new DecelerateInterpolator());
                    set.start();
                }
            }
        });
        actionDialog.show();
    }

    private int getScreenHeight(@NonNull Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    private int getStatusBarHeight(@NonNull Context context) {
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            Field field = clazz.getField("status_bar_height");
            int x = (Integer) field.get(object);
            return context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dp2px(context, 24);
    }

    private int dp2px(@NonNull Context context, float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }

}
