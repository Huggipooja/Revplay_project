package com.revplay.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revplay.model.Playlist;
import com.revplay.model.Song;
import com.revplay.util.DBConnection;

public class PlaylistDAO {

    private static final Logger logger = LogManager.getLogger(PlaylistDAO.class);

    /* ================= CREATE PLAYLIST ================= */
    public void createPlaylist(Playlist p) {

        logger.info("Creating playlist for userId={}, name={}", p.getUserId(), p.getName());

        String sql =
            "INSERT INTO playlists(playlist_id, user_id, name, description, is_public, created_at) " +
            "VALUES (playlists_seq.NEXTVAL, ?, ?, ?, ?, SYSDATE)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, p.getUserId());
            ps.setString(2, p.getName());
            ps.setString(3, p.getDescription());
            ps.setString(4, p.getIsPublic());

            ps.executeUpdate();
            logger.info("Playlist created successfully");

        } catch (Exception e) {
            logger.error("Error creating playlist for userId={}", p.getUserId(), e);
        }
    }

    /* ================= VIEW USER PLAYLISTS ================= */
    public List<Playlist> getUserPlaylists(int userId) {

        logger.info("Fetching playlists for userId={}", userId);

        List<Playlist> list = new ArrayList<>();

        String sql = "SELECT * FROM playlists WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Playlist p = new Playlist();
                p.setPlaylistId(rs.getInt("playlist_id"));
                p.setUserId(rs.getInt("user_id"));
                p.setName(rs.getString("name"));
                p.setDescription(rs.getString("description"));
                p.setIsPublic(rs.getString("is_public"));
                p.setCreatedAt(rs.getDate("created_at"));
                list.add(p);
            }

            logger.info("Playlists found: {}", list.size());

        } catch (Exception e) {
            logger.error("Error fetching playlists for userId={}", userId, e);
        }

        return list;
    }

    /* ================= ADD SONG TO PLAYLIST ================= */
    public void addSongToPlaylist(int playlistId, int songId) {

        logger.info("Adding songId={} to playlistId={}", songId, playlistId);

        String sql =
            "INSERT INTO playlist_songs (playlist_id, song_id) VALUES (?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, playlistId);
            ps.setInt(2, songId);

            ps.executeUpdate();
            logger.info("Song added to playlist successfully");

        } catch (Exception e) {
            logger.error("Error adding songId={} to playlistId={}", songId, playlistId, e);
        }
    }

    /* ================= REMOVE SONG FROM PLAYLIST ================= */
    public boolean removeSongFromPlaylist(int playlistId, int songId) {

        logger.info("Removing songId={} from playlistId={}", songId, playlistId);

        String sql =
            "DELETE FROM playlist_songs WHERE playlist_id = ? AND song_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, playlistId);
            ps.setInt(2, songId);

            boolean removed = ps.executeUpdate() > 0;

            if (removed) {
                logger.info("Song removed from playlist successfully");
            } else {
                logger.warn("Song not found in playlist (playlistId={}, songId={})",
                        playlistId, songId);
            }

            return removed;

        } catch (Exception e) {
            logger.error("Error removing songId={} from playlistId={}", songId, playlistId, e);
            return false;
        }
    }
    
    public List<Song> getSongsInPlaylist(int playlistId) {

        logger.info("Fetching songs for playlistId={}", playlistId);

        List<Song> songs = new ArrayList<>();

        String sql =
            "SELECT s.song_id, s.title, s.genre " +
            "FROM songs s " +
            "JOIN playlist_songs ps ON s.song_id = ps.song_id " +
            "WHERE ps.playlist_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, playlistId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Song s = new Song();
                s.setSongId(rs.getInt("song_id"));
                s.setTitle(rs.getString("title"));
                s.setGenre(rs.getString("genre"));
                songs.add(s);
            }

            logger.info("Total songs found in playlistId={}: {}",
                    playlistId, songs.size());

        } catch (Exception e) {
            logger.error("Error fetching songs for playlistId={}",
                    playlistId, e);
        }

        return songs;
    }

    public boolean updatePlaylist(int playlistId, int userId,
            String name, String description,
            boolean isPublic) {

logger.info("Updating playlistId={} for userId={}",
playlistId, userId);

String sql =
"UPDATE playlists " +
"SET name = ?, description = ?, is_public = ? " +
"WHERE playlist_id = ? AND user_id = ?";

try (Connection conn = DBConnection.getConnection();
PreparedStatement ps = conn.prepareStatement(sql)) {

ps.setString(1, name);
ps.setString(2, description);
ps.setString(3, isPublic ? "Y" : "N");
ps.setInt(4, playlistId);
ps.setInt(5, userId);

boolean updated = ps.executeUpdate() > 0;

logger.info("Playlist update status for playlistId={}: {}",
playlistId, updated);

return updated;

} catch (Exception e) {
logger.error("Error updating playlistId={} for userId={}",
playlistId, userId, e);
return false;
}
}

    public boolean deletePlaylist(int playlistId, int userId) {

        logger.warn("Deleting playlistId={} for userId={}",
                playlistId, userId);

        String sql =
            "DELETE FROM playlists WHERE playlist_id = ? AND user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, playlistId);
            ps.setInt(2, userId);

            boolean deleted = ps.executeUpdate() > 0;

            logger.info("Playlist delete status for playlistId={}: {}",
                    playlistId, deleted);

            return deleted;

        } catch (Exception e) {
            logger.error("Error deleting playlistId={} for userId={}",
                    playlistId, userId, e);
            return false;
        }
    }


    public List<Playlist> getPublicPlaylists() {

        logger.info("Fetching all public playlists");

        List<Playlist> list = new ArrayList<>();

        String sql =
            "SELECT playlist_id, name, description, user_id " +
            "FROM playlists WHERE is_public = 'Y'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Playlist p = new Playlist();
                p.setPlaylistId(rs.getInt("playlist_id"));
                p.setName(rs.getString("name"));
                p.setDescription(rs.getString("description"));
                p.setUserId(rs.getInt("user_id"));
                list.add(p);
            }

            logger.info("Total public playlists found: {}", list.size());

        } catch (Exception e) {
            logger.error("Error fetching public playlists", e);
        }

        return list;
    }

    
    public List<Playlist> searchPlaylists(String keyword) {

        logger.info("Searching public playlists with keyword='{}'", keyword);

        List<Playlist> playlists = new ArrayList<>();

        String sql =
            "SELECT p.playlist_id, p.name, p.description, u.username " +
            "FROM playlists p " +
            "JOIN users u ON p.user_id = u.user_id " +
            "WHERE p.is_public = 'Y' " +
            "AND LOWER(p.name) LIKE LOWER(?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Playlist p = new Playlist();
                p.setPlaylistId(rs.getInt("playlist_id"));
                p.setName(rs.getString("name"));
                p.setDescription(rs.getString("description"));
                p.setOwnerName(rs.getString("username"));
                playlists.add(p);
            }

            logger.info("Search result count for keyword='{}': {}",
                    keyword, playlists.size());

        } catch (Exception e) {
            logger.error("Error searching playlists with keyword='{}'",
                    keyword, e);
        }

        return playlists;
    }


}

