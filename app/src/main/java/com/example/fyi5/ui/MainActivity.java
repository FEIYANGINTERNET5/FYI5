package com.example.fyi5.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.baidu.mapapi.map.MapView;
import com.example.fyi5.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private MapView mMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView = findViewById(R.id.baidu_map_view);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }


    @Override
    public void onClick(View v) {
        Intent jumpIntent;
        switch (v.getId()) {
            case R.id.voice_btn:
                jumpIntent = new Intent(this, VoiceActivity.class);
                startActivity(jumpIntent);
                break;
            case R.id.map_fun_btn:
                jumpIntent = new Intent(this, MapActivity.class);
                startActivity(jumpIntent);
                break;
            case R.id.map_btn:
                jumpIntent = new Intent(this, MapTestActivity.class);
                startActivity(jumpIntent);
            default:
                break;
        }
    }
}