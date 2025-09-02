package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Optional;

public interface UserStorage {
    Optional<User> getUser(long id);
    long addUser(User user);
    void updateUser(User user);
    void deleteUser(User user);
}
