package com.example.fyi5.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.fyi5.R;

public class LoginActivity extends AppCompatActivity {

    private Button bt_login;
    private EditText et_tel,et_verifyCode;
    private TextView tv_getVerifyCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //初始化所有控件
        initView();

        // 获取验证码 监听
        tv_getVerifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //发送短信
            }
        });


        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //登录检查 手机号验证码是否输入 是否正确

                //跳转至地图界面
                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });


        //捕获手机号 存入数据库

        //捕获验证码 并进行检查
    }

    private void initView() {

        bt_login = findViewById(R.id.bt_login_login);
        et_tel = findViewById(R.id.et_login_tel);
        et_verifyCode = findViewById(R.id.et_login_verifycode);
        tv_getVerifyCode = findViewById(R.id.tv_login_getverifycode);

    }
}