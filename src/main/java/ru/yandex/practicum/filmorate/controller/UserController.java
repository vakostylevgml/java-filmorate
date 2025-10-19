package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.event.EventDto;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdatedUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final UserService userService;
    private final FilmService filmService;

    @Autowired
    public UserController(UserService userService, FilmService filmService) {
        this.userService = userService;
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<UserDto> findAll() {
        log.info("Find all users");
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable("id") int id) {
        return userService.getUserByID(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable("id") @Positive int userId) {
        log.info("Delete user with id {}", userId);
        userService.deleteUser(userId);
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

    @GetMapping("/{id}/recommendations")
    public List<FilmDto> getRecommendations(@PathVariable("id") int userId) {
        log.info("Getting film recommendations for user with id {}", userId);
        return filmService.getRecommendations(userId);
    }

    @GetMapping("/{id}/feed")
    public Collection<EventDto> getFeed(@PathVariable Integer id) {
        return userService.getFeed(id);
    }

}
