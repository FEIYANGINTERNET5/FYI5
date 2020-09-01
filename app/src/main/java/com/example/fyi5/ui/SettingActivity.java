package com.example.fyi5.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fyi5.R;

public class SettingActivity extends AppCompatActivity {

    private ImageView back, one, two, three, four;
    private EditText tel, helpCon, con;
    private Button save;
    private EditText settingVoiceEdit;
    private ImageView mDefaultVoiceImg;
    private ImageView mAddVoiceImg;

    private boolean one_switch_flag = true;
    private boolean two_switch_flag = false;
    private boolean three_switch_flag = true;
    private boolean four_switch_flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        initView();

        one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (one_switch_flag) {
                    one.setImageResource(R.drawable.close2);
                    one_switch_flag = false;
                } else {
                    one.setImageResource(R.drawable.open2);
                    one_switch_flag = true;
                }
            }
        });

        two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (two_switch_flag) {
                    two.setImageResource(R.drawable.close2);
                    settingVoiceEdit.setVisibility(View.GONE);
                    two_switch_flag = false;
                } else {
                    two.setImageResource(R.drawable.open2);
                    settingVoiceEdit.setVisibility(View.VISIBLE);
                    two_switch_flag = true;
                }
            }
        });

        three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (three_switch_flag) {
                    three.setImageResource(R.drawable.close2);
                    three_switch_flag = false;
                } else {
                    three.setImageResource(R.drawable.open2);
                    three_switch_flag = true;
                }
            }
        });

        four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (four_switch_flag) {
                    four.setImageResource(R.drawable.close2);
                    four_switch_flag = false;
                } else {
                    four.setImageResource(R.drawable.open2);
                    four_switch_flag = true;
                }
            }
        });

        mDefaultVoiceImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDefaultVoiceImg.setImageResource(R.mipmap.setting_raido_1);
                mAddVoiceImg.setImageResource(R.mipmap.setting_raido_0);
            }
        });

        mAddVoiceImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDefaultVoiceImg.setImageResource(R.mipmap.setting_raido_0);
                mAddVoiceImg.setImageResource(R.mipmap.setting_raido_1);
            }
        });

    }

    private void initView() {
        back = findViewById(R.id.iv_setting_back);
        one = findViewById(R.id.iv_setting_first);
        two = findViewById(R.id.iv_setting_two);
        three = findViewById(R.id.iv_setting_three);
        four = findViewById(R.id.iv_setting_four);

        tel = findViewById(R.id.et_setting_tel);
        helpCon = findViewById(R.id.et_setting_help_con);

        save = findViewById(R.id.bt_setting_save);
        settingVoiceEdit = findViewById(R.id.setting_voice_text);

        mDefaultVoiceImg = findViewById(R.id.setting_default_voice);
        mAddVoiceImg = findViewById(R.id.setting_add_voice);
    }
}