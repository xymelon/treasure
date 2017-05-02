package com.xycoding.treasure.view.richtext.style;

import android.text.Spannable;
import android.text.SpannableStringBuilder;

import com.xycoding.treasure.view.richtext.TagBlock;
import com.xycoding.treasure.view.richtext.typeface.LinkClickSpan;

/**
 * Created by xuyang on 2017/5/2.
 */
public class LinkTagStyle extends BlockTagStyle {

    private static final String ATTRIBUTE_HREF = "href";
    private static final String LINK_TAG = "a";

    public LinkTagStyle(LinkClickSpan span) {
        super(span, LINK_TAG);
    }

    @Override
    public void start(TagBlock block, SpannableStringBuilder builder) {
        if (block.getAttributes() != null) {
            ((LinkClickSpan) mStyleSpan).setLinkUrl(block.getAttributes().get(ATTRIBUTE_HREF));
        }
        final int len = builder.length();
        builder.setSpan(this, len, len, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
    }

}
