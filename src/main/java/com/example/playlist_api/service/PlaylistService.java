package com.example.playlist_api.service;

import java.util.List;

import com.example.playlist_api.model.Playlist;
import com.example.playlist_api.repository.PlaylistRepository;

public class PlaylistService {

    private final PlaylistRepository playlistRepository;

    public PlaylistService(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    public Playlist createPlaylist(Playlist playlist) {
        if (playlist.getName() == null || playlist.getName().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la playlist es requerido");
        }
        return playlistRepository.save(playlist);
    }

    public List<Playlist> getAllPlaylists() {
        return playlistRepository.findAll();
    }

    public Playlist getPlaylistByName(String name) {
        return playlistRepository.findByName(name).orElseThrow(() -> new RuntimeException("Playlist no encontrada"));
    }

    public void deletePlaylistByName(String name) {
        playlistRepository.findByName(name).ifPresent(playlistRepository::delete);
    }

}
