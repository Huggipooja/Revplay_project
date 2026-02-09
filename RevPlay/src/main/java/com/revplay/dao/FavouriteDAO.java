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

public class FavouriteDAO {

    private static final Logger logger = LogManager.getLogger(FavouriteDAO.class);

    // ================= ADD TO FAVOURITES =================
    public void addToFavourites(int userId, int songId) {

        logger.info("Adding songId={} to favourites for userId={}", songId, userId);

        String sql = "INSERT INTO favourites (user_id, song_id) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, songId);
            ps.executeUpdate();

            logger.info("Song added to favourites successfully");

        } catch (Exception e) {
            logger.error("Error adding song to favourites (userId={}, songId={})",
                    userId, songId, e);
        }
    }

    // ================= GET FAVOURITE SONGS =================
    public List<Song> getFavouriteSongs(int userId) {

        logger.info("Fetching favourite songs for userId={}", userId);

        List<Song> songs = new ArrayList<>();

        String sql =
            "SELECT s.song_id, s.title, s.genre " +
            "FROM songs s JOIN favourites f ON s.song_id = f.song_id " +
            "WHERE f.user_id = ?";

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

            logger.info("Total favourite songs found: {}", songs.size());

        } catch (Exception e) {
            logger.error("Error fetching favourite songs for userId={}", userId, e);
        }

        return songs;
    }
    
    public List<String> getUsersAndFavouritedSongsByArtist(int artistId) {

        logger.info("Fetching users and favourited songs for artistId={}", artistId);

        List<String> result = new ArrayList<>();

        String sql =
            "SELECT u.username, s.title " +
            "FROM favourites f " +
            "JOIN songs s ON f.song_id = s.song_id " +
            "JOIN users u ON f.user_id = u.user_id " +
            "WHERE s.artist_id = ? " +
            "ORDER BY u.username";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, artistId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String row =
                    "User: " + rs.getString("username") +
                    " | Song: " + rs.getString("title");
                result.add(row);
            }

            logger.info("Total favourite entries found for artistId={}: {}",
                    artistId, result.size());

        } catch (Exception e) {
            logger.error("Error fetching favourite songs for artistId={}",
                    artistId, e);
        }

        return result;
    }

    public void deleteFavouritesBySongId(int songId) {

        logger.warn("Deleting favourites for songId={}", songId);

        String sql = "DELETE FROM favourites WHERE song_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, songId);
            int rows = ps.executeUpdate();

            logger.info("Deleted {} favourite records for songId={}",
                    rows, songId);

        } catch (Exception e) {
            logger.error("Error deleting favourites for songId={}",
                    songId, e);
        }
    }



}


