package com.intprep.priyank.musicplayer;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //Variable to store songlist and store it into model class.
    private ArrayList<SongQry> songlist;
    private ListView songview;

    //Variable For using Service
    private MusicService musicServc;
    private Intent playintent;
    private boolean musicbound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songview = (ListView)findViewById(R.id.song_list);
        songlist = new ArrayList<SongQry>();
        getSongList();

        Collections.sort(songlist, new Comparator<SongQry>() {
            @Override
            public int compare(SongQry a, SongQry b) {
                return a.getTitle().compareTo(b.getTitle());
            }
        });

        SongAdapter songadpt = new SongAdapter(this,songlist);
        songview.setAdapter(songadpt);

    }
    public void getSongList(){
        ContentResolver musicResolver = getContentResolver();
        Uri musicuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCur = musicResolver.query(musicuri,null,null,null,null);

        if(musicCur!=null && musicCur.moveToFirst()){
            int titleCol = musicCur.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int IDCol = musicCur.getColumnIndex(MediaStore.Audio.Media._ID);
            int AlbumCol = musicCur.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int ArtistCol = musicCur.getColumnIndex(MediaStore.Audio.Media.ARTIST);

            do{
                long thisId = musicCur.getLong(IDCol);
                String thisTitle = musicCur.getString(titleCol);
                String thisAlbum = musicCur.getString(AlbumCol);
                String thisArtist = musicCur.getString(ArtistCol);
                songlist.add(new SongQry(thisId,thisTitle,thisArtist,thisAlbum));
            }while(musicCur.moveToNext());
        }
    }
    private ServiceConnection musicConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;

            musicServc = binder.getService();
            musicServc.setList(songlist);
            musicbound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicbound = false;
        }
    };
    @Override
    protected void onStart(){
        super.onStart();
        if(playintent==null){
            playintent = new Intent(this,MusicService.class);
            bindService(playintent,musicConnection, Context.BIND_AUTO_CREATE);
            startService(playintent);
        }
    }

    public void songPicked(View view){
        musicServc.setSong(Integer.parseInt(view.getTag().toString()));
        musicServc.playSong();
    }

    public boolean onOptionItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.shuffle:
                break;
            case R.id.action_stop:
                stopService(playintent);
                musicServc=null;
                System.exit(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onDestroy(){
        stopService(playintent);
        musicServc = null ;
        super.onDestroy();
    }
}
