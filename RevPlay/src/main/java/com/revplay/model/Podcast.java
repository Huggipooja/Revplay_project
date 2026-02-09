package com.revplay.model;

import java.sql.Date;

public class Podcast {

    private int podcastId;
    private int artistId;
    private String title;
    private String host;
    private String genre;
    private int duration;
    private Date releaseDate;

    public int getPodcastId() { return podcastId; }
    public void setPodcastId(int podcastId) { this.podcastId = podcastId; }

    public int getArtistId() { return artistId; }
    public void setArtistId(int artistId) { this.artistId = artistId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public Date getReleaseDate() { return releaseDate; }
    public void setReleaseDate(Date releaseDate) { this.releaseDate = releaseDate; }
}
