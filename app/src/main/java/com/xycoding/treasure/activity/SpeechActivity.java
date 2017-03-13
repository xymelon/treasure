package com.xycoding.treasure.activity;

import android.os.Bundle;
import android.text.Html;

import com.xycoding.treasure.R;
import com.xycoding.treasure.databinding.ActivitySpeechBinding;
import com.xycoding.treasure.rx.RxViewWrapper;
import com.xycoding.treasure.speech.DictSpeechRecognizer;
import com.xycoding.treasure.speech.SpeechConfiguration;
import com.xycoding.treasure.speech.SpeechRecognizerListener;

import rx.functions.Action1;

/**
 * Created by xuyang on 2017/3/9.
 */
public class SpeechActivity extends BaseBindingActivity {

    private ActivitySpeechBinding mBinding;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_speech;
    }

    @Override
    protected void initControls(Bundle savedInstanceState) {
        mBinding = (ActivitySpeechBinding) binding;
    }

    @Override
    protected void setListeners() {
        subscriptions.add(RxViewWrapper.clicks(mBinding.btnSpeech).subscribe(new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                startSpeech();
            }
        }));
    }

    @Override
    protected void initData(Bundle savedInstanceState) {
        mBinding.tvHint.setTextSize(30);
        mBinding.tvHint.setText(Html.fromHtml(String.format(getString(R.string.speech_language_others), "中", "日")));
    }

    private void startSpeech() {
        String language = getLanguage();
        DictSpeechRecognizer.getInstance(language).start(new SpeechRecognizerListener() {

            @Override
            public void onVolumeChanged(float volume) {
                //volume百分比
                mBinding.rippleBackground.setRippleScale(volume);
            }

            @Override
            public void onStartedRecording() {
                mBinding.tvHint.setText("倾听中...");
            }

            @Override
            public void onFinishedRecording() {
                mBinding.tvHint.setText("识别中...");
                mBinding.rippleBackground.stopRippleAnimation();
            }

            @Override
            public void onSuccess(String result) {
                mBinding.tvHint.setText(result);
            }

            @Override
            public void onError(String error) {
                mBinding.tvHint.setText(error);
                mBinding.rippleBackground.stopRippleAnimation();
            }

        });
    }

    private String getLanguage() {
        if (mBinding.rgLanguagePicker.getCheckedRadioButtonId() == R.id.rb_english) {
            return SpeechConfiguration.ENGLISH;
        } else if (mBinding.rgLanguagePicker.getCheckedRadioButtonId() == R.id.rb_japan) {
            return SpeechConfiguration.JAPANESE;
        }
        return SpeechConfiguration.CHINESE;
    }
}

