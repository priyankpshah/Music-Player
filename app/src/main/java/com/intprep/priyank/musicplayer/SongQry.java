package com.intprep.priyank.musicplayer;

/**
 * Created by priyank on 9/24/16.
 */
public class SongQry {
    private long id;
    private String title;
    private String artist;
    private String album;


    public SongQry(long SId,String STitle,String SArtist,String SAlbum){
        id = SId;
        title = STitle;
        artist = SArtist;
        album = SAlbum;
    }

    public long getId()
    {
        return id;
    }

    public String getTitle(){
        return title;
    }

    public String getArtist(){
        return artist;
    }
    public String getAlbum(){
        return album;
    }
}
