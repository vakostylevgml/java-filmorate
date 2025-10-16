package ru.yandex.practicum.filmorate.dto.director;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UpdatedDirectorRequest {
    @Positive
    private int id;
    @NotBlank
    private String name;
}