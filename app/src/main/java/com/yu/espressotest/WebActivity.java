package com.yu.espressotest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * 网页测试
 *  https://www.baidu.com/s?wd=0
 */
public class WebActivity extends AppCompatActivity {

    private WebView mWebView;

    private String mUrl;

    //本地 html
    private  static final String LOCAL_URL="file:///android_asset/test_web.html";

    private String TAG = "WebActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        Intent intent = getIntent();
        if (intent != null) {
            mUrl = intent.getStringExtra("url"); //获取传递进来的 url
        }

        Log.e(TAG, "intent url : " + mUrl);

        // 初始化 WebView
        initWebView();

        // 初始化 Js 交互
        initJsInterface();
    }

    /**
     * 初始化 WebView
     */
    private void initWebView() {
        mWebView = (WebView) findViewById(R.id.webview);

        //mWebView.loadUrl(mUrl);//测试传递进来的 url

        mWebView.loadUrl(LOCAL_URL);

        //设置 WebView
        WebSettings settings = mWebView.getSettings();
        //设置支持 Js 交互
        settings.setJavaScriptEnabled(true);
        //设置自适应屏幕
        settings.setUseWideViewPort(true);//将图片调整到适合 webview 的大小
        settings.setLoadWithOverviewMode(true);//将网页缩放至适应屏幕
        //设置支持缩放操作
        settings.setSupportZoom(true);//支持缩放，默认为true
        settings.setBuiltInZoomControls(true);//设置使用原生的缩放控件，若为 false 则 webview 不可缩放
        settings.setDisplayZoomControls(false);//隐藏原生的缩放控件
        //设置缓存策略
        //settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);//优先加载缓存

        //设置 WebViewClient
        mWebView.setWebViewClient(new WebViewClient() {
            //复写 shouldOverrideUrlLoading()，否则点击网页中的链接，将会跳转到浏览器
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {

                Log.d(TAG, "开始加载：" + url);

                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                Log.d(TAG, "加载完成：" + url);

                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Toast.makeText(WebActivity.this, "加载出错了", Toast.LENGTH_SHORT).show();
                super.onReceivedError(view, request, error);
            }
        });

        //设置WebChromeClient类
        mWebView.setWebChromeClient(new WebChromeClient() {


            //获取网站标题
            @Override
            public void onReceivedTitle(WebView view, String title) {
                Log.d(TAG, "加载标题：" + title);
            }


            //获取加载进度
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                Log.d(TAG, "加载进度：" + newProgress);
            }
        });


    }

    /**
     * 设置 Js 交互 需要添加 @SuppressLint("JavascriptInterface") 注解
     */
    @SuppressLint("JavascriptInterface")
    private void initJsInterface(){
        //参数 2 的值与 html 中指定 Js交互 方法的对象有关系，这里是 "android"
        mWebView.addJavascriptInterface(new MyJsInterface(), "android");
    }

    class MyJsInterface {

        public MyJsInterface(){}

        //Js 回调 Android 端的代码，需要加上 注解 否则方法不会执行
        @android.webkit.JavascriptInterface
        public void showToast(String msg){
            Toast.makeText(WebActivity.this, msg, Toast.LENGTH_SHORT).show();
        }
    }


    //点击返回上一页面而不是退出浏览器
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    //销毁Webview
    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            mWebView.clearHistory();

            ((ViewGroup) mWebView.getParent()).removeView(mWebView);
            mWebView.destroy();
            mWebView = null;
        }
        super.onDestroy();
    }
}