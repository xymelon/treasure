package com.xycoding.treasure.utils;

import android.net.Uri;
import android.os.Environment;
import android.os.FileObserver;
import android.support.annotation.NonNull;

public class ScreenshotObserver extends FileObserver {

    private static final String PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Screenshots";

    private OnScreenshotListener mListener;
    private String mLastTakenPath;

    public ScreenshotObserver(@NonNull OnScreenshotListener listener) {
        super(PATH);
        mListener = listener;
    }

    @Override
    public void onEvent(int event, String path) {
        if (path == null || event != FileObserver.CLOSE_WRITE) {
            //Don't care.
            return;
        }
        if (path.equalsIgnoreCase(mLastTakenPath)) {
            //This event has been observed before.
            return;
        }
        mLastTakenPath = path;
        mListener.onScreenshotTaken(Uri.parse(PATH + path));
    }

    public interface OnScreenshotListener {
        void onScreenshotTaken(Uri uri);
    }

} 