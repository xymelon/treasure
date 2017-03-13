package com.xycoding.treasure.speech;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xuyang on 2017/3/8.
 */
public abstract class DictSpeechRecognizer {

    private static DictSpeechRecognizer xunfeiRecognizer;
    private static DictSpeechRecognizer nuanceRecognizer;

    private static Map<String, String> sLanguageMap = new HashMap<String, String>() {{
        put(SpeechConfiguration.CHINESE, "zh_cn:mandarin");
        put(SpeechConfiguration.ENGLISH, "en_us");
        put(SpeechConfiguration.ENGLISH2, "en_us");
        put(SpeechConfiguration.JAPANESE, "jpn-JPN");
        put(SpeechConfiguration.KOREAN, "kor-KOR");
        put(SpeechConfiguration.FRENCH, "fra-FRA");
        put(SpeechConfiguration.GERMAN, "deu-DEU");
        put(SpeechConfiguration.SPAIN, "spa-ESP");
        put(SpeechConfiguration.PORTUGAL, "por-PRT");
        put(SpeechConfiguration.RUSSIA, "rus-RUS");
    }};

    DictSpeechRecognizer() {
    }

    public static DictSpeechRecognizer getInstance(String language) {
        if (TextUtils.isEmpty(language)
                || language.equals(SpeechConfiguration.ENGLISH2)
                || language.equals(SpeechConfiguration.CHINESE)) {
            //英语和中文使用科大讯飞语音识别
            if (xunfeiRecognizer == null) {
                xunfeiRecognizer = new XunFeiSpeechRecognizer();
            }
            xunfeiRecognizer.setLanguage(sLanguageMap.get(language));
            return xunfeiRecognizer;
        } else {
            //小语种使用nuance语音识别
            if (nuanceRecognizer == null) {
                nuanceRecognizer = new NuanceSpeechRecognizer();
            }
            nuanceRecognizer.setLanguage(sLanguageMap.get(language));
            return nuanceRecognizer;
        }
    }

    protected abstract void setLanguage(String language);

    public abstract void start(SpeechRecognizerListener listener);

    public abstract void stop();

    public abstract void release();

}
