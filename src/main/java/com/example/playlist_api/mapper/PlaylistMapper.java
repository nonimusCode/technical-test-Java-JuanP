package com.example.playlist_api.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.playlist_api.dto.PlaylistDTO;
import com.example.playlist_api.dto.SongDTO;
import com.example.playlist_api.model.Playlist;
import com.example.playlist_api.model.Song;

@Component
public class PlaylistMapper {

    public Playlist toEntity(PlaylistDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("El objeto PlaylistDTO no puede ser nulo");
        }

        Playlist playlist = new Playlist();

        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la playlist es obligatorio");
        }

        if (dto.getDescripcion() == null || dto.getDescripcion().trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción de la playlist es obligatoria");
        }

        if (dto.getCanciones() == null || dto.getCanciones().isEmpty()) {
            throw new IllegalArgumentException("La playlist debe tener al menos una canción");
        }

        playlist.setName(dto.getNombre());
        playlist.setDescription(dto.getDescripcion());

        List<Song> songs = dto.getCanciones().stream()
                .map(this::songToEntity)
                .collect(Collectors.toList());

        playlist.setSongs(songs);

        return playlist;
    }

    public PlaylistDTO toDto(Playlist entity) {
        if (entity == null) {
            return null;
        }

        PlaylistDTO dto = new PlaylistDTO();
        dto.setNombre(entity.getName());
        dto.setDescripcion(entity.getDescription());

        if (entity.getSongs() != null) {
            List<SongDTO> songDTOs = entity.getSongs().stream()
                    .map(this::songToDto)
                    .collect(Collectors.toList());

            dto.setCanciones(songDTOs);
        }

        return dto;
    }

    private Song songToEntity(SongDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("El objeto SongDTO no puede ser nulo");
        }

        Song song = new Song();

        if (dto.getTitulo() == null || dto.getTitulo().trim().isEmpty()) {
            throw new IllegalArgumentException("El título de la canción es obligatorio");
        }

        if (dto.getArtista() == null || dto.getArtista().trim().isEmpty()) {
            throw new IllegalArgumentException("El artista de la canción es obligatorio");
        }

        song.setTitle(dto.getTitulo());
        song.setArtist(dto.getArtista());
        song.setAlbum(dto.getAlbum());

        if (dto.getAnno() != null) {
            if (dto.getAnno() < 1900 || dto.getAnno() > 2100) {
                throw new IllegalArgumentException("El año debe estar entre 1900 y 2100");
            }
            song.setYear(dto.getAnno());
        } else {
            song.setYear(0);
        }

        song.setGenre(dto.getGenero());

        return song;
    }

    private SongDTO songToDto(Song entity) {
        if (entity == null) {
            return null;
        }

        SongDTO dto = new SongDTO();
        dto.setTitulo(entity.getTitle());
        dto.setArtista(entity.getArtist());
        dto.setAlbum(entity.getAlbum());
        dto.setAnno(entity.getYear());
        dto.setGenero(entity.getGenre());

        return dto;
    }
}