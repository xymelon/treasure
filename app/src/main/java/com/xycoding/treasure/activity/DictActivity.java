package com.xycoding.treasure.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import com.xycoding.treasure.R;
import com.xycoding.treasure.databinding.ActivityDictBinding;
import com.xycoding.treasure.fragment.DictResultFragment;

/**
 * Created by xuyang on 2017/3/22.
 */
public class DictActivity extends BaseBindingActivity<ActivityDictBinding> {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_dict;
    }

    @Override
    protected void initControls(Bundle savedInstanceState) {
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
        initViews();
    }

    @Override
    protected void setListeners() {

    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    private void initViews() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.layout_container, DictResultFragment.createInstance());
        transaction.commit();
    }

}
