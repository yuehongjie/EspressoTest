package com.yu.espressotest;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.widget.TextView;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by yu on 2017/6/28.
 * 测试 MainActivity
 * Espresso 默认对代码中的异步是不会等待的
 */

@RunWith(JUnit4.class)
@LargeTest
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivity = new ActivityTestRule<>(MainActivity.class);

    //异步测试
    @Test
    public void idlingTest() throws InterruptedException {

        //不做异步处理 会报错 因为此时 TextView 中的文字还为空串
        //Thread.sleep(6000);//这很危险 可能会导致 ANR
        //onView(withId(R.id.tv_result)).check(matches(withText("测试异步操作")));

        IdlingResource idlingResource = new MainIdlingResource((TextView) mActivity.getActivity().findViewById(R.id.tv_result));

        Espresso.registerIdlingResources(idlingResource);

        onView(withId(R.id.tv_result)).check(matches(withText("测试异步操作")));

        Espresso.unregisterIdlingResources(idlingResource);

    }

}
