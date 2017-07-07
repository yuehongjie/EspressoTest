package com.yu.espressotest;

import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

/**
 * Created by Administrator on 2017-7-7.
 * RecyclerView 测试
 */

@RunWith(JUnit4.class)
@LargeTest
public class ListActivityTest {

    @Rule
    public ActivityTestRule<ListActivity> mActivity = new ActivityTestRule<>(ListActivity.class);

    @Test
    public void testList(){

        testItemClick();

        testItemClick();
    }

    private void testItemClick() {
        //随机得到一个 item 位置
        int pos = (int) (Math.random() * 29);
        //滚动到 item 位置
        onView(withId(R.id.rv_content)).perform(RecyclerViewActions.scrollToPosition(pos));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //点击 item
        onView(withId(R.id.rv_content)).perform(RecyclerViewActions.actionOnItemAtPosition(pos, click()));

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
