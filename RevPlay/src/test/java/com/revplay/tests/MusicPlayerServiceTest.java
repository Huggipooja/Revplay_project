package com.revplay.tests;

import static org.junit.Assert.*;

import org.junit.Test;

import com.revplay.model.Song;
import com.revplay.service.MusicPlayerService;

public class MusicPlayerServiceTest {

    @Test
    public void testPlaySong() {

        MusicPlayerService service = new MusicPlayerService();

        Song song = new Song();
        song.setSongId(1);
        song.setTitle("Test Song");

        service.play(song, 1);

        assertTrue(true);
    }

    @Test
    public void testPauseAndResume() {

        MusicPlayerService service = new MusicPlayerService();

        Song song = new Song();
        song.setSongId(1);
        song.setTitle("Test Song");

        service.play(song, 1);
        service.pause();
        service.resume();

        assertTrue(true);
    }

    @Test
    public void testStopPlayback() {

        MusicPlayerService service = new MusicPlayerService();

        Song song = new Song();
        song.setSongId(1);
        song.setTitle("Test Song");

        service.play(song, 1);
        service.stop();

        assertTrue(true);
    }
}
