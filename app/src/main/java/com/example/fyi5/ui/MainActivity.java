package com.example.fyi5.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fyi5.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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