package com.example.framework.manager;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;

import com.example.framework.utils.LogUtils;

import java.io.IOException;

public class MediaPlayerManager {
    public static final int MEDIA_STATUS_PLAY = 0;
    public static final int MEDIA_STATUS_PAUSE = 1;
    public static final int MEDIA_STATUS_STOP = 2;

    public static int MEDIA_STATUS = MEDIA_STATUS_STOP;

    private MediaPlayer mediaPlayer;

    private OnMusicProgressListener musicProgressListener;

    private static final int  H_PROGRESS = 1000;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case H_PROGRESS:
                        if(musicProgressListener != null){
                            int current = getCurrentPosition();
                            int pos = (int)(((float)current)/((float)getDuration())*100);
                            musicProgressListener.onProgress(current,pos);
                            handler.sendEmptyMessageDelayed(H_PROGRESS,1000);
                        }
                    break;
            }

            return false;
        }
    });


    public  MediaPlayerManager(){
        mediaPlayer = new MediaPlayer();
    }

    public  void startplay(String path){
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
            mediaPlayer.start();
            MEDIA_STATUS = MEDIA_STATUS_PLAY;
            handler.sendEmptyMessage(H_PROGRESS);
        } catch (IOException e) {
            LogUtils.e(e.toString());
            e.printStackTrace();
        }
    }

    public  void startplay(AssetFileDescriptor path){
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path.getFileDescriptor(),
                    path.getStartOffset(), path.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
            MEDIA_STATUS = MEDIA_STATUS_PLAY;
            handler.sendEmptyMessage(H_PROGRESS);
        } catch (IOException e) {
            LogUtils.e(e.toString());
            e.printStackTrace();
        }
    }

    public boolean isPlaying(){
        return mediaPlayer.isPlaying();
    }

    public void pauseplay(){

        if(isPlaying()){
            mediaPlayer.pause();
            MEDIA_STATUS = MEDIA_STATUS_PAUSE;
            removeHandler();
        }

    }

    public void continueplay(){
        mediaPlayer.start();
        MEDIA_STATUS = MEDIA_STATUS_PLAY;
        handler.sendEmptyMessage(H_PROGRESS);

    }


    public void stopplay(){
        mediaPlayer.stop();
        MEDIA_STATUS = MEDIA_STATUS_STOP;
        removeHandler();
    }

    public int getCurrentPosition(){
        return mediaPlayer.getCurrentPosition();
    }

    public int getDuration(){
        return mediaPlayer.getDuration();
    }

    public void setOnComplteionListener(MediaPlayer.OnCompletionListener listener){
        mediaPlayer.setOnCompletionListener(listener);
    }
    public void setOnErrorListener(MediaPlayer.OnErrorListener listener){
        mediaPlayer.setOnErrorListener(listener);
    }
    public void setOnProgressListener(OnMusicProgressListener listener){
        musicProgressListener = listener;
    }

    public void isL(boolean isLooping){
        mediaPlayer.setLooping(isLooping);
    }

    public void seekto(int ms){
        mediaPlayer.seekTo(ms);
    }


    public interface  OnMusicProgressListener{
        void onProgress(int progress,int pos);
    }

    public void removeHandler() {
        if (handler != null) {
            handler.removeMessages(H_PROGRESS);
        }
    }

}
