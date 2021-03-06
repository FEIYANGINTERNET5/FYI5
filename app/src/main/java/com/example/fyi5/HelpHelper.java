package com.example.fyi5;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.hardware.Camera;
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
    private MediaPlayer mediaPlayer;
    private Camera mCamera = Camera.open();

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

/*        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    openFlash();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    closeFlash();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();*/
    }

    /**
     * 打开闪光灯
     *
     * @return
     */
    private void openFlash() {
        if (mCamera == null) {
            return;
        }
        Camera.Parameters parameter = mCamera.getParameters();
        parameter.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        mCamera.setParameters(parameter);
    }

    /**
     * 关闭闪光灯
     *
     * @return
     */
    private void closeFlash() {
        if (mCamera == null) {
            return;
        }
        Camera.Parameters parameter = mCamera.getParameters();
        parameter.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        mCamera.setParameters(parameter);
    }


    public void stopOneKeyHelp() {
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
        closeFlash();
    }

    //！！！轻易不要调用这个方法！！！
    public void intelligentHelp() {
        sendSMS();
//        startRecord(60);
    }

    public void sendSMS() {
        PendingIntent paIntent = PendingIntent.getBroadcast(mContext, 0, new Intent(), 0);
        smsManager = SmsManager.getDefault();
        //TODO: 紧急联系人信息处理逻辑
        smsManager.sendTextMessage("15850726699", null,
                "【女性安全】陈女士/先生，您的朋友苗嘉鑫正在使用求助功能，TA的位置为:北京市朝阳区酒仙桥路电子城国际电子城总部。 建议您尽快联系TA确认。",
                paIntent, null);
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
