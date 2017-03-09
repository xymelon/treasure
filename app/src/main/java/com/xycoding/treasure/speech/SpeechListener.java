package com.xycoding.treasure.speech;

/**
 * Created by xuyang on 2017/3/8.
 */
public interface SpeechListener {

    void onVolumeChanged(float volume);

    /**
     * 开始接收语音
     */
    void onStartedRecording();

    /**
     * 结束接收语音（引擎会将接收到的语音发送到服务端开始识别）
     */
    void onFinishedRecording();

    /**
     * 识别成功
     *
     * @param result
     */
    void onSuccess(String result);

    void onError(String error);

}
