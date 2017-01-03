package com.intprep.priyank.musicplayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by priyank on 10/15/16.
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener{

    private MediaPlayer mPlayer;
    private ArrayList<SongQry> songs;
    private int songPos;
    private String songTitle="";
    private static final int NOTIFY_ID=1;
    private boolean shuffle= false;
    private Random rand;

    private final IBinder musicBind = new MusicBinder();

    public void onCreate(){
        super.onCreate();
        songPos = 0;
        mPlayer = new MediaPlayer();
        rand = new Random();
        initMusicPlayer();
    }

    public void initMusicPlayer(){
        mPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnErrorListener(this);
        mPlayer.setOnCompletionListener(this);
    }

    public void setList(ArrayList<SongQry> thisSong){
        songs = thisSong;
    }

    public class MusicBinder extends Binder{
        MusicService getService(){
            return MusicService.this;
        }
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    public boolean onUnbind(Intent intent){
       mPlayer.stop();
        mPlayer.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if(mPlayer.getCurrentPosition()>=0 ){
            mediaPlayer.reset();
            playNext();
        }

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        mediaPlayer.reset();
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        Intent nintent  = new Intent(this,MainActivity.class);
        /* to launch intent in same activity instead of new instance
            and close all the other activities running on it.
         */
        nintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendintent = PendingIntent.getActivity(this,0,nintent,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendintent)
                .setSmallIcon(R.drawable.android_music_player_play)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(songTitle);
        Notification notification = builder.build();

        startForeground(NOTIFY_ID,notification);
    }
    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    public void playSong(){
        mPlayer.reset();
        SongQry playSong = songs.get(songPos);
        songTitle = playSong.getTitle();
        long currSong = playSong.getId();
        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,currSong);

        try{
          mPlayer.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            Log.e("MUSIC SERVICE","Error setting Data Source",e);
        }
        mPlayer.prepareAsync();
    }

    public void setShuffle(){
        if(shuffle) shuffle=false;
        else { shuffle = true;  }
    }
    public void setSong(int songIndex){
        songPos = songIndex;
    }

    public int getSongPos(){
        return mPlayer.getCurrentPosition();
    }
    public int getDuration(){
        return mPlayer.getDuration();
    }

    public boolean isPlaying(){
        return mPlayer.isPlaying();
    }

    public void pausePlayer(){
        mPlayer.pause();
    }

    public void setSeek(int songPos){
        mPlayer.seekTo(songPos);
    }

    public void go(){
        mPlayer.start();
    }
    public void playPrevious(){
        songPos--;
        if(songPos==0){
            songPos = songs.size()-1;
            playSong();
        }
    }
    public void playNext(){
        if(shuffle){
            int newSong = songPos;
            while(newSong==songPos){
                newSong=rand.nextInt(songs.size());
            }
            songPos=newSong;
        }
        else{
            songPos++;
            if(songPos >=songs.size()) songPos=0;
        }
        playSong();
        }
    }


