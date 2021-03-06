package com.example.fyi5.ui.test;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.example.fyi5.AppEnv;
import com.example.fyi5.R;
import com.example.fyi5.ui.CommentActivity;
import com.example.fyi5.ui.LoginActivity;
import com.example.fyi5.ui.SettingActivity;

public class MainTestActivity extends AppCompatActivity implements View.OnClickListener {

    final MKOfflineMap mOffline = new MKOfflineMap();
    SmsManager smsManager;

    private Button bt_set;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_test);


    }


    @Override
    public void onClick(View v) {
        Intent jumpIntent;
        switch (v.getId()) {
            case R.id.main_voice_btn:
                jumpIntent = new Intent(this, VoiceActivity.class);
                startActivity(jumpIntent);
                break;
            case R.id.test_map_fun_btn:
                jumpIntent = new Intent(this, LocationActivity.class);
                startActivity(jumpIntent);
                break;
            case R.id.test_map_btn:
                jumpIntent = new Intent(this, MapActivity.class);
                startActivity(jumpIntent);
            case R.id.test_map_download_btn:
                // 传入MKOfflineMapListener，离线地图状态发生改变时会触发该回调
                mOffline.init(new MKOfflineMapListener() {
                    @Override
                    public void onGetOfflineMapState(int i, int i1) {
                        mOffline.update(131);
                    }
                });
                mOffline.start(131);
                break;
            case R.id.test_main_btn:
                jumpIntent = new Intent(this, com.example.fyi5.ui.MainActivity.class);
                startActivity(jumpIntent);
                break;
            case R.id.test_main_setting:
                jumpIntent = new Intent(this, SettingActivity.class);
                startActivity(jumpIntent);
                break;
            case R.id.test_main_login:
                jumpIntent = new Intent(this, LoginActivity.class);
                startActivity(jumpIntent);
                break;
            case R.id.test_main_comment:
                jumpIntent = new Intent(this, CommentActivity.class);
                startActivity(jumpIntent);
                break;
            default:
                break;
        }
    }
}