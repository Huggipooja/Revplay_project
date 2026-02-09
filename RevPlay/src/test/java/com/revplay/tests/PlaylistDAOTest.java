package com.revplay.tests;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.revplay.dao.PlaylistDAO;
import com.revplay.model.Playlist;
import com.revplay.model.Song;

public class PlaylistDAOTest {

    @Test
    public void testCreateAndFetchPlaylist() {

        PlaylistDAO playlistDAO = new PlaylistDAO();

        Playlist playlist = new Playlist();
        playlist.setUserId(1);
        playlist.setName("JUnit Playlist");
        playlist.setDescription("JUnit test playlist");
        playlist.setIsPublic("Y");

        playlistDAO.createPlaylist(playlist);

        List<Playlist> playlists = playlistDAO.getUserPlaylists(1);

        assertNotNull(playlists);
        assertTrue(playlists.size() > 0);
    }

    @Test
    public void testAddSongToPlaylistAndFetchSongs() {

        PlaylistDAO playlistDAO = new PlaylistDAO();

        List<Playlist> playlists = playlistDAO.getUserPlaylists(1);
        int playlistId = playlists.get(0).getPlaylistId();

        playlistDAO.removeSongFromPlaylist(playlistId, 1); // cleanup
        playlistDAO.addSongToPlaylist(playlistId, 1);

        List<Song> songs = playlistDAO.getSongsInPlaylist(playlistId);

        assertNotNull(songs);
        assertTrue(songs.size() > 0);
    }


    @Test
    public void testUpdatePlaylist() {

        PlaylistDAO playlistDAO = new PlaylistDAO();

        List<Playlist> playlists = playlistDAO.getUserPlaylists(1);
        assertFalse(playlists.isEmpty());

        int playlistId = playlists.get(0).getPlaylistId();

        boolean updated = playlistDAO.updatePlaylist(
                playlistId,
                1,
                "Updated Playlist",
                "Updated Description",
                true
        );

        assertTrue(updated);
    }
}
