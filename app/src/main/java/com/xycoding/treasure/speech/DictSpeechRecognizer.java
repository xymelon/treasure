package com.xycoding.treasure.speech;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xuyang on 2017/3/8.
 */
public abstract class DictSpeechRecognizer {

    public static final String CHINESE = "zh_CN";
    public static final String ENGLISH2 = "eng";

    public static final String ENGLISH = "";

    public static final String JAPANESE = "jap";

    public static final String KOREAN = "ko";

    public static final String FRENCH = "fr";

    /**
     * 添加新的语言支持
     */
    public static final String GERMAN = "de";
    public static final String SPAIN = "es";
    public static final String PORTUGAL = "pt";
    public static final String RUSSIA = "ru";

    private static DictSpeechRecognizer xunfeiRecognizer;

    private static DictSpeechRecognizer nuanceRecognizer;

    private static Map<String, String> sLanguageMap = new HashMap<String, String>() {{
        put(CHINESE, "zh_cn:mandarin");
        put(ENGLISH, "en_us");
        put(ENGLISH2, "en_us");
        put(JAPANESE, "jpn-JPN");
        put(KOREAN, "kor-KOR");
        put(FRENCH, "fra-FRA");
        put(GERMAN, "deu-DEU");
        put(SPAIN, "spa-ESP");
        put(PORTUGAL, "por-PRT");
        put(RUSSIA, "rus-RUS");
    }};

    DictSpeechRecognizer() {
    }

    public static DictSpeechRecognizer getInstance(String language) {
        if (TextUtils.isEmpty(language) || language.equals(ENGLISH2) || language.equals(CHINESE)) {
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

    public abstract void start(SpeechListener listener);

    public abstract void stop();

    public abstract void release();

}
