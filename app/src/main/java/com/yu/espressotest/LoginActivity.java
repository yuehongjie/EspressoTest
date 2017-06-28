package com.yu.espressotest;

import android.content.Intent;
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
            startActivity(new Intent(this, MainActivity.class));
        }else {
            tvErrorMsg.setVisibility(View.VISIBLE);
        }

    }
}
