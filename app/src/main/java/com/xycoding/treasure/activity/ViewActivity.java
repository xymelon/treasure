package com.xycoding.treasure.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.widget.Toast;

import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewAfterTextChangeEvent;
import com.xycoding.treasure.R;
import com.xycoding.treasure.databinding.ActivityViewBinding;
import com.xycoding.treasure.rx.RxViewWrapper;
import com.xycoding.treasure.utils.StringUtils;
import com.xycoding.treasure.view.richtext.RichText;
import com.xycoding.treasure.view.richtext.typeface.ClickSpan;
import com.xycoding.treasure.view.richtext.typeface.IStyleSpan;
import com.xycoding.treasure.view.richtext.typeface.LinkClickSpan;

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
                            public void onClick(CharSequence text, float rawX, float rawY) {
                                Toast.makeText(ViewActivity.this, text, Toast.LENGTH_SHORT).show();
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
                            public void onClick(String url) {
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
}
