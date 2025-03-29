package com.example.playlist_api.controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import com.example.playlist_api.model.Playlist;
import com.example.playlist_api.service.PlaylistService;
import com.example.playlist_api.dto.PlaylistDTO;
import com.example.playlist_api.mapper.PlaylistMapper;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/lists")
public class PlaylistController {
    private final PlaylistService playlistService;
    private final PlaylistMapper playlistMapper;

    public PlaylistController(PlaylistService playlistService, PlaylistMapper playlistMapper) {
        this.playlistService = playlistService;
        this.playlistMapper = playlistMapper;
    }

    @PostMapping
    public ResponseEntity<?> createPlaylist(@Valid @RequestBody PlaylistDTO playlistDTO, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            Map<String, Object> response = new HashMap<>();
            Map<String, String> errores = new HashMap<>();

            bindingResult.getFieldErrors().forEach(error -> {
                String campo = error.getField();
                String mensaje = error.getDefaultMessage();
                errores.put(campo, mensaje);
            });

            bindingResult.getGlobalErrors().forEach(error -> {
                errores.put("global", error.getDefaultMessage());
            });

            response.put("status", "ERROR");
            response.put("codigo", HttpStatus.BAD_REQUEST.value());
            response.put("mensaje", "Error de validación - Se requieren campos obligatorios");
            response.put("errores", errores);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            Playlist playlist = playlistMapper.toEntity(playlistDTO);
            Playlist createdPlaylist = playlistService.createPlaylist(playlist);
            PlaylistDTO createdPlaylistDTO = playlistMapper.toDto(createdPlaylist);

            return ResponseEntity.status(HttpStatus.CREATED).body(createdPlaylistDTO);
        } catch (IllegalArgumentException e) {
            Map<String, Object> response = new HashMap<>();
            Map<String, String> errores = new HashMap<>();

            errores.put("error", e.getMessage());
            response.put("status", "ERROR");
            response.put("codigo", HttpStatus.BAD_REQUEST.value());
            response.put("mensaje", "Error en los datos recibidos");
            response.put("errores", errores);

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @GetMapping
    public List<Playlist> getAllPlaylists() {
        return playlistService.getAllPlaylists();
    }

    @GetMapping("/{name}")
    public ResponseEntity<Playlist> getlistByName(@PathVariable String name) {
        try {
            return ResponseEntity.ok(playlistService.getlistByName(name));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable String name) {
        try {
            playlistService.deletePlaylistByName(name);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
