package com.revplay.dao;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.revplay.model.Album;
import com.revplay.model.Song;
import com.revplay.util.DBConnection;

public class AlbumDAO {

    private static final Logger logger = LogManager.getLogger(AlbumDAO.class);

    // ================= CREATE ALBUM =================
    public int createAlbumAndReturnId(Album album) {

        logger.info("Creating album for artistId={}, title={}",
                album.getArtistId(), album.getTitle());

        String sql =
            "INSERT INTO albums (album_id, artist_id, title, release_date) " +
            "VALUES (ALBUMS_SEQ.NEXTVAL, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             CallableStatement cs = conn.prepareCall(sql)) {

            cs.setInt(1, album.getArtistId());
            cs.setString(2, album.getTitle());
            cs.setDate(3, album.getReleaseDate());

            cs.execute();

            try (PreparedStatement ps =
                     conn.prepareStatement("SELECT ALBUMS_SEQ.CURRVAL FROM dual");
                 ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    int albumId = rs.getInt(1);
                    logger.info("Album created successfully with albumId={}", albumId);
                    return albumId;
                }
            }

        } catch (Exception e) {
            logger.error("Error creating album for artistId={}",
                    album.getArtistId(), e);
        }

        return -1;
    }

    // ================= VIEW MY ALBUMS =================
    public List<Album> getAlbumsByArtist(int artistId) {

        logger.info("Fetching albums for artistId={}", artistId);

        List<Album> albums = new ArrayList<>();
        String sql = "SELECT * FROM albums WHERE artist_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, artistId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Album a = new Album();
                a.setAlbumId(rs.getInt("album_id"));
                a.setArtistId(rs.getInt("artist_id"));
                a.setTitle(rs.getString("title"));
                a.setReleaseDate(rs.getDate("release_date"));
                albums.add(a);
            }

            logger.info("Total albums found for artistId={}: {}",
                    artistId, albums.size());

        } catch (Exception e) {
            logger.error("Error fetching albums for artistId={}", artistId, e);
        }
        return albums;
    }

    // ================= UPDATE ALBUM =================
    public boolean updateAlbum(Album album) {

        logger.info("Updating albumId={} for artistId={}",
                album.getAlbumId(), album.getArtistId());

        String sql =
            "UPDATE albums " +
            "SET title = ?, release_date = ? " +
            "WHERE album_id = ? AND artist_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, album.getTitle());
            ps.setDate(2, album.getReleaseDate());
            ps.setInt(3, album.getAlbumId());
            ps.setInt(4, album.getArtistId());

            boolean updated = ps.executeUpdate() > 0;
            logger.info("Album update status for albumId={}: {}",
                    album.getAlbumId(), updated);
            return updated;

        } catch (Exception e) {
            logger.error("Error updating albumId={}",
                    album.getAlbumId(), e);
        }
        return false;
    }

    // ================= DELETE ALBUM =================
    public boolean deleteAlbum(int albumId, int artistId) {

        logger.warn("Deleting albumId={} for artistId={}", albumId, artistId);

        String sql = "DELETE FROM albums WHERE album_id = ? AND artist_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, albumId);
            ps.setInt(2, artistId);

            boolean deleted = ps.executeUpdate() > 0;
            logger.info("Album delete status for albumId={}: {}", albumId, deleted);
            return deleted;

        } catch (Exception e) {
            logger.error("Error deleting albumId={}", albumId, e);
        }
        return false;
    }

    // ================= VIEW ALBUMS WITH SONGS =================
    public Map<Album, List<Song>> getAlbumsWithSongsByArtist(int artistId) {

        logger.info("Fetching albums with songs for artistId={}", artistId);

        String sql =
            "SELECT " +
            "a.album_id, a.title AS album_title, a.release_date, " +
            "s.song_id, s.title AS song_title, s.genre, s.duration " +
            "FROM albums a " +
            "LEFT JOIN songs s ON a.album_id = s.album_id " +
            "WHERE a.artist_id = ? " +
            "ORDER BY a.album_id";

        Map<Album, List<Song>> result = new LinkedHashMap<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, artistId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {

                int albumId = rs.getInt("album_id");

                Album album = result.keySet()
                        .stream()
                        .filter(a -> a.getAlbumId() == albumId)
                        .findFirst()
                        .orElse(null);

                if (album == null) {
                    album = new Album();
                    album.setAlbumId(albumId);
                    album.setTitle(rs.getString("album_title"));
                    album.setReleaseDate(rs.getDate("release_date"));
                    result.put(album, new ArrayList<>());
                }

                if (rs.getInt("song_id") != 0) {
                    Song song = new Song();
                    song.setSongId(rs.getInt("song_id"));
                    song.setTitle(rs.getString("song_title"));
                    song.setGenre(rs.getString("genre"));
                    song.setDuration(rs.getInt("duration"));

                    result.get(album).add(song);
                }
            }

            logger.info("Fetched {} albums with songs for artistId={}",
                    result.size(), artistId);

        } catch (Exception e) {
            logger.error("Error fetching albums with songs for artistId={}",
                    artistId, e);
        }

        return result;
    }

    // ================= SEARCH ALBUMS =================
    public List<Album> searchAlbums(String keyword) {

        logger.info("Searching albums with keyword='{}'", keyword);

        List<Album> albums = new ArrayList<>();

        String sql =
            "SELECT al.album_id, al.title, al.release_date, u.username " +
            "FROM albums al " +
            "JOIN artists a ON al.artist_id = a.artist_id " +
            "JOIN users u ON a.user_id = u.user_id " +
            "WHERE LOWER(al.title) LIKE LOWER(?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Album a = new Album();
                a.setAlbumId(rs.getInt("album_id"));
                a.setTitle(rs.getString("title"));
                a.setReleaseDate(rs.getDate("release_date"));
                a.setArtistName(rs.getString("username"));
                albums.add(a);
            }

            logger.info("Search result count for keyword='{}': {}",
                    keyword, albums.size());

        } catch (Exception e) {
            logger.error("Error searching albums with keyword='{}'",
                    keyword, e);
        }

        return albums;
    }
}
