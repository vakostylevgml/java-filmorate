package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.validation.FilmReleaseConstraint;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = "id")
public class Film {
    private long id;

    @NotBlank
    private String name;

    @Size(max = 200)
    private String description;

    @FilmReleaseConstraint
    private LocalDate releaseDate;

    @Positive
    private int duration;

    private Set<Long> likes = new HashSet<>();

    private Set<Genre> genres = new HashSet<>();

    private Rating rating;

    public int getLikesCount() {
        return likes.size();
    }

}
