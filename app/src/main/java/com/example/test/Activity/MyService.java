package com.example.test.Activity;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import com.example.test.R;

import java.io.File;

public class MyService extends Service {

    public MediaPlayer mediaPlayer = new MediaPlayer();;

    class MusicBinder extends Binder {

//       void musicChange() {
//           if (mediaPlayer.isPlaying()) {
//               mediaPlayer.pause();
//           } else if (!mediaPlayer.isPlaying()) {
//               mediaPlayer.start();
//           }
//        }

    }

    @Override
    public void onCreate() {
        Log.d("TAG", "onCreate");
        super.onCreate();
        initMediaPlayer();
        mediaPlayer = MediaPlayer.create(this, R.raw.music);
        mediaPlayer.start();
        Log.d("TAG", "st");
    }

    public void initMediaPlayer() {
        mediaPlayer = MediaPlayer.create(this, R.raw.music);
    }

    //创建实例
    private MusicBinder binder = new MusicBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }


}
