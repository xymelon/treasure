package com.xycoding.treasure.view.richtext.style;

import android.text.SpannableStringBuilder;
import android.text.style.CharacterStyle;

import com.xycoding.treasure.view.richtext.TagBlock;

import java.util.Arrays;
import java.util.List;

public abstract class BaseTagStyle {

    protected List<String> mTags;
    protected CharacterStyle mSpanStyle;

    public BaseTagStyle(CharacterStyle style, String... tags) {
        mSpanStyle = style;
        mTags = Arrays.asList(tags);
    }

    abstract public void start(TagBlock block, SpannableStringBuilder builder);

    abstract public void end(TagBlock block, SpannableStringBuilder builder);

    abstract public boolean match(String tagName);

}
