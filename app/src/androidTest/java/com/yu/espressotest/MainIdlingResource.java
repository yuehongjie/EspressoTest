package com.yu.espressotest;

import android.support.test.espresso.IdlingResource;
import android.text.TextUtils;
import android.widget.TextView;

/**
 * Created by yu on 2017/6/28.
 * 异步通知
 */

public class MainIdlingResource implements IdlingResource{

    private ResourceCallback mCallback;
    private TextView tvResult;

    public MainIdlingResource(TextView textView){
        this.tvResult = textView;
    }

    @Override
    public String getName() {
        return "MainIdlingResource";
    }

    @Override
    public boolean isIdleNow() {
        if (mCallback != null) {
            if (!TextUtils.isEmpty(tvResult.getText())) {
                mCallback.onTransitionToIdle();
                return true;
            }else {
                return false;
            }
        }else {
            return true;
        }
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        mCallback = callback;
    }
}
