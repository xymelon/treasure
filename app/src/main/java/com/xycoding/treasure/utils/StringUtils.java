package com.xycoding.treasure.utils;

import java.util.HashSet;
import java.util.Set;
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
        Set<String> words = new HashSet<>();
        while (matcher.find()) {
            words.add(matcher.group());
        }
        for (String word : words) {
            //将匹配到的单词加上tag，(?![a-zA-Z])断言保证替换完整单词
            origin = origin.replaceAll("(?<![a-zA-Z])" + word + "(?![a-zA-Z])", startTag + word + endTag);
        }
        return origin;
    }

}
