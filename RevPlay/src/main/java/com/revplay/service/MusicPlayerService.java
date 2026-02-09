package com.revplay.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revplay.dao.ListeningHistoryDAO;
import com.revplay.model.Song;

public class MusicPlayerService {

    private static final Logger logger =
            LogManager.getLogger(MusicPlayerService.class);

    private boolean isPlaying = false;
    private boolean isPaused = false;
    private Song currentSong;

    public void play(Song song, int userId) {

        this.currentSong = song;
        isPlaying = true;
        isPaused = false;

        logger.info("Playing song '{}' for userId={}",
                song.getTitle(), userId);

        ListeningHistoryDAO historyDAO = new ListeningHistoryDAO();
        historyDAO.addHistory(userId, song.getSongId());
    }

    public void pause() {

        if (isPlaying && !isPaused) {
            isPaused = true;
            logger.info("Playback paused for song '{}'",
                    currentSong != null ? currentSong.getTitle() : "N/A");
        } else {
            logger.warn("Pause requested but no active playback");
        }
    }

    public void resume() {

        if (isPlaying && isPaused) {
            isPaused = false;
            logger.info("Playback resumed for song '{}'",
                    currentSong != null ? currentSong.getTitle() : "N/A");
        } else {
            logger.warn("Resume requested but song is not paused");
        }
    }

    public void repeat() {

        if (isPlaying && currentSong != null) {
            logger.info("Replaying song '{}'", currentSong.getTitle());
        } else {
            logger.warn("Repeat requested but no song is playing");
        }
    }

    public void stop() {

        logger.info("Stopping playback");

        isPlaying = false;
        isPaused = false;
        currentSong = null;
    }
}
