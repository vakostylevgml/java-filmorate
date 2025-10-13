package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Builder
public class Film {
    private int id;

    private String name;

    private String description;

    private LocalDate releaseDate;

    private int duration;

    @Builder.Default
    private LinkedHashSet<Genre> genres = new LinkedHashSet<>();

    private MpaRating mpa;

    public void addGenres(Set<Genre> newGenres) {
        genres.addAll(newGenres);
    }

}
