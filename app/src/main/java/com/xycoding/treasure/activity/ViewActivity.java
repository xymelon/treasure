package com.xycoding.treasure.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jakewharton.rxbinding2.widget.RxTextView;
import com.jakewharton.rxbinding2.widget.TextViewAfterTextChangeEvent;
import com.xycoding.treasure.R;
import com.xycoding.treasure.databinding.ActivityViewBinding;
import com.xycoding.treasure.databinding.DialogQuickActionBinding;
import com.xycoding.treasure.rx.RxViewWrapper;
import com.xycoding.treasure.utils.DeviceUtils;

import io.reactivex.functions.Consumer;

/**
 * Created by xuyang on 2016/10/28.
 */
public class ViewActivity extends BaseBindingActivity<ActivityViewBinding> {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_view;
    }

    @Override
    protected void initControls(Bundle savedInstanceState) {
        initViews();
    }

    @Override
    protected void setListeners() {
        mDisposables.add(RxTextView.afterTextChangeEvents(mBinding.clearEditText).subscribe(new Consumer<TextViewAfterTextChangeEvent>() {
            @Override
            public void accept(TextViewAfterTextChangeEvent event) throws Exception {
                mBinding.fitTextView.setText(event.editable());
            }
        }));
        mDisposables.add(RxViewWrapper.clicks(mBinding.btnRecyclerView).subscribe(new Consumer<Object>() {
            @Override
            public void accept(Object o) throws Exception {
                startActivity(new Intent(ViewActivity.this, RecyclerViewActivity.class));
            }
        }));
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mBinding.clearEditText.setText("输入：TextView自适应");
        mBinding.autoEditText.setText("输入：EditText自适应");
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mBinding.autoEditText.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                mBinding.autoEditText.requestFocus();
//                KeyboardUtils.showSoftKeyBoard(ViewActivity.this, mBinding.autoEditText);
//            }
//        }, 500);
    }

    private void initViews() {
        mBinding.autoEditText.shouldBlinkOnMeiZu(true);
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
        final Dialog actionDialog = new Dialog(this, R.style.Dialog_NoTitleAndTransparent_FullScreen);
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
                    viewParams.leftMargin = screenX - arrowView.getWidth() / 2;
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
