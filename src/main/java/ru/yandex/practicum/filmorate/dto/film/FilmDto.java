package ru.yandex.practicum.filmorate.dto.film;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.model.Genre;

import java.time.LocalDate;
import java.util.LinkedHashSet;

@Data
@Builder
public class FilmDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int id;

    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private LinkedHashSet<Genre> genres;

    @JsonProperty("mpa")
    private MpaDto mpa;
}