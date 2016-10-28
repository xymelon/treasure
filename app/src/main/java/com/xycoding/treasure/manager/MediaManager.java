package com.xycoding.treasure.manager;

import android.media.MediaPlayer;

import java.io.IOException;

/**
 * Created by xuyang on 15/6/24.
 */
public class MediaManager {

    private static MediaManager mMediaManager;

    private static MediaPlayer mMediaPlayer;

    private MediaManager() {
        mMediaPlayer = new MediaPlayer();
    }

    public synchronized static MediaManager getInstance() {
        if (mMediaManager == null) {
            mMediaManager = new MediaManager();
        }
        return mMediaManager;
    }

    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    public void startPlaying(String filePath,
                             MediaPlayer.OnPreparedListener preparedListener,
                             MediaPlayer.OnCompletionListener completionListener,
                             MediaPlayer.OnErrorListener errorListener) {
        try {
            stopPlaying();
            mMediaPlayer.reset();
            mMediaPlayer.setOnPreparedListener(preparedListener);
            mMediaPlayer.setOnCompletionListener(completionListener);
            mMediaPlayer.setOnErrorListener(errorListener);
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopPlaying() {
        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mMediaPlayer.setOnPreparedListener(null);
        mMediaPlayer.setOnCompletionListener(null);
        mMediaPlayer.setOnErrorListener(null);
    }
}
