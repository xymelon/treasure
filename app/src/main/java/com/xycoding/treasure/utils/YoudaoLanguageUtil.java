package com.xycoding.treasure.utils;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Pair;

import com.xycoding.treasure.R;
import com.xycoding.treasure.internet.AsyncTask;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by xymelon on 2018/8/9.
 */
public class YoudaoLanguageUtil {

    public static void languageChineseUtil(@NonNull Context context) {
        final String[] languageXmlArray = context.getResources().getStringArray(R.array.lang_name);
        final List<String> languageArray = Arrays.asList(languageXmlArray);
        final Comparator<String> comparator = (o1, o2) -> {
            Collator collator = Collator.getInstance();
            return collator.getCollationKey(o1).compareTo(
                    collator.getCollationKey(o2));
        };
        //按字母排序
        Collections.sort(languageArray, comparator);
        StringBuilder strContent = new StringBuilder();
        //生成语言xml列表
        for (String language : languageArray) {
            strContent.append("<item>").append(language).append("</item>");
        }
        //按首字母生成xml列表
        strContent = new StringBuilder();
        StringBuilder strSection = new StringBuilder();
        for (int i = 0; i < languageArray.size(); i++) {
            if (i == 0) {
                strContent.append("<item>").append(languageArray.get(i));
                strSection.append("<item>").append(Character.toUpperCase(PinyinHelper.toHanyuPinyinStringArray(languageArray.get(i).charAt(0))[0].charAt(0)));
            } else if (PinyinHelper.toHanyuPinyinStringArray(languageArray.get(i).charAt(0))[0].charAt(0) !=
                    PinyinHelper.toHanyuPinyinStringArray(languageArray.get(i - 1).charAt(0))[0].charAt(0)) {
                strContent.append("</item>\n<item>").append(languageArray.get(i));
                strSection.append("</item>\n<item>").append(Character.toUpperCase(PinyinHelper.toHanyuPinyinStringArray(languageArray.get(i).charAt(0))[0].charAt(0)));
            } else {
                strContent.append(",").append(languageArray.get(i));
            }
        }
        strContent.append("</item>");
        strSection.append("</item>");

        //转为英语
        final String[] commonChineseArray = context.getResources().getStringArray(R.array.language_common_chinese);
        final String[] commonEnglishArray = context.getResources().getStringArray(R.array.language_common_english);
        final List<String> languageEnglishArray = new ArrayList<>();
        for (String language : languageArray) {
            for (int i = 0; i < commonChineseArray.length; i++) {
                if (commonChineseArray[i].equals(language)) {
                    languageEnglishArray.add(commonEnglishArray[i]);
                }
            }
        }
        languageEnglishUtil(languageEnglishArray);
    }

    private static void languageEnglishUtil(List<String> languageArray) {
        final Comparator<String> comparator = (o1, o2) -> {
            Collator collator = Collator.getInstance();
            return collator.getCollationKey(o1).compareTo(
                    collator.getCollationKey(o2));
        };
        //按字母排序
        Collections.sort(languageArray, comparator);
        StringBuilder strContent = new StringBuilder();
        //生成语音xml列表
        for (String language : languageArray) {
            strContent.append("<item>").append(language).append("</item>");
        }
        //按首字母生成xml列表
        strContent = new StringBuilder();
        StringBuilder strSection = new StringBuilder();
        for (int i = 0; i < languageArray.size(); i++) {
            if (i == 0) {
                strContent.append("<item>").append(languageArray.get(i));
                strSection.append("<item>").append(Character.toUpperCase(languageArray.get(i).charAt(0)));
            } else if (languageArray.get(i).charAt(0) !=
                    languageArray.get(i - 1).charAt(0)) {
                strContent.append("</item>\n<item>").append(languageArray.get(i));
                strSection.append("</item>\n<item>").append(Character.toUpperCase(languageArray.get(i).charAt(0)));
            } else {
                strContent.append(",").append(languageArray.get(i));
            }
        }
        strContent.append("</item>");
        strSection.append("</item>");
    }

    public static void convertTransLanguage(@NonNull Context context) {
        AsyncTask.THREAD_POOL_EXECUTOR.execute(() -> {
            try {
                final InputStream inputStream = context.getAssets().open("language/trans.txt");
                final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                final List<Pair<String, String>> languages = new ArrayList<>();
                final List<LanguageModel> languageMap = new ArrayList<>();

                String line = reader.readLine();
                while (line != null) {
                    if (!TextUtils.isEmpty(line)) {
                        final String[] strings = line.split("\\|");
                        final String language = strings[0].trim().toUpperCase() + "(\"" + strings[1].trim() + "\"),";
                        final String transLanguage = "sLanguages.put(CommonLanguage." + strings[0].trim().toUpperCase() + ", \"" + strings[2].trim() + "\");";

                        languageMap.add(new LanguageModel(strings[1].trim(), language, transLanguage));
                        languages.add(new Pair<>(strings[1].trim(), strings[0].trim()));
                    }
                    line = reader.readLine();
                }
                final Comparator<LanguageModel> comparator1 = (LanguageModel o1, LanguageModel o2) -> {
                    Collator collator = Collator.getInstance();
                    return collator.getCollationKey(o1.chinese).compareTo(
                            collator.getCollationKey(o2.chinese));
                };
                Collections.sort(languageMap, comparator1);
                final FileWriter languageFileWriter = new FileWriter(new File(Environment.getExternalStorageDirectory(), "YoudaoLanguage.txt"));
                final FileWriter transLanguageFileWriter = new FileWriter(new File(Environment.getExternalStorageDirectory(), "YoudaoTransLanguage.txt"));

                for (LanguageModel languageModel : languageMap) {
                    languageFileWriter.write(languageModel.enumString);
                    languageFileWriter.write("\n");
                    transLanguageFileWriter.write(languageModel.mapString);
                    transLanguageFileWriter.write("\n");
                }

                languageFileWriter.flush();
                languageFileWriter.close();
                transLanguageFileWriter.flush();
                transLanguageFileWriter.close();

                //生成xml
                final FileWriter languageXmlFileWriter = new FileWriter(new File(Environment.getExternalStorageDirectory(), "YoudaoLanguageXml.txt"));
                final Comparator<Pair> comparator2 = (o1, o2) -> {
                    Collator collator = Collator.getInstance();
                    return collator.getCollationKey((String) o1.first).compareTo(
                            collator.getCollationKey((String) o2.first));
                };
                //按字母排序
                Collections.sort(languages, comparator2);
                //生成中文语言xml列表
                for (Pair<String, String> pair : languages) {
                    languageXmlFileWriter.write("<item>" + pair.first + "</item>");
                    languageXmlFileWriter.write("\n");
                }
                languageXmlFileWriter.write("\n");
                languageXmlFileWriter.write("\n");
                languageXmlFileWriter.write("\n");
                //生成英文语言xml列表
                for (Pair<String, String> pair : languages) {
                    languageXmlFileWriter.write("<item>" + pair.second + "</item>");
                    languageXmlFileWriter.write("\n");
                }
                languageXmlFileWriter.flush();
                languageXmlFileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static void languageCreateEnum(@NonNull Context context) {
//        try {
//            final String[] allLanguagesEnglish = context.getResources().getStringArray(R.array.language_common_english);
//            final String[] allLanguagesChinese = context.getResources().getStringArray(R.array.language_common_chinese);
//            final FileWriter fileWriter = new FileWriter(new File(Environment.getExternalStorageDirectory(), "YoudaoLanguageEnum.txt"));
//            for (int i = 0; i < allLanguagesChinese.length; i++) {
//                final String language = allLanguagesEnglish[i].toUpperCase() + "(TEST[" + i + "], \"" + allLanguagesChinese[i].trim() + "\"),";
//                fileWriter.write(language);
//                fileWriter.write("\n");
//            }
//            fileWriter.flush();
//            fileWriter.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    static class LanguageModel {
        String chinese;
        String enumString;
        String mapString;

        LanguageModel(String chinese, String enumString, String mapString) {
            this.chinese = chinese;
            this.enumString = enumString;
            this.mapString = mapString;
        }
    }

}
