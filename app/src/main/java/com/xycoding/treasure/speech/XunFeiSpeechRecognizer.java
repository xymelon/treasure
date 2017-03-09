package com.xycoding.treasure.speech;

import android.os.Bundle;

import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.xycoding.treasure.TreasureApplication;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xuyang on 2017/3/8.
 */
public class XunFeiSpeechRecognizer extends DictSpeechRecognizer {

    private SpeechRecognizer speechRecognizer;
    private SpeechListener speechListener;

    XunFeiSpeechRecognizer() {
        SpeechUtility.createUtility(TreasureApplication.getInstance(), "appid=" + SpeechConfiguration.XUNFEI_APP_ID);
        ensureRecognizer();
    }

    private void ensureRecognizer() {
        if (speechRecognizer != null) {
            return;
        }
        speechRecognizer = SpeechRecognizer.createRecognizer(TreasureApplication.getInstance(), null);
        //清空参数
        speechRecognizer.setParameter(SpeechConstant.PARAMS, null);
        //设置听写引擎
        speechRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        //设置返回结果格式
        speechRecognizer.setParameter(SpeechConstant.RESULT_TYPE, "json");
        //设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        speechRecognizer.setParameter(SpeechConstant.VAD_BOS, "10000");
        //设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入，自动停止录音
        speechRecognizer.setParameter(SpeechConstant.VAD_EOS, "500");
        //设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        speechRecognizer.setParameter(SpeechConstant.ASR_PTT, "0");
    }

    @Override
    public void setLanguage(String language) {
        ensureRecognizer();

        String[] languages = language.split(":");
        if (languages.length == 2) {
            //设置语言
            speechRecognizer.setParameter(SpeechConstant.LANGUAGE, languages[0]);
            //设置语言区域
            speechRecognizer.setParameter(SpeechConstant.ACCENT, languages[1]);
        } else {
            //设置语言
            speechRecognizer.setParameter(SpeechConstant.LANGUAGE, language);
        }
    }

    @Override
    public void start(final SpeechListener listener) {
        ensureRecognizer();

        stop();
        speechListener = listener;
        speechRecognizer.startListening(mRecognizerListener);
    }

    @Override
    public void stop() {
        if (speechRecognizer != null) {
            //取消会话，未返回的结果将不再返回
            speechRecognizer.cancel();
        }
        speechListener = null;
    }

    @Override
    public void release() {
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
            speechRecognizer = null;
        }
        speechListener = null;
    }

    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        private List<String> speechResults = new ArrayList<>();

        @Override
        public void onVolumeChanged(int i, byte[] bytes) {
            //音量范围[0-30]
            float percent = i / 30.f;
            if (speechListener != null) {
                speechListener.onVolumeChanged(percent);
            }
        }

        @Override
        public void onBeginOfSpeech() {
            speechResults.clear();
            if (speechListener != null) {
                speechListener.onStartedRecording();
            }
        }

        @Override
        public void onEndOfSpeech() {
            if (speechListener != null) {
                speechListener.onFinishedRecording();
            }
        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean isLast) {
            if (recognizerResult != null) {
                speechResults.add(recognizerResult.getResultString());
            }
            //如果是最后条数据，则处理
            if (isLast) {
                StringBuilder strBuilder = new StringBuilder();
                for (String str : speechResults) {
                    strBuilder.append(parseIatResult(str));
                }
                if (speechListener != null) {
                    speechListener.onSuccess(strBuilder.toString());
                    speechListener = null;
                }
            }
        }

        @Override
        public void onError(SpeechError speechError) {
            //10118 您好像没有说话哦
            if (speechListener != null) {
                speechListener.onError(speechError.getErrorDescription());
                speechListener = null;
            }
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    /**
     * 解析讯飞语音数据
     *
     * @param json
     * @return
     */
    public static String parseIatResult(String json) {
        StringBuilder ret = new StringBuilder();
        try {
            JSONObject joResult = new JSONObject(json);

            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                // 转写结果词，默认使用第一个结果
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                JSONObject obj = items.getJSONObject(0);
                ret.append(obj.getString("w"));
                // 如果需要多候选结果，解析数组其他字段
//				for(int j = 0; j < items.length(); j++)
//				{
//					JSONObject obj = items.getJSONObject(j);
//					ret.append(obj.getString("w"));
//				}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.toString();
    }

}
