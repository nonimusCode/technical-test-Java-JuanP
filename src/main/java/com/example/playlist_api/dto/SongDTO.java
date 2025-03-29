package com.example.playlist_api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SongDTO {

    @NotBlank(message = "El título de la canción es obligatorio")
    @Size(max = 200, message = "El título no puede exceder los 200 caracteres")
    private String titulo;

    @NotBlank(message = "El artista es obligatorio")
    @Size(max = 200, message = "El nombre del artista no puede exceder los 200 caracteres")
    private String artista;

    @Size(max = 200, message = "El nombre del álbum no puede exceder los 200 caracteres")
    private String album;

    @Min(value = 1900, message = "El año debe ser posterior a 1900")
    @Max(value = 2100, message = "El año no puede ser superior a 2100")
    private Integer anno;

    @Size(max = 100, message = "El género no puede exceder los 100 caracteres")
    private String genero;
}