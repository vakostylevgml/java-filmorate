package ru.yandex.practicum.filmorate.dto.film;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.dto.film.constraint.ReleaseDateConstraint;
import ru.yandex.practicum.filmorate.dto.mpa.MpaRequest;

import java.time.LocalDate;
import java.util.LinkedHashSet;

@Data
@Builder
@Validated
public class NewFilmRequest {
    @NotBlank(message = "Name cannot be blank")
    @Size(max = 255, message
            = "Name must be < 255 characters")
    private String name;

    @Size(max = 200, message
            = "Description must be between 0 and 200 characters")
    private String description;

    @ReleaseDateConstraint
    private LocalDate releaseDate;

    @Positive
    private int duration;

    @JsonProperty("genres")
    private LinkedHashSet<GenreId> genres;

    @NotNull
    private MpaRequest mpa;
}