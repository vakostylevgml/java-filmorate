package ru.yandex.practicum.filmorate.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UserDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int id;

    private String email;
    private String login;
    private String name;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDate birthday;
}