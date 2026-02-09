

    package com.revplay.ui;

    import java.sql.Date;
    import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.revplay.dao.AlbumDAO;
import com.revplay.dao.ArtistDAO;
import com.revplay.dao.FavouriteDAO;
import com.revplay.dao.PodcastDAO;
import com.revplay.dao.SongDAO;
import com.revplay.model.Album;
import com.revplay.model.Artist;
import com.revplay.model.Podcast;
import com.revplay.model.Song;
    import com.revplay.model.User;

    public class ArtistDashboard {

        private User user;
        private Scanner sc = new Scanner(System.in);

        public ArtistDashboard(User user) {
            this.user = user;
        }

        public void start() {

            while (true) {

                System.out.println("\n====== ARTIST DASHBOARD ======");
                System.out.println("1. View Artist Profile");
                System.out.println("2. Update Artist Profile");
                System.out.println("3. Upload Song");
                System.out.println("4. View My Songs");
                System.out.println("5. Update Song");
                System.out.println("6. Delete Song");
                System.out.println("7. create album");
                System.out.println("8. view my albums");
                System.out.println("9. update album");
                System.out.println("10. delete album");
                System.out.println("11. Create Podcast");
                System.out.println("12. View My Podcasts");
                System.out.println("13. Update Podcast");
                System.out.println("14. Delete Podcast");
                System.out.println("15. view users who favourited my songs");
                System.out.println("16. View play count and statistics");

                System.out.println("17. Logout");

                System.out.print("Enter choice: ");
                int choice = sc.nextInt();
                sc.nextLine(); // clear buffer

                switch (choice) {

                    case 1:
                        viewArtistProfile();
                        break;

                    case 2:
                        updateArtistProfile();
                        break;

                    case 3:
                        uploadSong();
                        break;

                    case 4:
                        viewMySongs();
                        break;
                        
                    case 5:
                    	updateSong();
                    	break;
                        
                    case 6:
                        deleteSong();
                        break;
                        
                    case 7:
                        createAlbum();
                        break;
                        
                    case 8:
                    	viewMyAlbums();
                    	break;
                    
                    case 9:
                    	updateAlbum();
                    	break;
                    	
                    case 10:
                    	deleteAlbum();
                    	break;
                    	
                    case 11:
                        createPodcast();
                        break;

                    case 12:
                        viewMyPodcasts();
                        break;

                    case 13:
                        updatePodcast();
                        break;

                    case 14:
                        deletePodcast();
                        break;
                        
                    case 15:
                    	viewFavouriteUsers();
                    	break;
                    	
                    case 16:
                        viewSongStatistics();
                        break;
                    	

                    case 17:
                        System.out.println("Logged out 👋");
                        return;

                    default:
                        System.out.println("❌ Invalid choice");
                }
            }
        }

        /* =========================
           1. View Artist Profile
           ========================= */
        private void viewArtistProfile() {

            ArtistDAO dao = new ArtistDAO();
            Artist artist = dao.getArtistByUserId(user.getUserId());

            if (artist == null) {
                System.out.println("❌ Artist profile not found");
                return;
            }

            System.out.println("\n🎤 ARTIST PROFILE");
            System.out.println("Bio   : " + artist.getBio());
            System.out.println("Genre : " + artist.getGenre());
            System.out.println("Links : " + artist.getSocialLinks());
        }

        /* =========================
           2. Update Artist Profile
           ========================= */
        private void updateArtistProfile() {


            System.out.print("Enter new bio: ");
            String bio = sc.nextLine();

            System.out.print("Enter new genre: ");
            String genre = sc.nextLine();

            System.out.print("Enter new social links: ");
            String links = sc.nextLine();

            Artist artist = new Artist();
            artist.setUserId(user.getUserId());
            artist.setBio(bio);
            artist.setGenre(genre);
            artist.setSocialLinks(links);

            ArtistDAO dao = new ArtistDAO();
            boolean updated = dao.updateArtistProfile(artist);

            if (updated) {
                System.out.println("✅ Artist profile updated successfully!");
            } else {
                System.out.println("❌ Update failed!");
            }
        }


        /* =========================
           3. Upload Song
           ========================= */
        private void uploadSong() {

            sc.nextLine();

            System.out.print("Enter song title: ");
            String title = sc.nextLine();

            System.out.print("Enter genre: ");
            String genre = sc.nextLine();

            System.out.print("Enter duration (seconds): ");
            int duration = sc.nextInt();
            sc.nextLine();

            System.out.print("Enter release date (YYYY-MM-DD): ");
            String dateStr = sc.nextLine();

            // 🔥 GET ARTIST FIRST
            ArtistDAO artistDAO = new ArtistDAO();
            Artist artist = artistDAO.getArtistByUserId(user.getUserId());

            if (artist == null) {
                System.out.println("❌ Artist profile not found. Cannot upload song.");
                return;
            }

            Song song = new Song();
            song.setTitle(title);
            song.setGenre(genre);
            song.setDuration(duration);
            song.setReleaseDate(Date.valueOf(dateStr));

            // ✅ THIS IS THE FIX
            song.setArtistId(artist.getArtistId());

            SongDAO songDAO = new SongDAO();
            boolean inserted = songDAO.addSong(song);

            if (inserted) {
                System.out.println("🎵 Song uploaded successfully!");
            } else {
                System.out.println("❌ Song upload failed!");
            }
        }


        /* =========================
           4. View My Songs
           ========================= */
        private void viewMySongs() {

            ArtistDAO artistDAO = new ArtistDAO();
            Artist artist = artistDAO.getArtistByUserId(user.getUserId());

            if (artist == null) {
                System.out.println("❌ Artist profile not found.");
                return;
            }

            SongDAO songDAO = new SongDAO();

            // 🔥 USE artistId-specific method
            List<Song> songs = songDAO.getSongsByArtist(artist.getArtistId());

            System.out.println("\n🎶 MY SONGS");

            if (songs.isEmpty()) {
                System.out.println("No songs uploaded yet.");
                return;
            }

            for (Song s : songs) {
                System.out.println(
                    s.getSongId() + ". " +
                    s.getTitle() +
                    " | Plays: " + s.getPlayCount()
                );
            }
        }

        
        
        private void createAlbum() {

            System.out.print("Enter album title: ");
            String title = sc.nextLine();

            System.out.print("Enter release date (YYYY-MM-DD): ");
            Date releaseDate = Date.valueOf(sc.nextLine());

            Artist artist = new ArtistDAO().getArtistByUserId(user.getUserId());
            if (artist == null) {
                System.out.println("❌ Artist profile not found. Create profile first.");
                return;
            }

            Album album = new Album();
            album.setArtistId(artist.getArtistId());
            album.setTitle(title);
            album.setReleaseDate(releaseDate);

            AlbumDAO albumDAO = new AlbumDAO();
            int albumId = albumDAO.createAlbumAndReturnId(album);

            if (albumId == -1) {
                System.out.println("❌ Album creation failed");
                return;
            }

            System.out.print("How many songs to add to this album? ");
            int count = sc.nextInt();
            sc.nextLine();

            if (count <= 0) {
                System.out.println("⚠ Album created without songs.");
                return;
            }


            SongDAO songDAO = new SongDAO();

            for (int i = 1; i <= count; i++) {

                System.out.println("\nAdding song " + i);

                Song song = new Song();
                song.setArtistId(artist.getArtistId());
                song.setAlbumId(albumId);

                System.out.print("Title: ");
                song.setTitle(sc.nextLine());

                System.out.print("Genre: ");
                song.setGenre(sc.nextLine());

                System.out.print("Duration (seconds): ");
                song.setDuration(sc.nextInt());
                sc.nextLine();

                System.out.print("Release date (YYYY-MM-DD): ");
                song.setReleaseDate(Date.valueOf(sc.nextLine()));

                songDAO.addSongToAlbum(song);
            }

            System.out.println("🎶 Album created with songs successfully!");
        }

        
        private void viewMyAlbums() {

            Artist artist = new ArtistDAO().getArtistByUserId(user.getUserId());
            if (artist == null) {
                System.out.println("❌ Artist profile not found");
                return;
            }

            AlbumDAO albumDAO = new AlbumDAO();
            Map<Album, List<Song>> albums = albumDAO.getAlbumsWithSongsByArtist(artist.getArtistId());

            if (albums.isEmpty()) {
                System.out.println("No albums created yet.");
                return;
            }

            System.out.println("\n🎵 MY ALBUMS");

            for (Map.Entry<Album, List<Song>> entry : albums.entrySet()) {

                Album album = entry.getKey();
                List<Song> songs = entry.getValue();

                System.out.println("\n📀 Album: " + album.getTitle() +
                        " (" + album.getReleaseDate() + ")");

                if (songs.isEmpty()) {
                    System.out.println("   No songs in this album.");
                } else {
                    for (Song s : songs) {
                        System.out.println("   " + s.getSongId() + ". "
                                + s.getTitle() + " | "
                                + s.getGenre() + " | "
                                + s.getDuration() + "s");
                    }
                }
            }
        }

        

        private void updateAlbum() {

            System.out.print("Enter Album ID to update: ");
            int albumId = sc.nextInt();
            sc.nextLine();

            System.out.print("Enter new title: ");
            String title = sc.nextLine();

            System.out.print("Enter new release date (YYYY-MM-DD): ");
            String dateStr = sc.nextLine();

            Artist artist = new ArtistDAO().getArtistByUserId(user.getUserId());

            Album album = new Album();
            album.setAlbumId(albumId);
            album.setArtistId(artist.getArtistId());
            album.setTitle(title);
            album.setReleaseDate(Date.valueOf(dateStr));

            boolean updated = new AlbumDAO().updateAlbum(album);

            if (updated)
                System.out.println("✅ Album updated successfully!");
            else
                System.out.println("❌ Update failed (check album ID)");
        }

        private void deleteAlbum() {

            System.out.print("Enter Album ID to delete: ");
            int albumId = sc.nextInt();

            Artist artist = new ArtistDAO().getArtistByUserId(user.getUserId());

            boolean deleted = new AlbumDAO().deleteAlbum(albumId, artist.getArtistId());

            if (deleted)
                System.out.println("🗑 Album deleted successfully!");
            else
                System.out.println("❌ Delete failed (check album ID)");
        }
        
        private void createPodcast() {

            Podcast p = new Podcast();

            System.out.print("Title: ");
            p.setTitle(sc.nextLine());

            System.out.print("Host: ");
            p.setHost(sc.nextLine());

            System.out.print("Genre: ");
            p.setGenre(sc.nextLine());

            System.out.print("Duration (seconds): ");
            p.setDuration(sc.nextInt());
            sc.nextLine();

            System.out.print("Release date (YYYY-MM-DD): ");
            p.setReleaseDate(Date.valueOf(sc.nextLine()));

            Artist artist = new ArtistDAO().getArtistByUserId(user.getUserId());
            p.setArtistId(artist.getArtistId());

            boolean created = new PodcastDAO().createPodcast(p);

            System.out.println(created ? "✅ Podcast created" : "❌ Failed");
        }

        
        private void viewMyPodcasts() {

            Artist artist = new ArtistDAO().getArtistByUserId(user.getUserId());

            List<Podcast> list =
                new PodcastDAO().getPodcastsByArtist(artist.getArtistId());

            if (list.isEmpty()) {
                System.out.println("No podcasts found.");
                return;
            }

            for (Podcast p : list) {
                System.out.println(
                    p.getPodcastId() + ". " +
                    p.getTitle() + " | " +
                    p.getGenre() + " | " +
                    p.getDuration() + "s"
                );
            }
        }
    

        private void updatePodcast() {

            PodcastDAO dao = new PodcastDAO();
            Artist artist = new ArtistDAO().getArtistByUserId(user.getUserId());

            List<Podcast> podcasts = dao.getPodcastsByArtist(artist.getArtistId());

            if (podcasts.isEmpty()) {
                System.out.println("❌ No podcasts found.");
                return;
            }

            System.out.println("\n🎙 YOUR PODCASTS");
            System.out.println("ID\tTitle\t\tGenre");

            for (Podcast p : podcasts) {
                System.out.println(
                    p.getPodcastId() + "\t" +
                    p.getTitle() + "\t\t" +
                    p.getGenre()
                );
            }

            System.out.print("\nEnter Podcast ID to update: ");
            int podcastId = sc.nextInt();
            sc.nextLine(); // clear buffer

            Podcast selected = null;
            for (Podcast p : podcasts) {
                if (p.getPodcastId() == podcastId) {
                    selected = p;
                    break;
                }
            }

            if (selected == null) {
                System.out.println("❌ Invalid Podcast ID");
                return;
            }

            // ✅ PARTIAL UPDATE INPUTS
            System.out.print("New title (" + selected.getTitle() + "): ");
            String title = sc.nextLine();
            if (title.isBlank()) title = selected.getTitle();

            System.out.print("New genre (" + selected.getGenre() + "): ");
            String genre = sc.nextLine();
            if (genre.isBlank()) genre = selected.getGenre();

            System.out.print("New duration (" + selected.getDuration() + "): ");
            String durationInput = sc.nextLine();
            int duration = durationInput.isBlank()
                    ? selected.getDuration()
                    : Integer.parseInt(durationInput);

            System.out.print("New release date (" + selected.getReleaseDate() + ") [YYYY-MM-DD]: ");
            String dateInput = sc.nextLine();
            Date releaseDate = dateInput.isBlank()
                    ? selected.getReleaseDate()
                    : Date.valueOf(dateInput);

            Podcast p = new Podcast();
            p.setPodcastId(podcastId);
            p.setArtistId(artist.getArtistId());
            p.setTitle(title);
            p.setGenre(genre);
            p.setDuration(duration);
            p.setReleaseDate(releaseDate);

            boolean updated = dao.updatePodcast(p);

            if (updated)
                System.out.println("✅ Podcast updated successfully!");
            else
                System.out.println("❌ Update failed!");
        }

        private void deletePodcast() {

            PodcastDAO dao = new PodcastDAO();
            Artist artist = new ArtistDAO().getArtistByUserId(user.getUserId());

            List<Podcast> podcasts = dao.getPodcastsByArtist(artist.getArtistId());

            if (podcasts.isEmpty()) {
                System.out.println("❌ No podcasts found.");
                return;
            }

            System.out.println("\n🗑 YOUR PODCASTS");
            System.out.println("ID\tTitle\t\tGenre");

            for (Podcast p : podcasts) {
                System.out.println(
                    p.getPodcastId() + "\t" +
                    p.getTitle() + "\t\t" +
                    p.getGenre()
                );
            }

            System.out.print("\nEnter Podcast ID to delete: ");
            int podcastId = sc.nextInt();

            boolean deleted = dao.deletePodcast(podcastId, artist.getArtistId());

            if (deleted)
                System.out.println("🗑 Podcast deleted successfully!");
            else
                System.out.println("❌ Delete failed!");
        }



       
        private void deleteSong() {

            ArtistDAO artistDAO = new ArtistDAO();
            Artist artist = artistDAO.getArtistByUserId(user.getUserId());

            if (artist == null) {
                System.out.println("❌ Artist profile not found.");
                return;
            }

            SongDAO songDAO = new SongDAO();

            // ✅ use artistId-based method
            List<Song> songs = songDAO.getSongsByArtist(artist.getArtistId());

            if (songs.isEmpty()) {
                System.out.println("❌ No songs to delete.");
                return;
            }

            System.out.println("\n🎵 YOUR SONGS");
            System.out.println("ID\tTitle\t\tGenre");

            for (Song s : songs) {
                System.out.println(
                    s.getSongId() + "\t" +
                    s.getTitle() + "\t\t" +
                    s.getGenre()
                );
            }

            System.out.print("\nEnter Song ID to delete: ");
            int songId = sc.nextInt();

            // 🔥 IMPORTANT FIX STARTS HERE
            FavouriteDAO favDAO = new FavouriteDAO();

            // 1️⃣ delete favourites of this song first
            favDAO.deleteFavouritesBySongId(songId);

            // 2️⃣ now delete the song
            boolean deleted =
                songDAO.deleteSongByArtist(songId, artist.getArtistId());

            if (deleted) {
                System.out.println("🗑 Song deleted successfully.");
            } else {
                System.out.println("❌ Cannot delete song (invalid ID or not owned by you).");
            }
        }

        private void viewFavouriteUsers() {

            Artist artist = new ArtistDAO().getArtistByUserId(user.getUserId());

            if (artist == null) {
                System.out.println("❌ Artist profile not found.");
                return;
            }

            FavouriteDAO favDAO = new FavouriteDAO();
            List<String> data =
                favDAO.getUsersAndFavouritedSongsByArtist(artist.getArtistId());

            System.out.println("\n❤️ USERS WHO FAVOURITED YOUR SONGS");

            if (data.isEmpty()) {
                System.out.println("No users have favourited your songs yet.");
                return;
            }

            for (String row : data) {
                System.out.println(row);
            }
        }
        
        private void viewSongStatistics() {

            Artist artist = new ArtistDAO().getArtistByUserId(user.getUserId());

            if (artist == null) {
                System.out.println("❌ Artist profile not found.");
                return;
            }

            SongDAO songDAO = new SongDAO();
            List<Song> songs = songDAO.getSongStatisticsByArtist(artist.getArtistId());

            if (songs.isEmpty()) {
                System.out.println("❌ No songs uploaded yet.");
                return;
            }

            System.out.println("\n📊 SONG STATISTICS");
            System.out.println("ID\tTitle\t\tGenre\tPlays");
            System.out.println("----------------------------------------");

            for (Song s : songs) {
                System.out.println(
                    s.getSongId() + "\t" +
                    s.getTitle() + "\t\t" +
                    s.getGenre() + "\t" +
                    s.getPlayCount()
                );
            }
        }
        
        private void updateSong() {

            Artist artist = new ArtistDAO().getArtistByUserId(user.getUserId());

            if (artist == null) {
                System.out.println("❌ Artist profile not found.");
                return;
            }

            SongDAO songDAO = new SongDAO();
            List<Song> songs = songDAO.getSongsByArtist(artist.getArtistId());

            if (songs.isEmpty()) {
                System.out.println("❌ No songs uploaded yet.");
                return;
            }

            System.out.println("\n🎵 YOUR SONGS");
            System.out.println("ID\tTitle\t\tGenre\tDuration");

            for (Song s : songs) {
                System.out.println(
                    s.getSongId() + "\t" +
                    s.getTitle() + "\t\t" +
                    s.getGenre() + "\t" +
                    s.getDuration()
                );
            }

            System.out.print("\nEnter Song ID to update: ");
            int songId = sc.nextInt();
            sc.nextLine();

            Song selected = null;
            for (Song s : songs) {
                if (s.getSongId() == songId) {
                    selected = s;
                    break;
                }
            }

            if (selected == null) {
                System.out.println("❌ Invalid Song ID");
                return;
            }

            // Optional updates
            System.out.print("New title (" + selected.getTitle() + "): ");
            String title = sc.nextLine();
            if (title.isBlank()) title = selected.getTitle();

            System.out.print("New genre (" + selected.getGenre() + "): ");
            String genre = sc.nextLine();
            if (genre.isBlank()) genre = selected.getGenre();

            System.out.print("New duration (" + selected.getDuration() + "): ");
            String durationInput = sc.nextLine();
            int duration = durationInput.isBlank()
                    ? selected.getDuration()
                    : Integer.parseInt(durationInput);

            System.out.print("New release date (YYYY-MM-DD) (" +
                    selected.getReleaseDate() + "): ");
            String dateInput = sc.nextLine();
            Date releaseDate = dateInput.isBlank()
                    ? selected.getReleaseDate()
                    : Date.valueOf(dateInput);

            selected.setTitle(title);
            selected.setGenre(genre);
            selected.setDuration(duration);
            selected.setReleaseDate(releaseDate);
            selected.setArtistId(artist.getArtistId());

            boolean updated = songDAO.updateSongByArtist(selected);

            if (updated) {
                System.out.println("✅ Song updated successfully!");
            } else {
                System.out.println("❌ Update failed.");
            }
        }



     
    }
    
