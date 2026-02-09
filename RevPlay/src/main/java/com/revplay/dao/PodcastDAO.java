package com.revplay.dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import com.revplay.model.Podcast;
import com.revplay.util.DBConnection;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

	

public class PodcastDAO {

	    private static final Logger logger = LogManager.getLogger(PodcastDAO.class);

	    /* ================= CREATE PODCAST ================= */
	    public boolean createPodcast(Podcast p) {

	        logger.info("Creating podcast for artistId={}, title={}",
	                p.getArtistId(), p.getTitle());

	        String sql =
	            "INSERT INTO podcasts " +
	            "(podcast_id, title, host, genre, duration, release_date, artist_id) " +
	            "VALUES (PODCASTS_SEQ.NEXTVAL, ?, ?, ?, ?, ?, ?)";

	        try (Connection conn = DBConnection.getConnection();
	             PreparedStatement ps = conn.prepareStatement(sql)) {

	            ps.setString(1, p.getTitle());
	            ps.setString(2, p.getHost());
	            ps.setString(3, p.getGenre());
	            ps.setInt(4, p.getDuration());
	            ps.setDate(5, p.getReleaseDate());
	            ps.setInt(6, p.getArtistId());

	            boolean created = ps.executeUpdate() > 0;
	            logger.info("Podcast creation status for title='{}': {}",
	                    p.getTitle(), created);

	            return created;

	        } catch (Exception e) {
	            logger.error("Error creating podcast for artistId={}",
	                    p.getArtistId(), e);
	            return false;
	        }
	    }

	    /* ================= VIEW PODCASTS BY ARTIST ================= */
	    public List<Podcast> getPodcastsByArtist(int artistId) {

	        logger.info("Fetching podcasts for artistId={}", artistId);

	        List<Podcast> list = new ArrayList<>();

	        String sql =
	            "SELECT podcast_id, title, genre, duration " +
	            "FROM podcasts WHERE artist_id = ?";

	        try (Connection conn = DBConnection.getConnection();
	             PreparedStatement ps = conn.prepareStatement(sql)) {

	            ps.setInt(1, artistId);
	            ResultSet rs = ps.executeQuery();

	            while (rs.next()) {
	                Podcast p = new Podcast();
	                p.setPodcastId(rs.getInt("podcast_id"));
	                p.setTitle(rs.getString("title"));
	                p.setGenre(rs.getString("genre"));
	                p.setDuration(rs.getInt("duration"));
	                list.add(p);
	            }

	            logger.info("Total podcasts found for artistId={}: {}",
	                    artistId, list.size());

	        } catch (Exception e) {
	            logger.error("Error fetching podcasts for artistId={}",
	                    artistId, e);
	        }

	        return list;
	    }

	    /* ================= UPDATE PODCAST ================= */
	    public boolean updatePodcast(Podcast podcast) {

	        logger.info("Updating podcastId={} for artistId={}",
	                podcast.getPodcastId(), podcast.getArtistId());

	        String sql =
	            "UPDATE podcasts " +
	            "SET title = ?, genre = ?, duration = ?, release_date = ? " +
	            "WHERE podcast_id = ? AND artist_id = ?";

	        try (Connection conn = DBConnection.getConnection();
	             PreparedStatement ps = conn.prepareStatement(sql)) {

	            ps.setString(1, podcast.getTitle());
	            ps.setString(2, podcast.getGenre());
	            ps.setInt(3, podcast.getDuration());
	            ps.setDate(4, podcast.getReleaseDate());
	            ps.setInt(5, podcast.getPodcastId());
	            ps.setInt(6, podcast.getArtistId());

	            boolean updated = ps.executeUpdate() > 0;
	            logger.info("Podcast update status for podcastId={}: {}",
	                    podcast.getPodcastId(), updated);

	            return updated;

	        } catch (Exception e) {
	            logger.error("Error updating podcastId={}",
	                    podcast.getPodcastId(), e);
	            return false;
	        }
	    }

	    /* ================= DELETE PODCAST ================= */
	    public boolean deletePodcast(int podcastId, int artistId) {

	        logger.warn("Deleting podcastId={} for artistId={}",
	                podcastId, artistId);

	        String sql =
	            "DELETE FROM podcasts WHERE podcast_id = ? AND artist_id = ?";

	        try (Connection conn = DBConnection.getConnection();
	             PreparedStatement ps = conn.prepareStatement(sql)) {

	            ps.setInt(1, podcastId);
	            ps.setInt(2, artistId);

	            boolean deleted = ps.executeUpdate() > 0;
	            logger.info("Podcast delete status for podcastId={}: {}",
	                    podcastId, deleted);

	            return deleted;

	        } catch (Exception e) {
	            logger.error("Error deleting podcastId={}",
	                    podcastId, e);
	            return false;
	        }
	    }

	    /* ================= SEARCH PODCASTS ================= */
	    public List<Podcast> searchPodcasts(String keyword) {

	        logger.info("Searching podcasts with keyword='{}'", keyword);

	        List<Podcast> list = new ArrayList<>();

	        String sql =
	            "SELECT podcast_id, title, genre, duration " +
	            "FROM podcasts " +
	            "WHERE LOWER(title) LIKE LOWER(?) " +
	            "OR LOWER(genre) LIKE LOWER(?)";

	        try (Connection conn = DBConnection.getConnection();
	             PreparedStatement ps = conn.prepareStatement(sql)) {

	            String key = "%" + keyword + "%";
	            ps.setString(1, key);
	            ps.setString(2, key);

	            ResultSet rs = ps.executeQuery();

	            while (rs.next()) {
	                Podcast p = new Podcast();
	                p.setPodcastId(rs.getInt("podcast_id"));
	                p.setTitle(rs.getString("title"));
	                p.setGenre(rs.getString("genre"));
	                p.setDuration(rs.getInt("duration"));
	                list.add(p);
	            }

	            logger.info("Search result count for keyword='{}': {}",
	                    keyword, list.size());

	        } catch (Exception e) {
	            logger.error("Error searching podcasts with keyword='{}'",
	                    keyword, e);
	        }

	        return list;
	    }

	    /* ================= VIEW ALL PODCASTS ================= */
	    public List<Podcast> getAllPodcasts() {

	        logger.info("Fetching all podcasts");

	        List<Podcast> list = new ArrayList<>();

	        String sql =
	            "SELECT podcast_id, title, genre, duration " +
	            "FROM podcasts ORDER BY release_date DESC";

	        try (Connection conn = DBConnection.getConnection();
	             PreparedStatement ps = conn.prepareStatement(sql);
	             ResultSet rs = ps.executeQuery()) {

	            while (rs.next()) {
	                Podcast p = new Podcast();
	                p.setPodcastId(rs.getInt("podcast_id"));
	                p.setTitle(rs.getString("title"));
	                p.setGenre(rs.getString("genre"));
	                p.setDuration(rs.getInt("duration"));
	                list.add(p);
	            }

	            logger.info("Total podcasts fetched: {}", list.size());

	        } catch (Exception e) {
	            logger.error("Error fetching all podcasts", e);
	        }

	        return list;
	    }

	    /* ================= GET PODCAST BY ID ================= */
	    public Podcast getPodcastById(int podcastId) {

	        logger.info("Fetching podcast by podcastId={}", podcastId);

	        String sql =
	            "SELECT podcast_id, title, genre, duration " +
	            "FROM podcasts WHERE podcast_id = ?";

	        try (Connection conn = DBConnection.getConnection();
	             PreparedStatement ps = conn.prepareStatement(sql)) {

	            ps.setInt(1, podcastId);
	            ResultSet rs = ps.executeQuery();

	            if (rs.next()) {
	                Podcast p = new Podcast();
	                p.setPodcastId(rs.getInt("podcast_id"));
	                p.setTitle(rs.getString("title"));
	                p.setGenre(rs.getString("genre"));
	                p.setDuration(rs.getInt("duration"));

	                logger.info("Podcast found for podcastId={}", podcastId);
	                return p;
	            }

	            logger.warn("No podcast found for podcastId={}", podcastId);

	        } catch (Exception e) {
	            logger.error("Error fetching podcast by podcastId={}",
	                    podcastId, e);
	        }

	        return null;
	    }
	
}
