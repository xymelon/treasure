package com.xycoding.treasure.activity.transition;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.widget.Toast;

import com.xycoding.treasure.R;
import com.xycoding.treasure.activity.BaseBindingActivity;
import com.xycoding.treasure.databinding.ActivityTransitionBinding;

/**
 * Created by xymelon on 2018/2/6.
 */
public class TransitionActivity extends BaseBindingActivity<ActivityTransitionBinding> {

    public static final String BUNDLE_KEY_TRANSITION = "bundle_key_transition";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_transition;
    }

    @Override
    protected void initControls(Bundle savedInstanceState) {

    }

    @Override
    protected void setListeners() {
        mBinding.ivTest.setOnClickListener(v -> startTransitionActivity());
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    private void startTransitionActivity() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Intent intent = new Intent(this, TransitionSecondActivity.class);
            intent.putExtra(BUNDLE_KEY_TRANSITION, true);
            final ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    this, mBinding.ivTest, "transition_image");
            startActivity(intent, options.toBundle());
        } else {
            Toast.makeText(this, "需要API 21+", Toast.LENGTH_SHORT).show();
        }
    }

}
