package com.example.playlist_api.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.playlist_api.model.Playlist;
import com.example.playlist_api.repository.PlaylistRepository;

@Service
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
        Playlist playlist = playlistRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Playlist no encontrada"));
        playlistRepository.delete(playlist);
    }

}
