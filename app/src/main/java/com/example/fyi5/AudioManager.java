package com.example.fyi5;

import android.content.Context;
import android.media.MediaRecorder;

import android.text.format.DateFormat;
import android.util.Log;

import com.example.fyi5.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class AudioManager {

    private static MediaRecorder mMediaRecorder = null; // MediaRecorder 实例
    private String fileName; // 录音文件的名称
    private String filePath; // 录音文件存储路径
    private static Context mContext;
    // 录音文件存放目录
    private final String audioSaveDir = mContext.getFilesDir().getAbsolutePath() + "/audiodemo/";


    private static AudioManager audioManager;

    private AudioManager() {
    }

    public static synchronized AudioManager getInstance(Context context) {
        /* Initial：实例化MediaRecorder对象 */
        mContext = context;
        if (audioManager == null) {
            audioManager = new AudioManager();
            mMediaRecorder = new MediaRecorder();
        }
        return audioManager;
    }


    /**
     * 开始录音 使用amr格式
     * 录音文件
     *
     * @return
     */
    public void startRecord() {
        // 开始录音
        try {
            /* setAudioSource/setVedioSource */
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风
            /*
             * 设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式
             * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
             */
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            /* 设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样 */
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            fileName = DateFormat.format("yyyyMMdd_HHmmss", Calendar.getInstance(Locale.CHINA)) + ".m4a";
            if (!FileUtils.isFolderExist(FileUtils.getFolderName(audioSaveDir))) {
                FileUtils.makeFolders(audioSaveDir);
            }
            filePath = audioSaveDir + fileName;
            /* 准备 */
            mMediaRecorder.setOutputFile(filePath);
            mMediaRecorder.prepare();
            /* 开始 */
            mMediaRecorder.start();
            Log.d(AppEnv.TAG,"START RECORD");
        } catch (IllegalStateException | IOException e) {
            Log.e(AppEnv.TAG, Objects.requireNonNull(e.getMessage()));
        }
    }

    /**
     * 停止录音
     */
    public void stopRecord() {
        //有一些网友反应在5.0以上在调用stop的时候会报错，翻阅了一下谷歌文档发现上面确实写的有可能会报错的情况，捕获异常清理一下就行了，感谢大家反馈！
        try {
            mMediaRecorder.stop();
            mMediaRecorder.reset();//set state to idle
            mMediaRecorder.release();
            mMediaRecorder = null;
            filePath = "";
            Log.d(AppEnv.TAG,"STOP RECORD");

        } catch (RuntimeException e) {
            Log.e(AppEnv.TAG, e.toString());
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;

            File file = new File(filePath);
            if (file.exists())
                file.delete();
            filePath = "";
        }
    }
}
