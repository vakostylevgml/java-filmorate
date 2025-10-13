package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdatedUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.model.User;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {
    public static User mapToUser(NewUserRequest request) {
        String name;
        if (request.getName() == null || request.getName().isBlank()) {
            name = request.getLogin();
        } else {
            name = request.getName();
        }
        return User.builder().login(request.getLogin()).email(request.getEmail()).password(request.getPassword())
                .birthday(request.getBirthday()).name(name).build();
    }

    public static User mapToUser(UpdatedUserRequest request) {
        return User.builder().login(request.getLogin()).email(request.getEmail()).password(request.getPassword())
                .birthday(request.getBirthday()).name(request.getName()).id(request.getId()).build();
    }

    public static UserDto mapToUserDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setLogin(user.getLogin());
        dto.setBirthday(user.getBirthday());
        dto.setName(user.getName());
        return dto;
    }
}