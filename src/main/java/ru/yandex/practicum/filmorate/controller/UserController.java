package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.except.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private static final Map<Long, User> users = new HashMap<>();
    private static long ids = 0;

    @GetMapping
    public Collection<User> findAll() {
        log.info("Find all users");
        return users.values();
    }

    @PostMapping
    public User saveUser(@Valid @RequestBody User user) {
        log.info("Save a user: {}", user);
        if (users.containsKey(user.getId())) {
            throw new ValidationException("User with id " + user.getId() + " already exists");
        }
        user.setId(++ids);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Update a user: {}", user);
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("User with id " + user.getId() + " doesn't exist");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        return user;
    }
}
