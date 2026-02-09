package com.revplay.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revplay.model.Artist;
import com.revplay.util.DBConnection;

public class ArtistDAO {

    private static final Logger logger = LogManager.getLogger(ArtistDAO.class);

    // ================= CREATE ARTIST =================
    public void createArtist(Artist artist) {

        logger.info("Creating artist profile for userId={}", artist.getUserId());

        String sql =
            "INSERT INTO artists (artist_id, user_id, bio, genre, social_links) " +
            "VALUES (ARTISTS_SEQ.NEXTVAL, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, artist.getUserId());
            ps.setString(2, artist.getBio());
            ps.setString(3, artist.getGenre());
            ps.setString(4, artist.getSocialLinks());

            ps.executeUpdate();
            logger.info("Artist profile created successfully");

        } catch (Exception e) {
            logger.error("Error creating artist profile", e);
        }
    }

    // ================= GET ARTIST BY USER ID =================
    public Artist getArtistByUserId(int userId) {

        logger.info("Fetching artist by userId={}", userId);

        String sql = "SELECT * FROM artists WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Artist a = new Artist();
                a.setArtistId(rs.getInt("artist_id"));
                a.setUserId(rs.getInt("user_id"));
                a.setBio(rs.getString("bio"));
                a.setGenre(rs.getString("genre"));
                a.setSocialLinks(rs.getString("social_links"));
                return a;
            }

        } catch (Exception e) {
            logger.error("Error fetching artist for userId={}", userId, e);
        }
        return null;
    }

    // ================= CHECK IF ARTIST =================
    public boolean isArtist(int userId) {

        logger.info("Checking if userId={} is an artist", userId);

        String sql = "SELECT COUNT(*) FROM artists WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                boolean exists = rs.getInt(1) > 0;
                logger.info("Is artist: {}", exists);
                return exists;
            }

        } catch (Exception e) {
            logger.error("Error checking artist existence", e);
        }

        return false;
    }

    // ================= UPDATE ARTIST PROFILE =================
    public boolean updateArtistProfile(Artist artist) {

        logger.info("Updating artist profile for userId={}", artist.getUserId());

        String sql =
            "UPDATE artists " +
            "SET bio = ?, genre = ?, social_links = ? " +
            "WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, artist.getBio());
            ps.setString(2, artist.getGenre());
            ps.setString(3, artist.getSocialLinks());
            ps.setInt(4, artist.getUserId());

            boolean updated = ps.executeUpdate() > 0;
            logger.info("Artist profile update status: {}", updated);
            return updated;

        } catch (Exception e) {
            logger.error("Error updating artist profile", e);
            return false;
        }
    }

    // ================= SEARCH ARTISTS =================
    public List<Artist> searchArtists(String keyword) {

        logger.info("Searching artists with keyword={}", keyword);

        List<Artist> artists = new ArrayList<>();

        String sql =
            "SELECT a.artist_id, a.user_id, a.bio, a.genre, a.social_links, u.username " +
            "FROM artists a " +
            "JOIN users u ON a.user_id = u.user_id " +
            "WHERE LOWER(u.username) LIKE ? " +
            "OR LOWER(a.genre) LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword.toLowerCase() + "%");
            ps.setString(2, "%" + keyword.toLowerCase() + "%");

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Artist a = new Artist();
                a.setArtistId(rs.getInt("artist_id"));
                a.setUserId(rs.getInt("user_id"));
                a.setBio(rs.getString("bio"));
                a.setGenre(rs.getString("genre"));
                a.setSocialLinks(rs.getString("social_links"));

                // artist name from users table
                a.setArtistName(rs.getString("username"));

                artists.add(a);
            }

            logger.info("Total artists found: {}", artists.size());

        } catch (Exception e) {
            logger.error("Error searching artists", e);
        }

        return artists;
    }



    

}
