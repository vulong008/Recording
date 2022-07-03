package com.example.recording.Services;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.recording.Database.DBHelper;
import com.example.recording.Models.RecordingItem;

import java.io.File;
import java.io.IOException;

public class RecordService extends Service {

    MediaRecorder mediaRecorder;
    long mStartingTimeMilis = 0;
    long mElapsedMillis = 0;
    File file;
    String fileName;
    DBHelper dbHelper;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        StartRecord();
        return START_STICKY;
    }

    private void StartRecord() {
        Long tsLong = System.currentTimeMillis() / 1000;
        String ts = tsLong.toString();
        fileName = "audio_" + ts;
        file = new File(Environment.getExternalStorageDirectory() + "/MyShowRecord/"+fileName+".mp3");
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setOutputFile(file.getAbsolutePath());
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setAudioChannels(1);
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            mStartingTimeMilis = System.currentTimeMillis();
        } catch (IOException ioException) {
         Log.d("aaa","lỗi");
        }

    }

    private void stopRecord() {
        mediaRecorder.stop();
        mElapsedMillis = (System.currentTimeMillis() - mStartingTimeMilis);
        mediaRecorder.release();
        Toast.makeText(getApplicationContext(), "Recording ok Saved" + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        RecordingItem recordingItem = new RecordingItem(fileName, file.getAbsolutePath()
                , mElapsedMillis, System.currentTimeMillis());
        dbHelper.addRecording(recordingItem);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        dbHelper = new DBHelper(getApplicationContext());

    }

    @Override
    public void onDestroy() {

        if(mediaRecorder!=null) {
            stopRecord();
        }
        super.onDestroy();
    }
}
