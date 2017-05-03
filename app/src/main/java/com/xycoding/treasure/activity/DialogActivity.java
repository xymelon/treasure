package com.xycoding.treasure.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
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
import com.xycoding.treasure.databinding.DialogEventBinding;
import com.xycoding.treasure.databinding.DialogQuickActionBinding;
import com.xycoding.treasure.databinding.DialogSheetBinding;
import com.xycoding.treasure.rx.RxViewWrapper;
import com.xycoding.treasure.utils.DeviceUtils;
import com.xycoding.treasure.utils.ViewUtils;

import rx.functions.Action1;

/**
 * Created by xuyang on 2016/10/28.
 */
public class DialogActivity extends BaseBindingActivity {

    private ActivityDialogBinding mBinding;
    private Dialog mEventDialog;

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
                showCustomBottomSheetDialog();
            }
        }));
        subscriptions.add(RxViewWrapper.clicks(mBinding.cardViewQuickAction).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                showQuickActionDialog(mBinding.cardViewQuickAction);
            }
        }));
        subscriptions.add(RxViewWrapper.clicks(mBinding.cardViewEventDialog).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                showEventDialog();
            }
        }));
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    private void showBottomSheetDialog() {
        DialogSheetBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(this), R.layout.dialog_sheet, null, false);
        BottomSheetDialog sheetDialog = new BottomSheetDialog(this);
        sheetDialog.setContentView(binding.getRoot());
        sheetDialog.show();
    }

    private void showCustomBottomSheetDialog() {
        DialogSheetBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(this), R.layout.dialog_sheet, null, false);
        ViewUtils.createSheetDialog(this, binding.getRoot()).show();
    }

    private void showEventDialog() {
        if (mEventDialog == null) {
            DialogEventBinding binding = DataBindingUtil.inflate(
                    LayoutInflater.from(this), R.layout.dialog_event, null, false);
            mEventDialog = ViewUtils.createEventDialog(this, binding.getRoot());
        }
        if (mEventDialog.isShowing()) {
            mEventDialog.dismiss();
        } else {
            mEventDialog.show();
        }
    }

    private void showQuickActionDialog(View view) {
        Rect rect = new Rect();
        view.getGlobalVisibleRect(rect);
        showQuickActionDialog(200, rect.bottom);
    }

    private void showQuickActionDialog(final int screenX, final int screenY) {
        final DialogQuickActionBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(this), R.layout.dialog_quick_action, null, false);
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
                    int screenHeight = DeviceUtils.getScreenHeight(getApplicationContext());
                    ImageView arrowView;
                    int dialogY;
                    boolean showBelow = screenHeight - screenY > contentHeight;
                    if (showBelow) {
                        arrowView = binding.ivArrowUp;
                        binding.ivArrowUp.setVisibility(View.VISIBLE);
                        binding.ivArrowDown.setVisibility(View.GONE);
                        dialogY = screenY - DeviceUtils.getStatusBarHeight(getApplicationContext()) + DeviceUtils.dp2px(getApplicationContext(), 2);
                    } else {
                        arrowView = binding.ivArrowDown;
                        binding.ivArrowUp.setVisibility(View.GONE);
                        binding.ivArrowDown.setVisibility(View.VISIBLE);
                        dialogY = screenY - DeviceUtils.getStatusBarHeight(getApplicationContext()) - contentHeight - DeviceUtils.dp2px(getApplicationContext(), 52);
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

}
