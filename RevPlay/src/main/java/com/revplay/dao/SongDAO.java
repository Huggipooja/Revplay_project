package com.revplay.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revplay.model.Song;
import com.revplay.util.DBConnection;

public class SongDAO {

    private static final Logger logger = LogManager.getLogger(SongDAO.class);

    /* ===================== 1️⃣ View all songs ===================== */
    public List<Song> getAllSongs() {

        logger.info("Fetching all songs");

        List<Song> songs = new ArrayList<>();
        String sql = "SELECT song_id, title, genre, duration, play_count FROM songs";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Song song = new Song();
                song.setSongId(rs.getInt("song_id"));
                song.setTitle(rs.getString("title"));
                song.setGenre(rs.getString("genre"));
                song.setDuration(rs.getInt("duration"));
                song.setPlayCount(rs.getInt("play_count"));
                songs.add(song);
            }

            logger.info("Total songs fetched: {}", songs.size());

        } catch (Exception e) {
            logger.error("Error while fetching all songs", e);
        }

        return songs;
    }

    /* ===================== 2️⃣ Get song by ID ===================== */
    public Song getSongById(int songId) {

        logger.info("Fetching song by ID: {}", songId);

        String sql = "SELECT song_id, title, genre, play_count FROM songs WHERE song_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, songId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Song song = new Song();
                song.setSongId(rs.getInt("song_id"));
                song.setTitle(rs.getString("title"));
                song.setGenre(rs.getString("genre"));
                song.setPlayCount(rs.getInt("play_count"));
                return song;
            }

            logger.warn("Song not found for ID: {}", songId);

        } catch (Exception e) {
            logger.error("Error while fetching song by ID: " + songId, e);
        }

        return null;
    }

    /* ===================== 3️⃣ Increment play count ===================== */
    public void incrementPlayCount(int songId) {

        logger.info("Incrementing play count for song ID: {}", songId);

        String sql = "UPDATE songs SET play_count = play_count + 1 WHERE song_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, songId);
            ps.executeUpdate();

        } catch (Exception e) {
            logger.error("Error incrementing play count for song ID: " + songId, e);
        }
    }

    /* ===================== 4️⃣ Add new song ===================== */
    public boolean addSong(Song song) {

        logger.info("Adding new song: {}", song.getTitle());

        String sql =
            "INSERT INTO songs(song_id, artist_id, album_id, title, genre, duration, release_date, play_count) " +
            "VALUES (SONGS_SEQ.NEXTVAL, ?, NULL, ?, ?, ?, ?, 0)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, song.getArtistId());
            ps.setString(2, song.getTitle());
            ps.setString(3, song.getGenre());
            ps.setInt(4, song.getDuration());
            ps.setDate(5, song.getReleaseDate());

            int rows = ps.executeUpdate();

            logger.info("Song inserted, rows affected: {}", rows);
            return rows > 0;

        } catch (Exception e) {
            logger.error("Error adding song: " + song.getTitle(), e);
            return false;
        }
    }

    /* ===================== 5️⃣ Get songs by artist ===================== */
    public List<Song> getSongsByArtist(int artistId) {

        logger.info("Fetching songs for artist ID: {}", artistId);

        List<Song> songs = new ArrayList<>();
        String sql = "SELECT * FROM songs WHERE artist_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, artistId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Song s = new Song();
                s.setSongId(rs.getInt("song_id"));
                s.setTitle(rs.getString("title"));
                s.setGenre(rs.getString("genre"));
                s.setDuration(rs.getInt("duration"));
                s.setPlayCount(rs.getInt("play_count"));
                s.setReleaseDate(rs.getDate("release_date"));
                s.setArtistId(rs.getInt("artist_id"));
                songs.add(s);
            }

            logger.info("Songs found for artist {}: {}", artistId, songs.size());

        } catch (Exception e) {
            logger.error("Error fetching songs for artist ID: " + artistId, e);
        }

        return songs; // never null
    }

    /* ===================== 6️⃣ Search songs ===================== */
    public List<Song> searchSongs(String keyword) {

        logger.info("Searching songs with keyword: {}", keyword);

        List<Song> songs = new ArrayList<>();

        String sql =
            "SELECT * FROM songs " +
            "WHERE LOWER(title) LIKE LOWER(?) " +
            "OR LOWER(genre) LIKE LOWER(?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            String key = "%" + keyword + "%";
            ps.setString(1, key);
            ps.setString(2, key);

            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Song s = new Song();
                s.setSongId(rs.getInt("song_id"));
                s.setTitle(rs.getString("title"));
                s.setGenre(rs.getString("genre"));
                s.setDuration(rs.getInt("duration"));
                s.setPlayCount(rs.getInt("play_count"));
                songs.add(s);
            }

            logger.info("Search results count: {}", songs.size());

        } catch (Exception e) {
            logger.error("Error searching songs with keyword: " + keyword, e);
        }

        return songs;
    }

    /* ===================== 7️⃣ Add song to album ===================== */
    public boolean addSongToAlbum(Song song) {

        logger.info("Adding song '{}' to album ID {}", song.getTitle(), song.getAlbumId());

        String sql =
            "INSERT INTO songs(song_id, artist_id, album_id, title, genre, duration, release_date, play_count) " +
            "VALUES (SONGS_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?, 0)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, song.getArtistId());
            ps.setInt(2, song.getAlbumId());
            ps.setString(3, song.getTitle());
            ps.setString(4, song.getGenre());
            ps.setInt(5, song.getDuration());
            ps.setDate(6, song.getReleaseDate());

            int rows = ps.executeUpdate();
            logger.info("Song added to album, rows affected: {}", rows);

            return rows > 0;

        } catch (Exception e) {
            logger.error("Error adding song to album", e);
            return false;
        }
    }
    
    public List<Song> getSongsByGenre(String genre) {

        logger.info("Fetching songs by genre='{}'", genre);

        List<Song> list = new ArrayList<>();

        String sql =
            "SELECT song_id, title, genre " +
            "FROM songs WHERE LOWER(genre) = LOWER(?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, genre);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Song s = new Song();
                s.setSongId(rs.getInt("song_id"));
                s.setTitle(rs.getString("title"));
                s.setGenre(rs.getString("genre"));
                list.add(s);
            }

            logger.info("Total songs found for genre='{}': {}", genre, list.size());

        } catch (Exception e) {
            logger.error("Error fetching songs by genre='{}'", genre, e);
        }

        return list;
    }


    public List<Song> getSongsByArtist(String artistName) {

        logger.info("Fetching songs by artistName='{}'", artistName);

        List<Song> list = new ArrayList<>();

        String sql =
            "SELECT s.song_id, s.title, s.genre " +
            "FROM songs s " +
            "JOIN artists a ON s.artist_id = a.artist_id " +
            "WHERE LOWER(a.name) LIKE LOWER(?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + artistName + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Song s = new Song();
                s.setSongId(rs.getInt("song_id"));
                s.setTitle(rs.getString("title"));
                s.setGenre(rs.getString("genre"));
                list.add(s);
            }

            logger.info("Total songs found for artistName='{}': {}",
                    artistName, list.size());

        } catch (Exception e) {
            logger.error("Error fetching songs by artistName='{}'",
                    artistName, e);
        }

        return list;
    }

    public List<Song> getRecentlyPlayed(int userId) {

        logger.info("Fetching recently played songs for userId={}", userId);

        List<Song> list = new ArrayList<>();

        String sql =
            "SELECT * FROM (" +
            "  SELECT s.song_id, s.title, s.genre " +
            "  FROM listening_history h " +
            "  JOIN songs s ON h.song_id = s.song_id " +
            "  WHERE h.user_id = ? " +
            "  ORDER BY h.listened_at DESC" +
            ") WHERE ROWNUM <= 5";

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

            logger.info("Recently played songs count for userId={}: {}",
                    userId, list.size());

        } catch (Exception e) {
            logger.error("Error fetching recently played songs for userId={}",
                    userId, e);
        }

        return list;
    }

    public boolean deleteSongByArtist(int songId, int artistId) {

        logger.warn("Deleting songId={} for artistId={}", songId, artistId);

        String sql =
            "DELETE FROM songs WHERE song_id = ? AND artist_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, songId);
            ps.setInt(2, artistId);

            boolean deleted = ps.executeUpdate() > 0;

            logger.info("Song delete status for songId={}: {}",
                    songId, deleted);

            return deleted;

        } catch (Exception e) {
            logger.error("Error deleting songId={} for artistId={}",
                    songId, artistId, e);
            return false;
        }
    }


    
    public boolean updateSongByArtist(Song song) {

        logger.info("Updating songId={} for artistId={}",
                song.getSongId(), song.getArtistId());

        String sql =
            "UPDATE songs " +
            "SET title = ?, genre = ?, duration = ?, release_date = ? " +
            "WHERE song_id = ? AND artist_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, song.getTitle());
            ps.setString(2, song.getGenre());
            ps.setInt(3, song.getDuration());
            ps.setDate(4, song.getReleaseDate());
            ps.setInt(5, song.getSongId());
            ps.setInt(6, song.getArtistId());

            boolean updated = ps.executeUpdate() > 0;

            logger.info("Song update status for songId={}: {}",
                    song.getSongId(), updated);

            return updated;

        } catch (Exception e) {
            logger.error("Error updating songId={} for artistId={}",
                    song.getSongId(), song.getArtistId(), e);
            return false;
        }
    }

    
    public List<Song> getSongStatisticsByArtist(int artistId) {

        logger.info("Fetching song statistics for artistId={}", artistId);

        List<Song> songs = new ArrayList<>();

        String sql =
            "SELECT song_id, title, genre, play_count " +
            "FROM songs " +
            "WHERE artist_id = ? " +
            "ORDER BY play_count DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, artistId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Song s = new Song();
                s.setSongId(rs.getInt("song_id"));
                s.setTitle(rs.getString("title"));
                s.setGenre(rs.getString("genre"));
                s.setPlayCount(rs.getInt("play_count"));
                songs.add(s);
            }

            logger.info("Total songs found for statistics artistId={}: {}",
                    artistId, songs.size());

        } catch (Exception e) {
            logger.error("Error fetching song statistics for artistId={}",
                    artistId, e);
        }

        return songs;
    }

}
