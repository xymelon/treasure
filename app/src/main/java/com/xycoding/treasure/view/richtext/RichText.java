package com.xycoding.treasure.view.richtext;

import android.support.annotation.NonNull;
import android.text.Spanned;
import android.text.style.CharacterStyle;

import com.xycoding.treasure.view.richtext.style.BlockTagStyle;

/**
 * Created by xuyang on 2017/4/28.
 */
public class RichText {

    private final TagParser mTagParser;

    private RichText(TagParser parser) {
        mTagParser = parser;
    }

    public Spanned parse(String tagString) {
        return mTagParser.parse(tagString);
    }

    public static class Builder {

        private TagParser mParser;

        public Builder() {
            mParser = new TagParser();
        }

        public Builder addTypefaceStyle(@NonNull CharacterStyle style, String... tags) {
            mParser.addTypefaceStyle(new BlockTagStyle(style, tags));
            return this;
        }

        public RichText build() {
            return new RichText(mParser);
        }

    }

}
