package com.example.fyi5.ui.test;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fyi5.AppEnv;
import com.example.fyi5.AudioManager;
import com.example.fyi5.R;

public class VoiceActivity extends AppCompatActivity implements View.OnClickListener {
    SmsManager smsManager;
    AudioManager audioManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice);
        // 录音文件存放目录
        String audioSaveDir = getFilesDir().getAbsolutePath() + "/audiodemo/";
        audioManager = AudioManager.getInstance(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_sms_btn:
                PendingIntent paIntent = PendingIntent.getBroadcast(this, 0, new Intent(), 0);
                smsManager = SmsManager.getDefault();
                //TODO: 紧急联系人信息处理逻辑
                smsManager.sendTextMessage("15850726699", null, "来自FYI5的测试短信，请勿回复", paIntent,
                        null);
                Log.d(AppEnv.TAG, "send SMS");
                break;
            case R.id.start_audio_btn:
                audioManager.startRecord();
                break;
            case R.id.stop_audio_btn:
                audioManager.stopRecord();
                break;
            default:
                break;
        }
    }
}