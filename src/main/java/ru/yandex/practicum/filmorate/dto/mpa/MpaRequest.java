package ru.yandex.practicum.filmorate.dto.mpa;

import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class MpaRequest {
    @Positive
    private int id;
}