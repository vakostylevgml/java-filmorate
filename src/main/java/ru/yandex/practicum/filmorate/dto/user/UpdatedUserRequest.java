package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;

@Data
@Builder
@Validated
public class UpdatedUserRequest {
    private int id;

    @Pattern(regexp = "\\S+")
    @NotBlank
    @Size(max = 255, message
            = "Login must be < 255 characters")
    private String login;

    @NotEmpty
    @Email
    @Size(max = 255, message
            = "Email must be < 255 characters")
    private String email;

    @Size(max = 40, message
            = "Pwd must be between 0 and 40 characters")
    private String password;

    @NotNull
    @PastOrPresent
    private LocalDate birthday;

    @Size(max = 255, message
            = "Name must be between 0 and 255 characters")
    private String name;
}