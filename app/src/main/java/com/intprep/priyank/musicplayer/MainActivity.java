package com.intprep.priyank.musicplayer;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ArrayList<SongQry> songlist;
    private ListView songview;

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

}
