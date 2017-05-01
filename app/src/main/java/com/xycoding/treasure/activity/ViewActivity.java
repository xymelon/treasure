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

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        String tagString = "Her eyes beginning to water, she went on, \"So I would like you all to make me a promise: from now on, on your way to school， or on your way home, find something beautiful to notice. It doesn' t have to be something you see -it could be a scent perhaps of freshly baked bread wafting out of someone 's house, or it could be the sound of the breeze slightly rustling the leaves in the trees, or the way the morning light catches one autumn leaf as it falls gently to the ground. Please, look for these things, and remember them.\"\n" +
                "　　她的眼睛开始湿润了，她接着说因此我想让你们每个人答应我:从今以后，在你上学或者放学的路上，要发现一些美丽的事物。它不一定是你看到的某个东西——它可能是一种香味——也许是新鲜烤面包的味道从某一座房里飘出来，也许是微风轻拂树叶的声音，或者是晨光照射在轻轻飘落的秋叶上的方式。请你们寻找这些东西并且记住它们吧。 \"";
        Pattern pattern = Pattern.compile("(?![^<]*>)[a-zA-Z]+");
        Matcher matcher = pattern.matcher(tagString);
        Set<String> words = new HashSet<>();
        while (matcher.find()) {
            words.add(matcher.group());
        }
        for (String word : words) {
            tagString = tagString.replaceAll("(?<![a-zA-Z])" + word + "(?![a-zA-Z])", "<c>" + word + "</c>");
        }
        System.out.println(tagString);

//        String expression1 = "(?<![a-zA-Z])(?=((?![^<]*>)[a-zA-Z]+))";
//        String expression2 = "(?<=((?![^<]{0,10})[a-zA-Z]))(?![a-zA-Z])";
//        tagString = tagString.replaceAll(expression1, Matcher.quoteReplacement("<c>"));
//        System.out.println(tagString);
//        tagString = tagString.replaceAll(expression2, Matcher.quoteReplacement("</c>"));
//        System.out.println(tagString);

        int normalTextColor = ContextCompat.getColor(this, R.color.R1);
        int pressedTextColor = ContextCompat.getColor(this, R.color.W1);
        int pressedBackgroundColor = ContextCompat.getColor(this, R.color.B2);
        RichText richText = new RichText.Builder()
                .addTypefaceSpan(new ClickSpan(
                        normalTextColor,
                        pressedTextColor,
                        pressedBackgroundColor,
                        new ClickSpan.OnClickListener() {
                            @Override
                            public void onClick(CharSequence text, float rawX, float rawY) {
                                Toast.makeText(ViewActivity.this, text, Toast.LENGTH_SHORT).show();
                            }
                        }), "c")
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
        richText.with(mBinding.tvRichText);
        mBinding.tvRichText.setText(richText.parse(tagString));
    }
}
