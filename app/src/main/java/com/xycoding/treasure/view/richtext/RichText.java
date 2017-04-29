package com.xycoding.treasure.view.richtext;

import android.support.annotation.NonNull;
import android.text.Spanned;

import com.xycoding.treasure.view.richtext.style.BlockTagStyle;
import com.xycoding.treasure.view.richtext.typeface.IStyleSpan;

/**
 * Created by xuyang on 2017/4/28.
 */
public class RichText {

    private final TagParser mTagParser;

    private RichText(TagParser parser) {
        mTagParser = parser;
    }

    public Spanned parse(String tagString) {
        return mTagParser.parse("<html>" + tagString + "</html>");
    }

    public static class Builder {

        private TagParser mParser;

        public Builder() {
            mParser = new TagParser();
        }

        public Builder addTypefaceSpan(@NonNull IStyleSpan span, String... tags) {
            mParser.addTypefaceStyle(new BlockTagStyle(span, tags));
            return this;
        }

        public RichText build() {
            return new RichText(mParser);
        }

    }

}
