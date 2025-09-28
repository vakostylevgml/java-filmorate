package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdatedUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<UserDto> findAll() {
        log.info("Find all users");
        return userService.findAll();
    }

    @DeleteMapping
    public void deleteUser(@Valid @RequestBody int id) {
        log.info("Delete user with id {} ", id);
        userService.deleteUser(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto saveUser(@Valid @RequestBody NewUserRequest user) {
        log.info("Save a user: {}", user);
        return userService.addUser(user);
    }

    @PutMapping
    public UserDto updateUser(@Valid @RequestBody UpdatedUserRequest user) {
        log.info("Update a user: {}", user);
        return userService.updateUser(user);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public void addFriend(@PathVariable("userId") int userId, @PathVariable("friendId") int friendId) {
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public void deleteFriend(@PathVariable("userId") int userId, @PathVariable("friendId") int friendId) {
        userService.deleteFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public Collection<UserDto> getFriends(@PathVariable("userId") int userId) {
        return userService.getFriends(userId);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public Collection<UserDto> getCommonFriends(@PathVariable("id") int id, @PathVariable("otherId") int otherId) {
        return userService.getCommonFriends(id, otherId);
    }
}
