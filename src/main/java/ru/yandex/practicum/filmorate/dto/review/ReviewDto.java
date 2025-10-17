package ru.yandex.practicum.filmorate.dto.review;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReviewDto {
    private int reviewId;
    private int userId;
    private int filmId;
    private String content;
    @JsonProperty("isPositive")
    private boolean isPositive;
    private int useful;
}