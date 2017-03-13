package com.xycoding.treasure.speech;

import android.os.Handler;

import com.nuance.speechkit.DetectionType;
import com.nuance.speechkit.Language;
import com.nuance.speechkit.Recognition;
import com.nuance.speechkit.RecognitionException;
import com.nuance.speechkit.RecognitionType;
import com.nuance.speechkit.Session;
import com.nuance.speechkit.Transaction;
import com.nuance.speechkit.TransactionException;
import com.xycoding.treasure.TreasureApplication;

/**
 * Created by xuyang on 2017/3/8.
 */
public class NuanceSpeechRecognizer extends DictSpeechRecognizer {

    private Transaction.Options options = new Transaction.Options();
    private Handler mainHandler = new Handler();
    private Session speechSession;
    private Transaction transaction;
    private SpeechRecognizerListener speechRecognizerListener;

    NuanceSpeechRecognizer() {
        speechSession = Session.Factory.session(
                TreasureApplication.getInstance(), SpeechConfiguration.NUANCE_SERVER_URI, SpeechConfiguration.NUANCE_APP_KEY);
        options.setRecognitionType(RecognitionType.DICTATION);
        options.setDetection(DetectionType.Long);
    }

    @Override
    public void setLanguage(String language) {
        options.setLanguage(new Language(language));
    }

    @Override
    public void start(final SpeechRecognizerListener listener) {
        stop();
        speechRecognizerListener = listener;
        transaction = speechSession.recognize(options, recognizerListener);
    }

    @Override
    public void stop() {
        speechRecognizerListener = null;
        if (transaction != null) {
            transaction.cancel();
            transaction = null;
        }
    }

    @Override
    public void release() {

    }

    private void startVolumePoll() {
        volumePoller.run();
    }

    private void stopVolumePoll() {
        mainHandler.removeCallbacks(volumePoller);
    }

    private void startCountdown() {
        mainHandler.postDelayed(countdownTimer, SpeechConfiguration.VAD_BEGIN_TIMEOUT);
    }

    private void stopCountdown() {
        mainHandler.removeCallbacks(countdownTimer);
    }

    /**
     * 开始录入音频后，最长静音超时且未得到结果即返回错误
     */
    private Runnable countdownTimer = new Runnable() {
        @Override
        public void run() {
            if (transaction != null) {
                recognizerListener.onError(
                        transaction,
                        SpeechConfiguration.VAD_BEGIN_TIMEOUT_ERROR,
                        new RecognitionException(SpeechConfiguration.VAD_BEGIN_TIMEOUT_ERROR));
                stop();
            }
        }
    };

    /**
     * Every 50 milliseconds we should update the volume meter in our UI.
     */
    private Runnable volumePoller = new Runnable() {
        @Override
        public void run() {
            if (transaction != null && speechRecognizerListener != null) {
                //音量范围[0-90]
                float percent = transaction.getAudioLevel() / 90.f;
                speechRecognizerListener.onVolumeChanged(percent);
                mainHandler.postDelayed(volumePoller, 50);
            }
        }
    };

    private Transaction.Listener recognizerListener = new Transaction.Listener() {

        @Override
        public void onStartedRecording(Transaction transaction) {
            if (speechRecognizerListener != null) {
                speechRecognizerListener.onStartedRecording();
            }
            startCountdown();
            startVolumePoll();
        }

        @Override
        public void onFinishedRecording(Transaction transaction) {
            if (speechRecognizerListener != null) {
                speechRecognizerListener.onFinishedRecording();
            }
            stopCountdown();
            stopVolumePoll();
        }

        @Override
        public void onRecognition(Transaction transaction, Recognition recognition) {
            if (speechRecognizerListener != null) {
                speechRecognizerListener.onSuccess(recognition.getText());
                speechRecognizerListener = null;
            }
        }

        @Override
        public void onError(Transaction transaction, String suggestion, TransactionException e) {
            if (speechRecognizerListener != null) {
                speechRecognizerListener.onError(suggestion);
                speechRecognizerListener = null;
            }
        }

    };

}
