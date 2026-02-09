package com.revplay.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revplay.model.Song;
import com.revplay.util.DBConnection;

public class ListeningHistoryDAO {

    private static final Logger logger = LogManager.getLogger(ListeningHistoryDAO.class);

    /* ================= SAVE PLAY HISTORY ================= */
    public void addHistory(int userId, int songId) {

        logger.info("Saving listening history: userId={}, songId={}", userId, songId);

        String sql =
            "INSERT INTO listening_history (history_id, user_id, song_id, listened_at) " +
            "VALUES (LISTENING_HISTORY_SEQ.NEXTVAL, ?, ?, SYSDATE)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false); // safety

            ps.setInt(1, userId);
            ps.setInt(2, songId);

            int rows = ps.executeUpdate();

            if (rows > 0) {
                conn.commit();   // ✅ CRITICAL
                logger.info("Listening history saved successfully");
            } else {
                conn.rollback();
                logger.warn("Listening history insert failed");
            }

        } catch (Exception e) {
            logger.error("Error saving listening history", e);
        }
    }


    /* ================= GET USER HISTORY ================= */
    public List<Song> getUserHistory(int userId) {

        logger.info("Fetching listening history for userId={}", userId);

        List<Song> songs = new ArrayList<>();

        String sql =
            "SELECT s.song_id, s.title, s.genre, h.listened_at " +
            "FROM listening_history h " +
            "JOIN songs s ON h.song_id = s.song_id " +
            "WHERE h.user_id = ? " +
            "ORDER BY h.listened_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Song s = new Song();
                s.setSongId(rs.getInt("song_id"));
                s.setTitle(rs.getString("title"));
                s.setGenre(rs.getString("genre"));
                songs.add(s);
            }

            logger.info("Listening history records found: {}", songs.size());

        } catch (Exception e) {
            logger.error("Error fetching listening history for userId={}", userId, e);
        }

        return songs;
    }
    
    public List<Song> getRecentlyPlayed(int userId) {

        List<Song> list = new ArrayList<>();

        String sql =
            "SELECT * FROM (" +
            "  SELECT s.song_id, s.title, s.genre " +
            "  FROM listening_history h " +
            "  JOIN songs s ON h.song_id = s.song_id " +
            "  WHERE h.user_id = ? " +
            "  ORDER BY h.listened_at DESC" +
            ") WHERE ROWNUM = 1";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Song s = new Song();
                s.setSongId(rs.getInt("song_id"));
                s.setTitle(rs.getString("title"));
                s.setGenre(rs.getString("genre"));
                list.add(s);
            }

        } catch (Exception e) {
        	 logger.error("Error fetching recently played for userId={}", userId, e);

        
    }
		return list;

}
}

