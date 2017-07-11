## 一、Espresso 简介

Espresso 是 Google 自家的 UI 测试框架，google 也很推荐开发者去使用，而且在高版本的 android studio 中会自动引入测试包。


官方链接：https://google.github.io/android-testing-support-library/docs/espresso/index.html

中文翻译：http://www.jianshu.com/p/ef4ad5424784

Google Training： https://developer.android.com/training/testing/ui-testing/espresso-testing.html

测试支持库 API ：https://developer.android.com/topic/libraries/testing-support-library/index.html


Espresso有三种重要体系的类：Matchers(匹配器)、ViewAction(界面行为)、ViewAssertions(界面判断)
- Matchers：是通过匹配条件来查找 UI 组件或过滤 UI
- ViewAction：使用来模拟用户操作界面的行为
- ViewAssertions：对模拟行为操作的View进行变换和结果验证

其三者关系如图所示： 

![关系图](http://ong9pclk3.bkt.clouddn.com/espresso_cheat_sheet.png)


## 二、集成测试 —— 配置工程 添加依赖
在 app module 下的 build.gradle 中引入：


```
apply plugin: 'com.android.application'

android {
    compileSdkVersion 25
    buildToolsVersion "25.0.2"
    defaultConfig {
        ...
        ...
        
        // 需要 Juint 单元测试的支持
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
    }
    ...
}

dependencies {
    compile fileTree(include: ['*.jar'], dir: 'libs')
    
    testCompile 'junit:junit:4.12'
    
    // 如果不使用 okhttp3-idling-resource 异步测试库只需要添加下面一句 
    /*
    androidTestCompile('com.android.support.test.espresso:espresso-core:2.2.2', {
        exclude group: 'com.android.support', module: 'support-annotations'
    })
    */
    
    
    // 如果使用 okhttp3-idling-resource 需要指定 app 和测试的依赖的版本，否则会报 依赖不一致的错误
    
    // 测试的注解依赖
    androidTestCompile 'com.android.support:support-annotations:25.3.1'
    // 主测试库依赖
    androidTestCompile 'com.android.support.test.espresso:espresso-core:2.2.2'
    // jakewharton 写的一个用于 okhttp 的异步测试方法
    androidTestCompile 'com.jakewharton.espresso:okhttp3-idling-resource:1.0.0'
    // 处理 app 依赖与 测试 依赖的 okhttp 版本不一致问题
    androidTestCompile 'com.squareup.okhttp3:okhttp:3.8.0'
    compile 'com.squareup.okhttp3:okhttp:3.8.0'

    
    ...
}

```

**如果使用 okhttp3-idling-resource 需要指定 app 和测试的依赖的版本，否则会报 依赖不一致的错误：**

Error:Conflict with dependency 'com.squareup.okio:okio' in project ':app'. Resolved versions for app (1.13.0) and test app (1.9.0) differ. See http://g.co/androidstudio/app-test-app-conflict for details.

Error:Conflict with dependency 'com.android.support:support-annotations' in project ':app'. Resolved versions for app (25.3.1) and test app (24.0.0) differ. See http://g.co/androidstudio/app-test-app-conflict for details.

Error:Conflict with dependency 'com.squareup.okhttp3:okhttp' in project ':app'. Resolved versions for app (3.8.0) and test app (3.4.0) differ. See http://g.co/androidstudio/app-test-app-conflict for details.

解决方法是修改 app 依赖和 测试依赖库 保持版本一致即可

## 三、创建第一个测试用例
Android studio 默认编写测试用例的目录在 app/src/androidTest/java/包名/ 下

假设我们要测试的是一个登陆功能，界面如下，输入账号和密码，验证并登陆

- 如果账号/密码不正确则会 “账号或密码有误” 提示文字会被显示
- 如果账号/密码正确，则会弹出 “登陆成功” 的吐司提示

![test_login](http://ong9pclk3.bkt.clouddn.com/espresso_login_test.JPG)

**1. 布局文件**

```xml

<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:layout_editor_absoluteY="25dp"
    tools:layout_editor_absoluteX="0dp">

    <Button
        android:id="@+id/btn_login"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="登录"
        android:layout_marginRight="0dp"
        app:layout_constraintRight_toRightOf="@+id/et_password"
        android:layout_marginTop="32dp"
        app:layout_constraintTop_toBottomOf="@+id/et_password" />

    <EditText
        android:id="@+id/et_username"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:ems="10"
        android:inputType="textPersonName"
        android:hint="用户名/手机号"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintHorizontal_bias="0.502"
        android:layout_marginBottom="32dp"
        app:layout_constraintBottom_toTopOf="@+id/et_password" />

    <EditText
        android:id="@+id/et_password"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:ems="10"
        android:inputType="textPassword"
        android:hint="请输入密码"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintHorizontal_bias="0.502" />

    <TextView
        android:id="@+id/tv_error_msg"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginRight="0dp"
        android:layout_marginTop="0dp"
        android:text="账号或密码有误"
        app:layout_constraintRight_toRightOf="@+id/et_password"
        app:layout_constraintTop_toBottomOf="@+id/et_password"
        android:textColor="#f00"
        android:visibility="gone"/>
</android.support.constraint.ConstraintLayout>


```

登陆界面上有两个输入框，一个按钮和一个错误提示，对应的 id 分别为：

- 用户名：R.id.et_username
- 密码：  R.id.et_password
- 登陆：  R.id.btn_login
- 错误提示：R.id.tv_error_msg

**2. LoginActivity.java**

```java

package com.yu.espressotest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText etUsername;
    private EditText etPassword;
    private TextView tvErrorMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
    }

    private void initView() {
        Button btnLogin = (Button) findViewById(R.id.btn_login);
        etUsername = (EditText) findViewById(R.id.et_username);
        etPassword = (EditText) findViewById(R.id.et_password);
        tvErrorMsg = (TextView) findViewById(R.id.tv_error_msg);

        btnLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_login) {
            doLogin();
        }
    }

    private void doLogin() {

        String name = etUsername.getText().toString().trim();
        String passwd = etPassword.getText().toString().trim();

        Log.e("LoginActivity", "name: " + name + "   passwd: " + passwd);

        if ("yu".equals(name) && "001002".equals(passwd)) {
            tvErrorMsg.setVisibility(View.GONE);
            Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
        }else {
            tvErrorMsg.setVisibility(View.VISIBLE);
        }

    }
}


```

**3. 测试代码 LoginActivityTest.java**

先检查输入错误的情况，后检查输入正确的情况

```java

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

    // 测试所依赖的 Activity，会在所有的方法执行之前，启动这个 Activity
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

```

**4.效果图**

![测试效果图](http://ong9pclk3.bkt.clouddn.com/espresso_login_test_result.gif)


## 四、异步测试介绍

Espresso 默认是不会等待后台进程，比如网络请求，如果在异步操作没有完成前，进行检查，那么测试可能会报错，所以需要阻塞主线程而等待后台操作完成再检查，这时你可能会想到 Thread.sleep() ，效果可以达到，但是别忘了此操作是在 Android 主线程进行，那么很可能造成 ANR。

Espresso 提供了一个用于异步测试的接口 IdlingResource，该接口有两个主要的方法

```java

@Override
public boolean isIdleNow() {
    return false;
}

```

```java

@Override
public void registerIdleTransitionCallback(ResourceCallback callback) {
    mCallback = callback;
}

```

第一个方法 isIdleNow() 用来通知主线程，其异步操作的完成，好让主线程更新UI。返回 true 表示异步操作完成，不再阻塞主线程。

第二个方法 registerIdleTransitionCallback 是注册 ResourceCallback 回调，该回调接口如下：


```
public interface ResourceCallback {
    /** * Called when the resource goes from busy to idle. */
    public void onTransitionToIdle();
  }
  
```

当异步操作完成（isIdleNow() 返回 true），需要调用 onTransitionToIdle() 通知 Espresso 转换为空闲状态，如果不通知会报错 ：

    java.lang.IllegalStateException: Resource MainIdlingResource isIdleNow() is returning true, but a message indicating that the resource has transitioned from busy to idle was never sent.
    

## 五、创建异步测试示例

测试用例如图，进入界面 **5s 后才会**在文本上显示 “异步测试操作” 这几个文字，表示异步操作完成。

![image](http://ong9pclk3.bkt.clouddn.com/espress_main_idling_test.JPG)

**1. 界面文件 activity_main.xml** 

界面中间有一个 TextView 文本，文字内容初始为空，当异步加载完成时会赋值，该例就是根据文字是否为空，判断异步加载是否完成


```xml

<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context="com.yu.espressotest.MainActivity">

    <TextView
        android:id="@+id/tv_result"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textSize="20sp"
        app:layout_constraintTop_toTopOf="parent"
        android:layout_marginTop="8dp"
        app:layout_constraintBottom_toBottomOf="parent"
        android:layout_marginBottom="8dp"
        android:layout_marginLeft="8dp"
        app:layout_constraintLeft_toLeftOf="parent"
        android:layout_marginRight="8dp"
        app:layout_constraintRight_toRightOf="parent" />

    <!-- 为了看出效果 加一个倒计时 -->
    <TextView
        android:id="@+id/tv_timer"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginRight="16dp"
        android:textSize="16sp"
        android:textColor="#f00"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        android:layout_marginTop="16dp" />
</android.support.constraint.ConstraintLayout>



```

2. MainActivity.java


```java

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
    
        //模拟异步耗时操作
        
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


```

**3. MainIdlingResource.java**

在测试包下创建 IdlingResource 异步回调类，用于通知异步操作完成

```java

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

    // 构造方法传入 tvResult 文本控件，用来判断异步是否完成
    public MainIdlingResource(TextView textView){
        this.tvResult = textView;
    }

    // 返回一个名字 一般用于日志打印
    @Override
    public String getName() {
        return "MainIdlingResource";
    }

    //通知 Espresso 异步操作是否完成
    @Override
    public boolean isIdleNow() {
        if (mCallback != null) {
        
            //当 tvResult 文字不为空的时候表示异步完成
            if (!TextUtils.isEmpty(tvResult.getText())) {
                // 这里一定要通知 Espresso 切换为空闲状态
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


```

**4. 异步测试代码 MainActivityTest.java**

```java

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


        //使用 IdlingResource 做异步通知
        IdlingResource idlingResource = new MainIdlingResource((TextView) mActivity.getActivity().findViewById(R.id.tv_result));

        //注册 IdlingResources ， 之后的 UI 操作会被阻塞(非 UI 操作之前的代码，好像仍不会阻塞)
        Espresso.registerIdlingResources(idlingResource);

        //异步完成检查结果是否正确
        onView(withId(R.id.tv_result)).check(matches(withText("测试异步操作")));

        //取消 IdlingResource 注册
        Espresso.unregisterIdlingResources(idlingResource);

    }

}


```

**5. 效果图**

![异步效果图](http://ong9pclk3.bkt.clouddn.com/espresso_main_test_result.gif)

## 六、创建 RecyclerView Item 测试

在 Android 中列表分为两种， RecyclerView 和 AdapterView (ListView、GridView、Spinner)，目前多使用 RecyclerView 。

使用 Espresso 测试 AdapterView 比较麻烦些，这里先列出 RecyclerView 的测试方法。

测试用例如图：一个 RecyclerView 列表，点击其中的一个 Item 弹出相应位置的吐司提示。

![image](http://ong9pclk3.bkt.clouddn.com/recyclerview_test1.png)

界面和实现逻辑都比较简单，不解释，我就直接贴代码了

**1. 界面布局 activity_list.xml**


```xml

<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <android.support.v7.widget.RecyclerView
        android:id="@+id/rv_content"
        android:layout_width="match_parent"
        android:layout_height="match_parent"/>

</LinearLayout>

```

**2. Item 布局 item_list.xml**


```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        >
        <TextView
            android:id="@+id/tv_item"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:padding="15dp"
            android:gravity="center"
            android:text="item"
            android:textColor="#000"
            android:textSize="20sp"
            android:background="@drawable/selector_ripple_bg"
            android:clickable="true"/>
        <View
            android:layout_width="match_parent"
            android:layout_height="1dp"
            android:background="#888"
            android:layout_marginLeft="15dp"
            android:layout_marginRight="15dp"/>
    </LinearLayout>


</LinearLayout>

```

其中 android:background="@drawable/selector_ripple_bg" 是设置水波纹效果 ，具体可参考这里 >> [传送门](http://note.youdao.com/noteshare?id=2b6138c14f66efb121a9e57aea14280f&sub=13FF81D98D1147718C120A8714D663D5)。

**3. Adapter 代码 ListAdapter.java**


```java

package com.yu.espressotest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yu.espressotest.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017-7-7.
 * adapter
 */

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ItemHolder>{

    private LayoutInflater inflater;
    private ArrayList<String> mDataList;
    private OnItemClickedListener mListener;

    public ListAdapter(Context context, ArrayList<String> dataList) {
        inflater = LayoutInflater.from(context);
        mDataList = dataList;
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View rootView = inflater.inflate(R.layout.item_list, null, false);

        return new ItemHolder(rootView);
    }

    @Override
    public void onBindViewHolder(final ItemHolder holder, final int position) {
        holder.tvItem.setText(mDataList.get(position));
        if (mListener != null) {
            holder.tvItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemClick(position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    class ItemHolder extends RecyclerView.ViewHolder {

        private TextView tvItem;

        public ItemHolder(View itemView) {
            super(itemView);

            tvItem = (TextView) itemView.findViewById(R.id.tv_item);
        }
    }


    // 点击事件

    public void setOnItemClickListener(OnItemClickedListener listener) {
        this.mListener = listener;
    }

    public interface OnItemClickedListener {
        void onItemClick(int pos);
    }
}


```

**4. ListActivity.java**


```java

package com.yu.espressotest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.yu.espressotest.adapter.ListAdapter;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017-7-7.
 *
 * RecyclerView 列表测试
 */

public class ListActivity extends AppCompatActivity{

    private RecyclerView mRecyclerView;

    private ArrayList<String> mDataList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        initView();

        initData();

        initRecyclerView();
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_content);
    }

    private void initData() {
        mDataList = new ArrayList<>();
        for (int i=0; i<30; i++) {
            mDataList.add("item " + i);
        }
    }

    // 初始化 RecyclerView 显示数据
    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        ListAdapter adapter = new ListAdapter(getApplicationContext(), mDataList);
        adapter.setOnItemClickListener(new ListAdapter.OnItemClickedListener() {
            @Override
            public void onItemClick(int pos) {
                Toast.makeText(ListActivity.this, "点击了 " + mDataList.get(pos), Toast.LENGTH_SHORT).show();
            }
        });
        mRecyclerView.setAdapter(adapter);
    }
}


```

**5. 测试代码 ListActivityTest.java**


```java

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



```


Espresso 提供了一个 **RecyclerViewActions 类**，专门用来测试 RecyclerView，比如滑动到指定的 item ，点击 Item 等。主要有一下几个方法：

- scrollTo - 滑动到匹配的 Item
- scrollToHolder - 滑动到匹配的 Item 的持有者（Holder）
- scrollToPosition - 滑动到指定的位置
- actionOnHolderItem - 对匹配的 Item 的持有者执行行“指定的”操作，如获取 position
- actionOnItem - 对匹配的 Item 执行“指定”操作，如 click() 事件
- actionOnItemAtPosition - 对指定位置的 Item 执行指定的操作，如 click().

如果想要使用该类的话，还需在 app 下的 gridle 中引入 **espresso-contrib 库**，配置如下：

```

androidTestCompile 'com.android.support:support-annotations:25.3.1'
androidTestCompile 'com.android.support.test.espresso:espresso-core:2.2.2'

//测试 RecyclerView 要用到
androidTestCompile('com.android.support.test.espresso:espresso-contrib:2.0') {
    exclude group: 'com.android.support', module: 'appcompat'
    exclude group: 'com.android.support', module: 'support-v4'
    exclude module: 'recyclerview-v7'
}

```

exclude 表示引入库的时候不引入的模块，否则可能会报项目使用的版本和测试版本不一致的问题。 

**6.效果图**

![image](http://ong9pclk3.bkt.clouddn.com/espresso_recyclerview_test.gif)

## 七、创建 Web 测试

在 Android 中经常会使用到 WebView 来展示一些网页数据，而不是跳转到默认的浏览器。而且很多时候还需要做 Html 和 Android 的 Js 交互
，由于 WebView 加载的是 html 界面，界面中的按钮等属于 html 元素，并不是 Android 原生的控件，所以，没有办法使用 onView() 等普通的 UI 测试 API，为此 Google 制定了 WebInteractions 来专门做 web 的测试。

官方文档： https://google.github.io/android-testing-support-library/docs/espresso/web/index.html

中文翻译： http://www.jianshu.com/p/e2d37d04e95e

本测试用例比较简单，使用 WebView 加载一个本地的 Html 文件，html 界面上有一个“按钮”，点击后会使 value 的值改变为 1，并每次点击都会加 1。同时通过 Js 交互，调用 Android 原生的吐司，弹出提示。（下面图 1 是点击 “按钮” 前，图 2 是点击一次按钮）

图 1：

![image](http://ong9pclk3.bkt.clouddn.com/web1.png) 

图 2：

![image](http://ong9pclk3.bkt.clouddn.com/web2.png)

**1. Html 文件**
在 assets 资源文件夹下面，新建 test_web.html 文件，文件内容如下：


```html

<html>
<head>
    <title>Espresso test Web</title>
    <meta charset="utf-8"/>

    <style type="text/css">
        h1 {color: red; font-size:80px;margin:0 auto;text-align:center;}
        p {color: blue; font-size:180px;margin:0 auto;text-align:center;margin-top: 80px; margin-bottom: 80px;}
        button {width: 400px; height: 180px;font-size:100px;}
    </style>

</head>
<body>

<h1 >Hello Espresso Web!</h1>

<!-- 显示结果的元素 id 是 p_value -->
<p id="p_value">Value</p>

<!-- 按钮的 id 是 web_btn -->
<center><button type="button" id="web_btn" onclick="testClick()" >使劲儿</button></center>
<script type="text/javascript">

    var x = 0;

    function testClick() {
        x = x + 1;
        document.getElementById("p_value").innerHTML = x;
        
        //调用 Android 代码弹出吐司提示
        android.showToast("web test " + x);
    }
</script>
</body>

</html>

```

上面代码中有两个元素的 id 是后面要用到的：

- 标签 p 是显示按钮点击后结果的元素，它的 id 是 p_value
- 标签 button 是按钮，点击触发 testClick() 方法，修改 p_value 元素的值，并通过 Js 调用 android  原生代码，弹出吐司，它的 id 是 web_btn

**2. 布局文件 activity_web.xml** 

很简单，只有一个 WebView 控件布满屏幕，其中 WebView 的 id 是 webview

```xml

<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context="com.yu.espressotest.WebActivity">

    <WebView
        android:id="@+id/webview"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:layout_marginBottom="8dp"
        android:layout_marginLeft="8dp"
        android:layout_marginRight="8dp"
        android:layout_marginTop="8dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintHorizontal_bias="0.507"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintVertical_bias="0.415" />
</android.support.constraint.ConstraintLayout>


```

**3. WebView 界面 WebActivity.java**

至少需要设置 WebView 可以进行 Js 交互，一是要对负责调用 Js 交互的方法加上 **@SuppressLint("JavascriptInterface")** 注解，以兼容 17 以下的版本，二是要对接口中被调用的方法，需要加上  **@android.webkit.JavascriptInterface**， 注解，否则方法不会执行。

本例对 WebView 做了详细的设置并添加了测试，可以根据需要，删除部分代码。


```java

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


```


**4. 引入 Espress web 测试包**

在进行测试前，需要在 app 模块下的 gradle 文件中，添加相关 API 的依赖 **espresso-web** 。如


```gradle

// ... , ...

testCompile 'junit:junit:4.12'

androidTestCompile 'com.android.support:support-annotations:25.3.1'

androidTestCompile 'com.android.support.test.espresso:espresso-core:2.2.2'

//测试 webview
androidTestCompile 'com.android.support.test.espresso:espresso-web:2.2.2'


```

**5. 编写测试代码 WebActivityTest.java**


```java

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



```

- 如果布局中只有一个 WebView，可以使用 onWebView() 即可;
- 如果一个布局总有多个 WebView 需要使用 onWebView(withId(R.id.web_view)) 指定
- 更有甚者，如果使用 ViewPager 嵌套 WebView 那么 webview 就不唯一了 (会有多个 id 相同的 webview)，那么可以使用 onWebView(allOf(withId(R.id.webview), isDisplayed())) 指定


**6. 运行效果图**

![web test](http://ong9pclk3.bkt.clouddn.com/espresso_web_test.gif)

以上只是 webview 最简单，也是最基础的测试，测试用例中，我们使用的是 id 来获取 html 元素的，并只进行了简单的点击测试，但 web 的测试却不仅限于此...

## 八、其他测试

**1. ViewPager 测试**

Android 中最常用的控件还有 ViewPager，可以左右滑动切换界面，这时可以使用 swipeLeft() 或者 swipeRight() 进行界面的切换。如


```

//根据 id 找到 ViewPager ,并且为用户可见的
ViewInteraction appCompatViewPager = onView(allOf(withId(R.id.vp_discover), isDisplayed()));

//使用 swipeLeft 执行向左滑动操作
appCompatViewPager.perform(swipeLeft()).perform(swipeLeft());

```


**2. TabLayout 测试**

TabLayout 经常和 ViewPager 配合使用，用于显示所有的 ViewPager 的名称，及快速切换到某一个界面，如果只是使用上面的 ViewPager 执行左右滑动的操作，想滑到最后一个界面，需要执行很多次 swipe 操作，而且可能没有确切的边界。
所以如果可以对 TabLayout 直接进行某个标签的选择，那么效率很高很多...


```

// 找到 TabLayout 中名为 “标签1”的页签，执行点击操作
onView(allOf(withText("标签1"), isDescendantOfA(withId(R.id.tab_layout))))
                .perform(click())
                .check(matches(isDisplayed()));

```



## 九、源码

那就 [戳这里](https://github.com/yuehongjie/EspressoTest) 吧
