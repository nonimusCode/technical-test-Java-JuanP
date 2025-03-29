package com.example.playlist_api.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.example.playlist_api.model.Playlist;
import com.example.playlist_api.model.Song;
import com.example.playlist_api.service.PlaylistService;
import com.example.playlist_api.mapper.PlaylistMapper;
import com.example.playlist_api.dto.PlaylistDTO;
import com.example.playlist_api.dto.SongDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = PlaylistController.class)
class PlaylistControllerTest {

        @Autowired
        private WebApplicationContext context;

        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockBean
        private PlaylistService playlistService;

        @MockBean
        private PlaylistMapper playlistMapper;

        private Playlist playlist;
        private Song song;
        private PlaylistDTO playlistDTO;
        private SongDTO songDTO;

        @BeforeEach
        void setUp() {
                // Configurar MockMvc con seguridad y CSRF deshabilitado
                mockMvc = MockMvcBuilders
                                .webAppContextSetup(context)
                                .apply(springSecurity())
                                .build();

                // Configurar entidades
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

                // Configurar DTOs
                songDTO = new SongDTO();
                songDTO.setTitulo("Canción de prueba");
                songDTO.setArtista("Artista de prueba");
                songDTO.setAlbum("Álbum de prueba");
                songDTO.setAnno(2023);
                songDTO.setGenero("Rock");

                playlistDTO = new PlaylistDTO();
                playlistDTO.setNombre("Lista de prueba");
                playlistDTO.setDescripcion("Descripción de prueba");
                playlistDTO.setCanciones(Arrays.asList(songDTO));

                // Configurar comportamiento del mapper
                when(playlistMapper.toEntity(any(PlaylistDTO.class))).thenReturn(playlist);
                when(playlistMapper.toDto(any(Playlist.class))).thenReturn(playlistDTO);
        }

        @Test
        @WithMockUser
        void createPlaylist_ValidData_Returns201() throws Exception {
                when(playlistService.createPlaylist(any(Playlist.class))).thenReturn(playlist);

                mockMvc.perform(post("/lists")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(playlistDTO)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.nombre").value("Lista de prueba"))
                                .andExpect(jsonPath("$.descripcion").value("Descripción de prueba"))
                                .andExpect(jsonPath("$.canciones[0].titulo").value("Canción de prueba"));

                verify(playlistService, times(1)).createPlaylist(any(Playlist.class));
        }

        @Test
        @WithMockUser
        void createPlaylist_NullName_Returns400() throws Exception {
                playlistDTO.setNombre(null);

                mockMvc.perform(post("/lists")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(playlistDTO)))
                                .andExpect(status().isBadRequest());

                verify(playlistService, never()).createPlaylist(any(Playlist.class));
        }

        @Test
        @WithMockUser
        void getAllPlaylists_ReturnsAllPlaylists() throws Exception {
                List<Playlist> playlists = Arrays.asList(playlist);
                when(playlistService.getAllPlaylists()).thenReturn(playlists);

                mockMvc.perform(get("/lists")
                                .with(csrf()))
                                .andExpect(status().isOk());

                verify(playlistService, times(1)).getAllPlaylists();
        }

        @Test
        @WithMockUser
        void getlistByName_ExistingName_Returns200() throws Exception {
                when(playlistService.getlistByName("Lista de prueba")).thenReturn(playlist);

                mockMvc.perform(get("/lists/Lista de prueba")
                                .with(csrf()))
                                .andExpect(status().isOk());

                verify(playlistService, times(1)).getlistByName("Lista de prueba");
        }

        @Test
        @WithMockUser
        void getlistByName_NonExistingName_Returns404() throws Exception {
                when(playlistService.getlistByName("Inexistente"))
                                .thenThrow(new RuntimeException("Playlist no encontrada"));

                mockMvc.perform(get("/lists/Inexistente")
                                .with(csrf()))
                                .andExpect(status().isNotFound());

                verify(playlistService, times(1)).getlistByName("Inexistente");
        }

        @Test
        @WithMockUser
        void deletePlaylist_ExistingName_Returns204() throws Exception {
                doNothing().when(playlistService).deletePlaylistByName("Lista de prueba");

                mockMvc.perform(delete("/lists/Lista de prueba")
                                .with(csrf()))
                                .andExpect(status().isNoContent());

                verify(playlistService, times(1)).deletePlaylistByName("Lista de prueba");
        }

        @Test
        @WithMockUser
        void deletePlaylist_NonExistingName_Returns404() throws Exception {
                doThrow(new RuntimeException("Playlist no encontrada")).when(playlistService)
                                .deletePlaylistByName("Inexistente");

                mockMvc.perform(delete("/lists/Inexistente")
                                .with(csrf()))
                                .andExpect(status().isNotFound());

                verify(playlistService, times(1)).deletePlaylistByName("Inexistente");
        }
}