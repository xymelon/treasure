package com.xycoding.treasure.speech;

import android.os.Handler;

import com.nuance.speechkit.DetectionType;
import com.nuance.speechkit.Language;
import com.nuance.speechkit.Recognition;
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
    private Handler volumeHandler = new Handler();
    private Session speechSession;
    private Transaction transaction;
    private SpeechListener speechListener;

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
    public void start(final SpeechListener listener) {
        stop();
        speechListener = listener;
        transaction = speechSession.recognize(options, recognizerListener);
    }

    @Override
    public void stop() {
        speechListener = null;
        if (transaction != null) {
            transaction.cancel();
        }
    }

    @Override
    public void release() {

    }

    private void startVolumePoll() {
        volumePoller.run();
    }

    private void stopVolumePoll() {
        volumeHandler.removeCallbacks(volumePoller);
    }

    /**
     * Every 50 milliseconds we should update the volume meter in our UI.
     */
    private Runnable volumePoller = new Runnable() {
        @Override
        public void run() {
            if (transaction != null && speechListener != null) {
                //音量范围[0-90]
                float percent = transaction.getAudioLevel() / 90.f;
                speechListener.onVolumeChanged(percent);
                volumeHandler.postDelayed(volumePoller, 50);
            }
        }
    };

    private Transaction.Listener recognizerListener = new Transaction.Listener() {

        @Override
        public void onStartedRecording(Transaction transaction) {
            if (speechListener != null) {
                speechListener.onStartedRecording();
            }
            startVolumePoll();
        }

        @Override
        public void onFinishedRecording(Transaction transaction) {
            if (speechListener != null) {
                speechListener.onFinishedRecording();
            }
            stopVolumePoll();
        }

        @Override
        public void onRecognition(Transaction transaction, Recognition recognition) {
            if (speechListener != null) {
                speechListener.onSuccess(recognition.getText());
                speechListener = null;
            }
        }

        @Override
        public void onError(Transaction transaction, String suggestion, TransactionException e) {
            if (speechListener != null) {
                speechListener.onError(suggestion);
                speechListener = null;
            }
        }

    };

}
