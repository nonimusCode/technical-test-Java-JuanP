package com.example.playlist_api.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.example.playlist_api.model.Playlist;
import com.example.playlist_api.model.Song;
import com.example.playlist_api.repository.PlaylistRepository;

class PlaylistServiceTest {

    @Mock
    private PlaylistRepository playlistRepository;

    @InjectMocks
    private PlaylistService playlistService;

    private Playlist playlist;
    private Song song;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        song = new Song();
        song.setTitle("Canción de prueba");
        song.setArtist("Artista de prueba");
        song.setAlbum("Álbum de prueba");
        song.setYear(2023);
        song.setGenre("Rock");

        playlist = new Playlist();
        playlist.setName("Lista de prueba");
        playlist.setDescription("Descripción de prueba");
        playlist.setSongs(Arrays.asList(song));
    }

    @Test
    void createPlaylist_ValidPlaylist_ReturnsPlaylist() {
        when(playlistRepository.save(any(Playlist.class))).thenReturn(playlist);

        Playlist result = playlistService.createPlaylist(playlist);

        assertNotNull(result);
        assertEquals("Lista de prueba", result.getName());
        verify(playlistRepository, times(1)).save(playlist);
    }

    @Test
    void createPlaylist_NullName_ThrowsException() {
        playlist.setName(null);

        assertThrows(IllegalArgumentException.class, () -> {
            playlistService.createPlaylist(playlist);
        });

        verify(playlistRepository, never()).save(any());
    }

    @Test
    void getAllPlaylists_ReturnsAllPlaylists() {
        List<Playlist> playlists = Arrays.asList(playlist);
        when(playlistRepository.findAll()).thenReturn(playlists);

        List<Playlist> result = playlistService.getAllPlaylists();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Lista de prueba", result.get(0).getName());
        verify(playlistRepository, times(1)).findAll();
    }

    @Test
    void getPlaylistByName_ExistingName_ReturnsPlaylist() {
        when(playlistRepository.findByName("Lista de prueba")).thenReturn(Optional.of(playlist));

        Playlist result = playlistService.getPlaylistByName("Lista de prueba");

        assertNotNull(result);
        assertEquals("Lista de prueba", result.getName());
        verify(playlistRepository, times(1)).findByName("Lista de prueba");
    }

    @Test
    void getPlaylistByName_NonExistingName_ThrowsException() {
        when(playlistRepository.findByName("Inexistente")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            playlistService.getPlaylistByName("Inexistente");
        });

        verify(playlistRepository, times(1)).findByName("Inexistente");
    }

    @Test
    void deletePlaylistByName_ExistingName_DeletesPlaylist() {
        when(playlistRepository.findByName("Lista de prueba")).thenReturn(Optional.of(playlist));
        doNothing().when(playlistRepository).delete(playlist);

        playlistService.deletePlaylistByName("Lista de prueba");

        verify(playlistRepository, times(1)).findByName("Lista de prueba");
        verify(playlistRepository, times(1)).delete(playlist);
    }

    @Test
    void deletePlaylistByName_NonExistingName_ThrowsException() {
        when(playlistRepository.findByName("Inexistente")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            playlistService.deletePlaylistByName("Inexistente");
        });

        verify(playlistRepository, times(1)).findByName("Inexistente");
        verify(playlistRepository, never()).delete(any());
    }
}