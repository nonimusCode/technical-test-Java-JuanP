package com.example.playlist_api.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.example.playlist_api.model.Playlist;
import com.example.playlist_api.model.Song;
import com.example.playlist_api.service.PlaylistService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(PlaylistController.class)
class PlaylistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PlaylistService playlistService;

    private Playlist playlist;
    private Song song;

    @BeforeEach
    void setUp() {
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
    @WithMockUser
    void createPlaylist_ValidData_Returns201() throws Exception {
        when(playlistService.createPlaylist(any(Playlist.class))).thenReturn(playlist);

        mockMvc.perform(post("/lists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(playlist)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre").value("Lista de prueba"))
                .andExpect(jsonPath("$.descripcion").value("Descripción de prueba"))
                .andExpect(jsonPath("$.canciones[0].titulo").value("Canción de prueba"));

        verify(playlistService, times(1)).createPlaylist(any(Playlist.class));
    }

    @Test
    @WithMockUser
    void createPlaylist_NullName_Returns400() throws Exception {
        playlist.setName(null);

        mockMvc.perform(post("/lists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(playlist)))
                .andExpect(status().isBadRequest());

        verify(playlistService, never()).createPlaylist(any(Playlist.class));
    }

    @Test
    @WithMockUser
    void getAllPlaylists_ReturnsAllPlaylists() throws Exception {
        List<Playlist> playlists = Arrays.asList(playlist);
        when(playlistService.getAllPlaylists()).thenReturn(playlists);

        mockMvc.perform(get("/lists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Lista de prueba"))
                .andExpect(jsonPath("$[0].descripcion").value("Descripción de prueba"));

        verify(playlistService, times(1)).getAllPlaylists();
    }

    @Test
    @WithMockUser
    void getPlaylistByName_ExistingName_Returns200() throws Exception {
        when(playlistService.getPlaylistByName("Lista de prueba")).thenReturn(playlist);

        mockMvc.perform(get("/lists/Lista de prueba"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Lista de prueba"))
                .andExpect(jsonPath("$.descripcion").value("Descripción de prueba"));

        verify(playlistService, times(1)).getPlaylistByName("Lista de prueba");
    }

    @Test
    @WithMockUser
    void getPlaylistByName_NonExistingName_Returns404() throws Exception {
        when(playlistService.getPlaylistByName("Inexistente"))
                .thenThrow(new RuntimeException("Playlist no encontrada"));

        mockMvc.perform(get("/lists/Inexistente"))
                .andExpect(status().isNotFound());

        verify(playlistService, times(1)).getPlaylistByName("Inexistente");
    }

    @Test
    @WithMockUser
    void deletePlaylist_ExistingName_Returns204() throws Exception {
        doNothing().when(playlistService).deletePlaylistByName("Lista de prueba");

        mockMvc.perform(delete("/lists/Lista de prueba"))
                .andExpect(status().isNoContent());

        verify(playlistService, times(1)).deletePlaylistByName("Lista de prueba");
    }

    @Test
    @WithMockUser
    void deletePlaylist_NonExistingName_Returns404() throws Exception {
        doThrow(new RuntimeException("Playlist no encontrada")).when(playlistService)
                .deletePlaylistByName("Inexistente");

        mockMvc.perform(delete("/lists/Inexistente"))
                .andExpect(status().isNotFound());

        verify(playlistService, times(1)).deletePlaylistByName("Inexistente");
    }
}