package ru.yandex.practicum.filmorate.dto.review;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class NewReviewRequest {
    @NotNull
    private Integer userId;

    @NotNull
    private Integer filmId;

    @NotBlank
    @Size(max = 255, message
            = "Review content must not exceed 255 characters")
    private String content;

    @NotNull
    private Boolean isPositive;
}