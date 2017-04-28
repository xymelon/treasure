package com.xycoding.treasure.view.richtext;

import android.text.SpannableStringBuilder;
import android.text.Spanned;

import com.xycoding.treasure.view.richtext.style.BaseTagStyle;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by xuyang on 2017/4/28.
 */
class TagParser extends DefaultHandler {

    private final org.ccil.cowan.tagsoup.Parser mParser;
    private final ArrayList<BaseTagStyle> mBaseTagStyles;
    private SpannableStringBuilder mSpannableStringBuilder;

    TagParser() {
        mParser = new org.ccil.cowan.tagsoup.Parser();
        mBaseTagStyles = new ArrayList<>();
    }

    Spanned parse(String tagString) {
        mSpannableStringBuilder = new SpannableStringBuilder();
        try {
            mParser.setContentHandler(this);
            mParser.parse(new InputSource(new StringReader(tagString)));
        } catch (IOException | SAXException e) {
            throw new IllegalArgumentException(e);
        }
        return mSpannableStringBuilder;
    }

    void addTypefaceStyle(BaseTagStyle listener) {
        mBaseTagStyles.add(listener);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        int attributesLength = attributes.getLength();
        Map<String, String> attributesMap = new HashMap<>(attributesLength);
        for (int i = 0; i < attributesLength; i++) {
            attributesMap.put(attributes.getLocalName(i), attributes.getValue(i));
        }
        TagBlock block = new TagBlock(localName, attributesMap);
        for (BaseTagStyle style : mBaseTagStyles) {
            if (style.match(localName)) {
                style.start(block, mSpannableStringBuilder);
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        TagBlock block = new TagBlock(localName);
        for (BaseTagStyle style : mBaseTagStyles) {
            if (style.match(localName)) {
                style.end(block, mSpannableStringBuilder);
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        StringBuilder sb = new StringBuilder();
        /*
         * Ignore whitespace that immediately follows other whitespace;
         * newlines count as spaces.
         */
        for (int i = 0; i < length; i++) {
            char c = ch[i + start];
            if (c == ' ' || c == '\n') {
                char pred;
                int len = sb.length();
                if (len == 0) {
                    len = mSpannableStringBuilder.length();
                    if (len == 0) {
                        pred = '\n';
                    } else {
                        pred = mSpannableStringBuilder.charAt(len - 1);
                    }
                } else {
                    pred = sb.charAt(len - 1);
                }
                if (pred != ' ' && pred != '\n') {
                    sb.append(' ');
                }
            } else {
                sb.append(c);
            }
        }
        mSpannableStringBuilder.append(sb);
    }

}
