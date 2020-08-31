package com.example.fyi5;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.util.Log;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class HelpHelper {
    private Context mContext;
    private SmsManager smsManager;
    private AudioManager audioManager;
    private String audioSaveDir;
    MediaPlayer mediaPlayer;

    public HelpHelper(Context mContext) {
        this.mContext = mContext;
        audioSaveDir = mContext.getFilesDir().getAbsolutePath() + "/audiodemo/";
        audioManager = AudioManager.getInstance(mContext);


    }

    public void startOneKeyHelp() {
        mediaPlayer = new MediaPlayer();

        AssetFileDescriptor fd = null;
        try {
            fd = mContext.getAssets().openFd("onekey_help_alert.mp3");
            mediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void stopOneKeyHelp() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
    }

    //！！！轻易不要调用这个方法！！！
    public void intelligentHelp() {
        sendSMS();
        startRecord(60);
    }

    public void sendSMS() {
        PendingIntent paIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(), 0);
        smsManager = SmsManager.getDefault();
        //TODO: 紧急联系人信息处理逻辑
        smsManager.sendTextMessage("15850726699", null, "来自FYI5的测试短信，请勿回复", paIntent,
                null);
        Log.d(AppEnv.TAG, "send SMS");
    }

    public void startRecord(int seconds) {
        audioManager.stopRecord();
        new Timer("audioRecorder").schedule(new TimerTask() {
            @Override
            public void run() {
                audioManager.stopRecord();
            }
        }, seconds);
    }


}
