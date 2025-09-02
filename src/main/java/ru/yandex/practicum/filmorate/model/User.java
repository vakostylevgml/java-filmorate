package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(of = "id")
public class User {
    private long id;

    @Email
    private String email;

    @Pattern(regexp = "^[^\s]+$")
    private String login;
    private String name;

    @Past
    private LocalDate birthday;

    private Set<Long> friends = new HashSet<>();
}
