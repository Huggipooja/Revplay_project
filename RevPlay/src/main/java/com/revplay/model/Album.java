package com.revplay.model;

import java.sql.Date;

public class Album {

    private int albumId;
    private int artistId;
    private String title;
    private Date releaseDate;
    private String artistName;

    public int getAlbumId() {
        return albumId;
    }
    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public int getArtistId() {
        return artistId;
    }
    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }
    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }
   

    public String getArtistName() { 
    	return artistName; 
    	}
    public void setArtistName(String artistName) { 
    	this.artistName = artistName;
    	}
}

