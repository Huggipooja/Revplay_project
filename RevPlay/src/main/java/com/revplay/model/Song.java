package com.revplay.model;

import java.sql.Date;

public class Song {

    private int songId;
    private int artistId;        // ✅ REQUIRED
    private String title;
    private String genre;
    private int duration;
    private int playCount;
    private Date releaseDate;
    private int albumId;

    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
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

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }
    
    public int getAlbumId() { 
    	return albumId;
    	}
    public void setAlbumId(int albumId) { 
    	this.albumId = albumId; }
}
