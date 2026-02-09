package com.revplay.model;

import java.sql.Date;

public class Playlist {

    private int playlistId;
    private int userId;
    private String name;
    private String description;
    private String isPublic;
    private Date createdAt;
    private String ownerName;

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int playlistId) {
        this.playlistId = playlistId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    

public String getOwnerName() { 
	return ownerName; 
	}
public void setOwnerName(String ownerName) { 
	this.ownerName = ownerName;
	}
}

