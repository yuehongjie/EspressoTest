package com.yu.espressotest;

import android.content.Intent;
import android.support.test.espresso.web.webdriver.Locator;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static android.support.test.espresso.web.assertion.WebViewAssertions.webMatches;
import static android.support.test.espresso.web.sugar.Web.onWebView;
import static android.support.test.espresso.web.webdriver.DriverAtoms.findElement;
import static android.support.test.espresso.web.webdriver.DriverAtoms.getText;
import static android.support.test.espresso.web.webdriver.DriverAtoms.webClick;
import static org.hamcrest.Matchers.containsString;

/**
 * Created by Administrator on 2017-7-11.
 * webview 测试
 *
 * 原文：  https://google.github.io/android-testing-support-library/docs/espresso/web/index.html
 * 翻译：  http://www.jianshu.com/p/e2d37d04e95e
 */

@RunWith(JUnit4.class)
@LargeTest
public class WebActivityTest {

    //参数 3 指定是否自动加载 WebActivity
    @Rule
    public ActivityTestRule<WebActivity> mActivityRule = new ActivityTestRule<WebActivity>(WebActivity.class, false, false) {
        @Override
        protected void afterActivityLaunched() {
            //允许 JS!
            onWebView().forceJavascriptEnabled();
        }
    };

    @Test
    public void testWeb() {

        //使用 Intent 传递数据
        Intent intent = new Intent();
        intent.putExtra("url", "https://www.baidu.com/s?wd=0");

        //懒启动 Activity
        mActivityRule.launchActivity(intent);

        //找到布局中的 WebView，如果有多个 WebView 可以使用 onWebView(withId(R.id.web_view)) 的方式指定
        onWebView()
                //通过 Id 找到相关的 元素（按钮）
                .withElement(findElement(Locator.ID, "web_btn"))
                //执行两次点击操作
                .perform(webClick())
                .perform(webClick());

        //通过 id 找到 value 元素，并进行断言（上面点击了两次，如果结果为 2 则断言正确。）
        onWebView().withElement(findElement(Locator.ID, "p_value"))
                .check(webMatches(getText(), containsString("2")));

        //为了看到效果 稍睡几秒 （危险操作）
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }
}