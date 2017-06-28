package com.yu.espressotest;

import android.support.test.espresso.Espresso;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

/**
 * Created by Administrator on 2017-6-19.
 * 主界面测试
 */

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginActivityTest {
    private String name;
    private String passwd;
    private String errorPasswd;

    @Rule
    public ActivityTestRule<LoginActivity> mActivity = new ActivityTestRule<>(LoginActivity.class);

    @Before
    public void initData(){
        name = "yu";
        passwd = "001002";
        errorPasswd = "123456";
    }

    //测试登录
    @Test
    public void checkLogin(){
        //检查错误的输入
        checkErrorInput();
        //检查正确的输入
        checkRightInput();
    }

    /**
     * 检查输入正确的账号密码 结果是否符合预期
     */
    private void checkRightInput() {
        // 2. 正确输入
        //先清除原来的文本
        onView(withId(R.id.et_username)).perform(clearText());
        onView(withId(R.id.et_password)).perform(clearText());

        //输入用户名
        onView(withId(R.id.et_username)).perform(typeText(name));
        //关闭软键盘（如果开启）
        Espresso.closeSoftKeyboard();
        //输入密码
        onView(withId(R.id.et_password)).perform(typeText(passwd));
        //关闭软键盘（如果开启）
        Espresso.closeSoftKeyboard();
        //点击登录按钮
        onView(withId(R.id.btn_login)).perform(click());

        //判断成功 吐司 是否弹出    如何判断吐司 --> https://stackoverflow.com/questions/28390574/checking-toast-message-in-android-espresso
        onView(withText("登录成功")).inRoot(withDecorView(not(mActivity.getActivity().getWindow().getDecorView()))).check(matches(isDisplayed()));
    }

    /**
     * 检查输入错误的账号密码 结果是否符合预期
     */
    private void checkErrorInput() {
        // 1. 错误输入
        //输入用户名
        onView(withId(R.id.et_username)).perform(typeText(name));
        //关闭软键盘（如果开启）
        Espresso.closeSoftKeyboard();
        //输入密码
        onView(withId(R.id.et_password)).perform(typeText(errorPasswd));
        //关闭软键盘（如果开启）
        Espresso.closeSoftKeyboard();
        //点击登录按钮
        onView(withId(R.id.btn_login)).perform(click());
        //判断错误提示是否显示
        onView(withId(R.id.tv_error_msg)).check(matches(isDisplayed()));
    }

}