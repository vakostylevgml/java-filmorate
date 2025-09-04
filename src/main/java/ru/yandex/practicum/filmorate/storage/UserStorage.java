package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {
    Optional<User> findUser(long id);

    Collection<User> findAll();

    User addUser(User user);

    User updateUser(User user);

    void deleteUser(long id);

    Set<User> getFriends(long id);
}
