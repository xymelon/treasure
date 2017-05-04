package com.xycoding.treasure.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xuyang on 2017/5/2.
 */
public class StringUtils {

    public static String replaceWordsWithTag(String origin, String startTag, String endTag) {
        //正则表达式：匹配除html标签的英文单词
        Pattern pattern = Pattern.compile("(?![^<]*>)[a-zA-Z]+");
        Matcher matcher = pattern.matcher(origin);
        StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(buffer, startTag + matcher.group() + endTag);
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

}
