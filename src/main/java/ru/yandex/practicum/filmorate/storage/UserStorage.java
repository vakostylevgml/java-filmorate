package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserStorage {
    Optional<User> findUser(int id);

    Optional<User> getUserByEmail(String email);

    Collection<User> findAll();

    User addUser(User user);

    User updateUser(User user);

    void deleteUser(int id);

    void addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    List<User> getFriends(int userId);

    List<User> getCommonFriends(int userId, int userId2);

}
