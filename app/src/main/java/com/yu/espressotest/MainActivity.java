package com.yu.espressotest;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView tvResult;
    private TextView tvTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
    }

    private void initView() {
        tvResult = (TextView) findViewById(R.id.tv_result);
        tvTimer = (TextView) findViewById(R.id.tv_timer);
    }

    private void initData() {
        tvResult.postDelayed(new Runnable() {
            @Override
            public void run() {
                tvResult.setText("测试异步操作");
            }
        }, 5000);

        startTimer();
    }

    //开启倒计时
    CountDownTimer countTimer;
    private void startTimer() {
        countTimer = new CountDownTimer(6000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                Log.d("onTick", millisUntilFinished / 1000 + "");
                tvTimer.setText("倒计时 " + millisUntilFinished / 1000 + " s");
            }

            @Override
            public void onFinish() {
                tvTimer.setText("倒计时 0 s");
            }
        };
        countTimer.start();
    }

    @Override
    protected void onDestroy() {
        if (countTimer != null) {
            countTimer.cancel();
        }
        super.onDestroy();
    }
}
