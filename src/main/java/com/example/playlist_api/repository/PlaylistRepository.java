package com.example.playlist_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.playlist_api.model.Playlist;

public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    Optional<Playlist> findByName(String name);
}