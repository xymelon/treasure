package com.xycoding.treasure.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.Toast;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewAfterTextChangeEvent;
import com.xycoding.treasure.R;
import com.xycoding.treasure.databinding.ActivityViewBinding;
import com.xycoding.treasure.rx.RxViewWrapper;
import com.xycoding.treasure.view.richtext.RichText;
import com.xycoding.treasure.view.richtext.typeface.ClickSpan;
import com.xycoding.treasure.view.richtext.typeface.IStyleSpan;

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
        String tagString = "<f>中文</f>:<b>China (乐团)</b>;英语:<b>China</b>;法语:<b>China</b>;";
        int normalTextColor = ContextCompat.getColor(this, R.color.R1);
        int pressedTextColor = ContextCompat.getColor(this, R.color.W1);
        int pressedBackgroundColor = ContextCompat.getColor(this, R.color.B2);
        RichText richText = new RichText.Builder()
                .addTypefaceSpan(new ClickSpan(
                        mBinding.tvRichText,
                        normalTextColor,
                        pressedTextColor,
                        pressedBackgroundColor,
                        new ClickSpan.OnClickListener() {
                            @Override
                            public void onClick(CharSequence text) {
                                Toast.makeText(ViewActivity.this, text, Toast.LENGTH_SHORT).show();
                            }
                        }), "b")
                .addTypefaceSpan(new IStyleSpan() {
                    @Override
                    public CharacterStyle getStyleSpan() {
                        return new StyleSpan(Typeface.ITALIC);
                    }
                }, "b")
                .addTypefaceSpan(new IStyleSpan() {

                    int textColor = ContextCompat.getColor(ViewActivity.this, R.color.T1);

                    @Override
                    public CharacterStyle getStyleSpan() {
                        return new ForegroundColorSpan(textColor);
                    }
                }, "f")
                .build();
        mBinding.tvRichText.setText(richText.parse(tagString));
    }
}
