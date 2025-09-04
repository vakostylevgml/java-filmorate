package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.except.DuplicateException;
import ru.yandex.practicum.filmorate.except.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Objects;
import java.util.Set;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User findUser(long id) {
        return userStorage.findUser(id)
                .orElseThrow(() -> new NotFoundException("User with id " + id + "not found"));
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User addUser(User user) {
        Objects.requireNonNull(user);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        try {
            return userStorage.addUser(user);
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new DuplicateException(illegalArgumentException.getMessage());
        }
    }

    public User updateUser(User user) {
        Objects.requireNonNull(user);

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        try {
            return userStorage.updateUser(user);
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new NotFoundException(illegalArgumentException.getMessage());
        }
    }

    public void deleteUser(long id) {
        userStorage.deleteUser(id);
        //@TODO delete friends
    }

    public Set<User> getUserFriends(long id) {
        try {
            return userStorage.getFriends(id);
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new NotFoundException(illegalArgumentException.getMessage());
        }
    }

    public Set<User> getCommonFriends(long user1, long user2) {
        try {
            Set<User> friends1 = userStorage.getFriends(user1);
            Set<User> friends2 = userStorage.getFriends(user2);
            friends1.retainAll(friends2);
            return friends1;
        } catch (IllegalArgumentException|NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    public void addFriend(long userId, long friendId) {
        try {
            User user = findUser(userId);
            User friend = findUser(friendId);
            Set<Long> userFriendsIds = user.getFriends();
            userFriendsIds.add(friendId);
            Set<Long> friendFriendsIds = friend.getFriends();
            friendFriendsIds.add(userId);
            user.setFriends(userFriendsIds);
            friend.setFriends(friendFriendsIds);
            userStorage.updateUser(user);
            userStorage.updateUser(friend);
        } catch (IllegalArgumentException|NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }

    public void deleteFriend(long userId, long friendId) {
        try {
            User user = findUser(userId);
            User friend = findUser(friendId);

            Set<Long> userFriendsIds = user.getFriends();
            userFriendsIds.remove(friendId);
            Set<Long> friendFriendsIds = friend.getFriends();
            friendFriendsIds.remove(userId);
            user.setFriends(userFriendsIds);
            friend.setFriends(friendFriendsIds);
            userStorage.updateUser(user);
            userStorage.updateUser(friend);
        } catch (IllegalArgumentException|NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }
    }
}
