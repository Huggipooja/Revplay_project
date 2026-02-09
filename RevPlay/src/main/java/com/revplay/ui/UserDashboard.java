package com.revplay.ui;

import java.util.List;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Scanner;

import com.revplay.dao.SongDAO;
import com.revplay.dao.AlbumDAO;
import com.revplay.dao.ArtistDAO;
import com.revplay.dao.FavouriteDAO;
import com.revplay.dao.ListeningHistoryDAO;
import com.revplay.dao.PlaylistDAO;
import com.revplay.dao.PodcastDAO;
import com.revplay.model.Album;
import com.revplay.model.Artist;
import com.revplay.model.Playlist;
import com.revplay.model.Podcast;
import com.revplay.model.Song;
import com.revplay.model.User;
import com.revplay.service.MusicPlayerService;
import com.revplay.util.DBConnection;


public class UserDashboard {
	MusicPlayerService player = new MusicPlayerService();

    private User user;
    private Scanner sc = new Scanner(System.in);
    

    public UserDashboard(User user) {
        this.user = user;
    }
    public void start() {

        while (true) {

            System.out.println("\n====== USER DASHBOARD ======");
            System.out.println("1. View All Songs");
            System.out.println("2. Play Song");
            System.out.println("3. Search Songs");
            System.out.println("4. view all podcasts");
            System.out.println("5. search podcast");
            System.out.println("6. play podcast");
            System.out.println("7. Search All");
            System.out.println("8. Add Song to Favourites");
            System.out.println("9. View Favourite Songs");
            System.out.println("10. Create Playlist");
            System.out.println("11. View My Playlists");
            System.out.println("12. Add Song to Playlist");
            System.out.println("13. Remove Song from Playlist");
            System.out.println("14. Update Playlist");
            System.out.println("15. Delete Playlist");
            System.out.println("16. View Public Playlists");
            System.out.println("17. View Listening History");
            System.out.println("18. Recently Played");
            System.out.println("19. Logout");

            System.out.print("Enter choice: ");
            int choice = sc.nextInt();   // ✅ DECLARED HERE

            switch (choice) {

                case 1:
                    viewAllSongs();
                    break;

                case 2:
                    playSong();
                    break;

                case 3:
                    searchSongs();
                    break;
                    
                case 4:
                    viewAllPodcasts();   // 📄 list podcasts
                    break;
                    
                case 5:
                    searchPodcasts();    // 🔍 keyword search
                    break;
                    
                case 6:
                    playPodcast();       // ▶ play with controls
                    break;

                case 7:
                    searchAll();
                    break;

                case 8:
                    addToFavourites();
                    break;

                case 9:
                    viewFavouriteSongs();
                    break;

                case 10:
                    createPlaylist();
                    break;

                case 11:
                    viewMyPlaylists();
                    break;

                case 12:
                    addSongToPlaylist();
                    break;

                case 13:
                    removeSongFromPlaylist();
                    break;

                case 14:
                    updatePlaylist();
                    break;

                case 15:
                    deletePlaylist();
                    break;

                case 16:
                    viewPublicPlaylists();
                    break;

                case 17:
                    viewListeningHistory();
                    break;

                case 18:
                    viewRecentlyPlayed();
                    break;

                case 19:
                    System.out.println("Logged out 👋");
                    return;

                default:
                    System.out.println("❌ Invalid choice");
            }
        }
    }


    private void viewAllSongs() {
        SongDAO songDAO = new SongDAO();
        List<Song> songs = songDAO.getAllSongs();

        System.out.println("\n🎵 ALL SONGS 🎵");
        if (songs.isEmpty()) {
            System.out.println("No songs available.");
            return;
        }

        for (Song s : songs) {
            System.out.println(
                s.getSongId() + ". " +
                s.getTitle() + " | " +
                s.getGenre() + " | Plays: " +
                s.getPlayCount()
            );
        }
    }
    
    private void playSong() {

        SongDAO songDAO = new SongDAO();
        ListeningHistoryDAO historyDAO = new ListeningHistoryDAO();

        // 1️⃣ Show all songs
        List<Song> songs = songDAO.getAllSongs();

        if (songs.isEmpty()) {
            System.out.println("❌ No songs available");
            return;
        }

        System.out.println("\n🎵 AVAILABLE SONGS");
        System.out.println("ID\tTitle\t\tGenre");

        for (Song s : songs) {
            System.out.println(
                s.getSongId() + "\t" +
                s.getTitle() + "\t\t" +
                s.getGenre()
            );
        }

        // 2️⃣ Select song
        System.out.print("\nEnter Song ID to play: ");
        int songId = sc.nextInt();

        Song song = songDAO.getSongById(songId);

        if (song == null) {
            System.out.println("❌ Invalid Song ID");
            return;
        }
        System.out.println("DEBUG userId = " + user.getUserId());
        System.out.println("DEBUG songId = " + songId);


        songDAO.incrementPlayCount(songId);
     
        historyDAO.addHistory(user.getUserId(), songId);

        // 3️⃣ Play
        System.out.println("\n▶ Now Playing: " +
                song.getTitle() + " (" + song.getGenre() + ")");

        // 5️⃣ Mini player
        boolean playing = true;
        while (playing) {
            System.out.println("\n🎧 Player Controls");
            System.out.println("1. Pause");
            System.out.println("2. Resume");
            System.out.println("3. Stop");
            System.out.print("Enter choice: ");

            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("⏸ Song paused");
                    break;

                case 2:
                    System.out.println("▶ Song resumed");
                    break;

                case 3:
                    System.out.println("⏹ Playback stopped");
                    playing = false;
                    break;

                default:
                    System.out.println("❌ Invalid option");
            }
        }
    }


        
    

    private void addToFavourites() {

        SongDAO songDAO = new SongDAO();
        List<Song> songs = songDAO.getAllSongs();

        if (songs.isEmpty()) {
            System.out.println("❌ No songs available.");
            return;
        }

        System.out.println("\n❤️ ADD TO FAVOURITES ❤️");
        System.out.println("---------------------------------------------");
        System.out.printf("%-5s %-20s %-10s%n", "ID", "Title", "Genre");
        System.out.println("---------------------------------------------");

        for (Song s : songs) {
            System.out.printf(
                "%-5d %-20s %-10s%n",
                s.getSongId(),
                s.getTitle(),
                s.getGenre()
            );
        }

        System.out.println("---------------------------------------------");
        System.out.print("Enter Song ID to add to favourites: ");
        int songId = sc.nextInt();

        FavouriteDAO favDAO = new FavouriteDAO();
        favDAO.addToFavourites(user.getUserId(), songId);
        System.out.println("song added to favourites");
    }


    private void viewFavouriteSongs() {
        FavouriteDAO favDAO = new FavouriteDAO();
        List<Song> songs = favDAO.getFavouriteSongs(user.getUserId());

        System.out.println("\n❤️ YOUR FAVOURITE SONGS ❤️");
        if (songs.isEmpty()) {
            System.out.println("No favoUrite songs yet.");
            return;
        }

        for (Song s : songs) {
            System.out.println(
                s.getSongId() + ". " +
                s.getTitle() + " | " +
                s.getGenre()
            );
        }
    }
    
    
    private void createPlaylist() {

        sc.nextLine();

        System.out.print("Playlist name: ");
        String name = sc.nextLine();

        System.out.print("Description: ");
        String desc = sc.nextLine();

        System.out.print("Public? (Y/N): ");
        String pub = sc.nextLine().toUpperCase();

        Playlist p = new Playlist();
        p.setUserId(user.getUserId());
        p.setName(name);
        p.setDescription(desc);
        p.setIsPublic(pub);

        new PlaylistDAO().createPlaylist(p);
    }

    private void viewMyPlaylists() {

        List<Playlist> list =
            new PlaylistDAO().getUserPlaylists(user.getUserId());

        if (list.isEmpty()) {
            System.out.println("No playlists found.");
            return;
        }

        System.out.println("\n🎶 MY PLAYLISTS");
        for (Playlist p : list) {
            System.out.println(
                p.getPlaylistId() + ". " +
                p.getName() +
                " (" + p.getIsPublic() + ")"
            );
        }
    }
    private void addSongToPlaylist() {

        PlaylistDAO playlistDAO = new PlaylistDAO();
        SongDAO songDAO = new SongDAO();

        // 1️⃣ Show user's playlists
        List<Playlist> playlists = playlistDAO.getUserPlaylists(user.getUserId());

        if (playlists.isEmpty()) {
            System.out.println("❌ You have no playlists. Create one first.");
            return;
        }

        System.out.println("\n🎵 YOUR PLAYLISTS");
        System.out.println("----------------------------------");
        System.out.printf("%-5s %-20s %-10s%n", "ID", "Name", "Public");
        System.out.println("----------------------------------");

        for (Playlist p : playlists) {
            System.out.printf(
                "%-5d %-20s %-10s%n",
                p.getPlaylistId(),
                p.getName(),
                p.getIsPublic()
            );
        }

        System.out.println("----------------------------------");
        System.out.print("Enter Playlist ID: ");
        int playlistId = sc.nextInt();

        // Find selected playlist name (for success message)
        Playlist selectedPlaylist = playlists.stream()
            .filter(p -> p.getPlaylistId() == playlistId)
            .findFirst()
            .orElse(null);

        if (selectedPlaylist == null) {
            System.out.println("❌ Invalid Playlist ID");
            return;
        }

        // 2️⃣ Show all songs
        List<Song> songs = songDAO.getAllSongs();

        if (songs.isEmpty()) {
            System.out.println("❌ No songs available.");
            return;
        }

        System.out.println("\n🎶 AVAILABLE SONGS");
        System.out.println("----------------------------------");
        System.out.printf("%-5s %-20s %-10s%n", "ID", "Title", "Genre");
        System.out.println("----------------------------------");

        for (Song s : songs) {
            System.out.printf(
                "%-5d %-20s %-10s%n",
                s.getSongId(),
                s.getTitle(),
                s.getGenre()
            );
        }

        System.out.println("----------------------------------");
        System.out.print("Enter Song ID to add: ");
        int songId = sc.nextInt();

        // 3️⃣ Add song to playlist
        playlistDAO.addSongToPlaylist(playlistId, songId);

        System.out.println(
            "✅ Song added to playlist: " + selectedPlaylist.getName()
        );
    }


    
    private void searchSongs() {

        sc.nextLine();
        System.out.print("Enter keyword (song/genre): ");
        String keyword = sc.nextLine();

        SongDAO dao = new SongDAO();
        List<Song> songs = dao.searchSongs(keyword);

        if (songs.isEmpty()) {
            System.out.println("❌ No songs found");
            return;
        }

        System.out.println("\n🎵 SEARCH RESULTS");
        for (Song s : songs) {
            System.out.println(
                s.getSongId() + ". " +
                s.getTitle() + " | " +
                s.getGenre()
            );
        }
    }
 
    
    private void viewListeningHistory() {

        ListeningHistoryDAO dao = new ListeningHistoryDAO();
        List<Song> songs = dao.getUserHistory(user.getUserId());

        System.out.println("\n🕒 LISTENING HISTORY");

        if (songs.isEmpty()) {
            System.out.println("No listening history yet.");
            return;
        }

        for (Song s : songs) {
            System.out.println(
                s.getSongId() + ". " +
                s.getTitle() + " | " +
                s.getGenre()
            );
        }
    }

    
    private void removeSongFromPlaylist() {

        PlaylistDAO playlistDAO = new PlaylistDAO();

        // 1️⃣ Show playlists
        List<Playlist> playlists = playlistDAO.getUserPlaylists(user.getUserId());

        if (playlists.isEmpty()) {
            System.out.println("❌ You have no playlists");
            return;
        }

        System.out.println("\n🎵 YOUR PLAYLISTS");
        System.out.println("ID\tName\tPublic");
        for (Playlist p : playlists) {
            System.out.println(
                p.getPlaylistId() + "\t" +
                p.getName() + "\t" +
                p.getIsPublic()
            );
        }

        // 2️⃣ Select playlist
        System.out.print("\nEnter Playlist ID: ");
        int playlistId = sc.nextInt();

        // 3️⃣ Show songs in playlist
        List<Song> songs = playlistDAO.getSongsInPlaylist(playlistId);

        if (songs.isEmpty()) {
            System.out.println("❌ No songs in this playlist");
            return;
        }

        System.out.println("\n🎶 SONGS IN PLAYLIST");
        System.out.println("ID\tTitle\tGenre");
        for (Song s : songs) {
            System.out.println(
                s.getSongId() + "\t" +
                s.getTitle() + "\t" +
                s.getGenre()
            );
        }

        // 4️⃣ Select song
        System.out.print("\nEnter Song ID to remove: ");
        int songId = sc.nextInt();

        // 5️⃣ Remove
        boolean removed = playlistDAO.removeSongFromPlaylist(playlistId, songId);

        if (removed) {
            System.out.println("✅ Song removed from playlist");
        } else {
            System.out.println("❌ Failed to remove song");
        }
    }

    
    private void showPlayerControls(MusicPlayerService player) {

        while (true) {
            System.out.println("\n--- Player Controls ---");
            System.out.println("1. Pause");
            System.out.println("2. Resume");
            System.out.println("3. Repeat");
            System.out.println("4. Stop");

            System.out.print("Enter choice: ");
            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    player.pause();
                    break;

                case 2:
                    player.resume();
                    break;

                case 3:
                    player.repeat();
                    break;

                case 4:
                    player.stop();
                    System.out.println("⏹ Playback stopped");
                    return; // exit to dashboard

                default:
                    System.out.println("❌ Invalid choice");
            }
        }
    }
    
    private void updatePlaylist() {

        PlaylistDAO dao = new PlaylistDAO();

        // 1️⃣ Get user's playlists
        List<Playlist> playlists = dao.getUserPlaylists(user.getUserId());

        if (playlists.isEmpty()) {
            System.out.println("❌ You have no playlists to update.");
            return;
        }

        // 2️⃣ Show playlists
        System.out.println("\n🎶 YOUR PLAYLISTS");
        System.out.println("ID\tName\t\tPublic");

        for (Playlist p : playlists) {
            System.out.println(
                p.getPlaylistId() + "\t" +
                p.getName() + "\t\t" +
                p.getIsPublic()
            );
        }

        // 3️⃣ Select playlist
        System.out.print("\nEnter Playlist ID to update: ");
        int playlistId = sc.nextInt();
        sc.nextLine();

        Playlist selected = null;
        for (Playlist p : playlists) {
            if (p.getPlaylistId() == playlistId) {
                selected = p;
                break;
            }
        }

        if (selected == null) {
            System.out.println("❌ Invalid Playlist ID");
            return;
        }

        // 4️⃣ Update values
        System.out.print("New name (" + selected.getName() + "): ");
        String newName = sc.nextLine();
        if (newName.isBlank()) newName = selected.getName();

        System.out.print("New description (" + selected.getDescription() + "): ");
        String newDesc = sc.nextLine();
        if (newDesc.isBlank()) newDesc = selected.getDescription();

        System.out.print("Make public? (Y/N): ");
        String pub = sc.nextLine();
        String isPublic = pub.isBlank() ? selected.getIsPublic()
                                       : pub.equalsIgnoreCase("Y") ? "Y" : "N";

        // 5️⃣ Save update
        boolean updated = dao.updatePlaylist(
            selected.getPlaylistId(),
            user.getUserId(),
            newName,
            newDesc,
            isPublic.equals("Y")
        );

        if (updated) {
            System.out.println("✅ Playlist updated successfully");
        } else {
            System.out.println("❌ Update failed");
        }
    }


    private void deletePlaylist() {

        PlaylistDAO dao = new PlaylistDAO();

        // 1️⃣ Get user's playlists
        List<Playlist> playlists = dao.getUserPlaylists(user.getUserId());

        if (playlists.isEmpty()) {
            System.out.println("❌ You have no playlists to delete.");
            return;
        }

        // 2️⃣ Show playlists
        System.out.println("\n🗑 YOUR PLAYLISTS");
        System.out.println("ID\tName\t\tPublic");

        for (Playlist p : playlists) {
            System.out.println(
                p.getPlaylistId() + "\t" +
                p.getName() + "\t\t" +
                p.getIsPublic()
            );
        }

        // 3️⃣ Select playlist
        System.out.print("\nEnter Playlist ID to delete: ");
        int playlistId = sc.nextInt();
        sc.nextLine();

        Playlist selected = null;
        for (Playlist p : playlists) {
            if (p.getPlaylistId() == playlistId) {
                selected = p;
                break;
            }
        }

        if (selected == null) {
            System.out.println("❌ Invalid Playlist ID");
            return;
        }

        // 4️⃣ Confirm delete
        System.out.print(
            "Are you sure you want to delete '" +
            selected.getName() + "'? (yes/no): "
        );
        String confirm = sc.nextLine();

        if (!confirm.equalsIgnoreCase("yes")) {
            System.out.println("❎ Delete cancelled");
            return;
        }

        // 5️⃣ Delete
        boolean deleted = dao.deletePlaylist(
            selected.getPlaylistId(),
            user.getUserId()
        );

        if (deleted) {
            System.out.println("🗑 Playlist deleted successfully");
        } else {
            System.out.println("❌ Delete failed");
        }
    }


    private void viewPublicPlaylists() {

        PlaylistDAO dao = new PlaylistDAO();
        List<Playlist> playlists = dao.getPublicPlaylists();

        System.out.println("\n🌍 PUBLIC PLAYLISTS");

        if (playlists.isEmpty()) {
            System.out.println("No public playlists available.");
            return;
        }

        for (Playlist p : playlists) {
            System.out.println(
                p.getPlaylistId() + ". " +
                p.getName() + " | " +
                p.getDescription() +
                " (User: " + p.getUserId() + ")"
            );
        }
    }



    private void searchSongsByGenre() {

        SongDAO songDAO = new SongDAO();

        sc.nextLine(); // clear buffer
        System.out.print("Enter genre: ");
        String genre = sc.nextLine();

        List<Song> songs = songDAO.getSongsByGenre(genre);

        System.out.println("\n🎵 SONGS IN GENRE: " + genre);

        if (songs.isEmpty()) {
            System.out.println("No songs found.");
            return;
        }

        for (Song s : songs) {
            System.out.println(
                s.getSongId() + ". " +
                s.getTitle() + " | " +
                s.getGenre()
            );
        }
    }
    private void searchSongsByArtist() {

        SongDAO songDAO = new SongDAO();

        sc.nextLine();
        System.out.print("Enter artist name: ");
        String artist = sc.nextLine();

        List<Song> songs = songDAO.getSongsByArtist(artist);

        System.out.println("\n🎤 SONGS BY ARTIST: " + artist);

        if (songs.isEmpty()) {
            System.out.println("No songs found.");
            return;
        }

        for (Song s : songs) {
            System.out.println(
                s.getSongId() + ". " +
                s.getTitle() + " | " +
                s.getGenre()
            );
        }
    }

    private void viewRecentlyPlayed() {

    	ListeningHistoryDAO dao = new ListeningHistoryDAO();
    	List<Song> songs = dao.getRecentlyPlayed(user.getUserId());

        System.out.println("\n🕒 RECENTLY PLAYED");

        if (songs.isEmpty()) {
            System.out.println("No recently played songs.");
            return;
        }

        for (Song s : songs) {
            System.out.println(
                s.getSongId() + ". " +
                s.getTitle() + " | " +
                s.getGenre()
            );
        }
    }
    
    private void searchAll() {

        sc.nextLine(); // clear buffer
        System.out.print("Enter keyword: ");
        String keyword = sc.nextLine();

        SongDAO songDAO = new SongDAO();
        ArtistDAO artistDAO = new ArtistDAO();
        AlbumDAO albumDAO = new AlbumDAO();
        PlaylistDAO playlistDAO = new PlaylistDAO();

        List<Song> songs = songDAO.searchSongs(keyword);
        List<Artist> artists = artistDAO.searchArtists(keyword);
        List<Album> albums = albumDAO.searchAlbums(keyword);
        List<Playlist> playlists = playlistDAO.searchPlaylists(keyword);

        System.out.println("\n🔍 SEARCH RESULTS FOR: " + keyword);

        // ===== SONGS =====
        System.out.println("\n🎵 SONGS");
        if (songs.isEmpty()) {
            System.out.println("No songs found.");
        } else {
            for (Song s : songs) {
                System.out.println(
                    s.getSongId() + ". " +
                    s.getTitle() + " | " +
                    s.getGenre()
                );
            }
        }

        // ===== ARTISTS =====
        System.out.println("\n🎤 ARTISTS");
        if (artists.isEmpty()) {
            System.out.println("No artists found.");
        } else {
            for (Artist a : artists) {
                System.out.println(
                    a.getArtistId() + ". " +
                    a.getArtistName() + " | " +
                    a.getGenre()
                );
            }
        }

        // ===== ALBUMS =====
        System.out.println("\n💿 ALBUMS");
        if (albums.isEmpty()) {
            System.out.println("No albums found.");
        } else {
            for (Album a : albums) {
                System.out.println(
                    a.getAlbumId() + ". " +
                    a.getTitle() +
                    " (" + a.getReleaseDate() + ")"
                );
            }
        }

        // ===== PLAYLISTS =====
        System.out.println("\n🎧 PLAYLISTS");
        if (playlists.isEmpty()) {
            System.out.println("No playlists found.");
        } else {
            for (Playlist p : playlists) {
                System.out.println(
                    p.getPlaylistId() + ". " +
                    p.getName() +
                    " | " + p.getDescription()
                );
            }
        }
    }


    private void searchPodcasts() {

        sc.nextLine(); // clear buffer
        System.out.print("Enter podcast keyword (title/genre): ");
        String keyword = sc.nextLine();

        PodcastDAO dao = new PodcastDAO();
        List<Podcast> podcasts = dao.searchPodcasts(keyword);

        System.out.println("\n🎙 PODCAST SEARCH RESULTS");

        if (podcasts.isEmpty()) {
            System.out.println("No podcasts found.");
            return;
        }

        System.out.println("ID\tTitle\t\tGenre\tDuration");

        for (Podcast p : podcasts) {
            System.out.println(
                p.getPodcastId() + "\t" +
                p.getTitle() + "\t\t" +
                p.getGenre() + "\t" +
                p.getDuration()
            );
        }
    }

    
    private void viewAllPodcasts() {

        PodcastDAO dao = new PodcastDAO();
        List<Podcast> podcasts = dao.getAllPodcasts();

        System.out.println("\n🎙 ALL PODCASTS");

        if (podcasts.isEmpty()) {
            System.out.println("No podcasts available.");
            return;
        }

        System.out.printf("%-5s %-20s %-10s %-10s%n",
                "ID", "Title", "Genre", "Duration");


        for (Podcast p : podcasts) {
            System.out.println(
                p.getPodcastId() + "\t" +
                p.getTitle() + "\t\t" +
                p.getGenre() + "\t" +
                p.getDuration()
            );
        }
    }

    private void playPodcast() {

        PodcastDAO dao = new PodcastDAO();
        List<Podcast> podcasts = dao.getAllPodcasts();

        if (podcasts.isEmpty()) {
            System.out.println("❌ No podcasts available.");
            return;
        }

        System.out.println("\n🎙 AVAILABLE PODCASTS");
        System.out.println("ID\tTitle\t\tGenre");

        for (Podcast p : podcasts) {
            System.out.println(
                p.getPodcastId() + "\t" +
                p.getTitle() + "\t\t" +
                p.getGenre()
            );
        }

        System.out.print("\nEnter Podcast ID to play: ");
        int podcastId = sc.nextInt();

        Podcast podcast = dao.getPodcastById(podcastId);

        if (podcast == null) {
            System.out.println("❌ Invalid Podcast ID");
            return;
        }

        System.out.println(
            "\n▶ Now Playing Podcast: " +
            podcast.getTitle() + " (" + podcast.getGenre() + ")"
        );

        // 🎧 Player controls (same logic as songs)
        boolean playing = true;

        while (playing) {
            System.out.println("\n🎧 Podcast Controls");
            System.out.println("1. Pause");
            System.out.println("2. Resume");
            System.out.println("3. Stop");
            System.out.print("Enter choice: ");

            int choice = sc.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("⏸ Podcast paused");
                    break;

                case 2:
                    System.out.println("▶ Podcast resumed");
                    break;

                case 3:
                    System.out.println("⏹ Podcast stopped");
                    playing = false;
                    break;

                default:
                    System.out.println("❌ Invalid option");
            }
        }
    }


    
    
}


    
