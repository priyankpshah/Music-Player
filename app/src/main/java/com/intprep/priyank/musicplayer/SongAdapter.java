package com.intprep.priyank.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by priyank on 10/6/16.
 */
public class SongAdapter extends BaseAdapter{
    private ArrayList<SongQry> songs;
    private LayoutInflater songInf;

    public SongAdapter(Context c,ArrayList<SongQry> theSong){
        songs = theSong;
        songInf = LayoutInflater.from(c);
    }
    @Override
    public int getCount() {
        return songs.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int pos, View view, ViewGroup parent) {
        LinearLayout songLay = (LinearLayout)songInf.inflate(R.layout.song,parent,false);

        TextView songview = (TextView)songLay.findViewById(R.id.song_title);
        TextView artistview = (TextView)songLay.findViewById(R.id.song_artist);
        TextView albumview = (TextView)songLay.findViewById(R.id.song_album);

        SongQry curSong = songs.get(pos);
        songview.setText(curSong.getTitle());
        artistview.setText(curSong.getArtist());
        albumview.setText(curSong.getAlbum());

        songLay.setTag(pos);

        return songLay;




    }
}
