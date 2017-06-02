package com.xycoding.treasure.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewAfterTextChangeEvent;
import com.xycoding.richtext.RichText;
import com.xycoding.richtext.typeface.ClickSpan;
import com.xycoding.richtext.typeface.IStyleSpan;
import com.xycoding.richtext.typeface.LinkClickSpan;
import com.xycoding.treasure.R;
import com.xycoding.treasure.databinding.ActivityViewBinding;
import com.xycoding.treasure.databinding.DialogQuickActionBinding;
import com.xycoding.treasure.rx.RxViewWrapper;
import com.xycoding.treasure.utils.DeviceUtils;
import com.xycoding.treasure.utils.StringUtils;

import rx.functions.Action1;

/**
 * Created by xuyang on 2016/10/28.
 */
public class ViewActivity extends BaseBindingActivity {

    private ActivityViewBinding mBinding;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_view;
    }

    @Override
    protected void initControls(Bundle savedInstanceState) {
        mBinding = (ActivityViewBinding) binding;
        initViews();
    }

    @Override
    protected void setListeners() {
        subscriptions.add(RxTextView.afterTextChangeEvents(mBinding.clearEditText).subscribe(new Action1<TextViewAfterTextChangeEvent>() {
            @Override
            public void call(TextViewAfterTextChangeEvent event) {
                mBinding.fitTextView.setText(event.editable());
            }
        }));
        subscriptions.add(RxViewWrapper.clicks(mBinding.btnRecyclerView).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
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
        initRichTextView();
    }

    private void initRichTextView() {
        final int foregroundTextColor = ContextCompat.getColor(this, R.color.T1);
        int normalTextColor = ContextCompat.getColor(this, R.color.G20);
        int pressedTextColor = ContextCompat.getColor(this, R.color.W1);
        int pressedBackgroundColor = ContextCompat.getColor(this, R.color.B2);
        RichText richText = new RichText.Builder()
                .addBlockTypeSpan(new ClickSpan(
                        normalTextColor,
                        pressedTextColor,
                        pressedBackgroundColor,
                        new ClickSpan.OnClickListener() {
                            @Override
                            public void onClick(TextView textView, CharSequence text, float rawX, float rawY) {
                                showQuickActionDialog((int) rawX, (int) rawY);
                            }
                        }), "c")
                .addBlockTypeSpan(new IStyleSpan() {
                    @Override
                    public CharacterStyle getStyleSpan() {
                        return new ForegroundColorSpan(foregroundTextColor);
                    }
                }, "b")
                .addLinkTypeSpan(new LinkClickSpan(
                        foregroundTextColor,
                        pressedTextColor,
                        pressedBackgroundColor,
                        new LinkClickSpan.OnLinkClickListener() {
                            @Override
                            public void onClick(TextView textView, String url) {
                                Toast.makeText(ViewActivity.this, url, Toast.LENGTH_SHORT).show();
                            }
                        })
                )
                .build();
        richText.with(mBinding.tvRichText1);
        richText.with(mBinding.tvRichText2);

        String tagString1 = "英语单词<a href='http://www.etymonline.com'>china</a>小写时表示“中国瓷”，大写时表示“中国”，那么它最初究竟是对中国的称呼还是对中国瓷的称呼呢？\n词源学研究显示，它最初是对中国的称呼。明代中期葡萄牙人贩瓷器到欧洲，将其称为chinaware，ware是器具的意思，可见china是地名，并无瓷器之意。";
        tagString1 = StringUtils.replaceWordsWithTag(tagString1, "<c>", "</c>");
        mBinding.tvRichText1.setText(richText.parse(tagString1));

        String tagString2 = "He did his award-winning work at the Chinese University of Hong Kong,<b>China</b> and at the Standard Telecommunication Laboratories in Britain.";
        tagString2 = StringUtils.replaceWordsWithTag(tagString2, "<c>", "</c>");
        mBinding.tvRichText2.setText(richText.parse(tagString2));
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
