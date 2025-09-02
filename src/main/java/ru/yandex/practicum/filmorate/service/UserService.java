package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.except.DuplicateException;
import ru.yandex.practicum.filmorate.except.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User getUser(long id) throws NotFoundException {
        Optional<User> user = userStorage.getUser(id);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new NotFoundException("User with id " + id + " not found");
        }
    }

    public long addUser(User user) throws DuplicateException {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.addUser(user);
    }

    public void updateUser(User user) throws NotFoundException {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        userStorage.updateUser(user);
    }

    public void deleteUser(User user) {
        userStorage.deleteUser(user);
    }

    public void addFriend(long userId, long friendId) throws DuplicateException, NotFoundException {
        Optional<User> user = userStorage.getUser(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
        Optional<User> friend = userStorage.getUser(friendId);
        if (friend.isEmpty()) {
            throw new NotFoundException("Friend with id " + friendId + " not found");
        }

        Set<Long> userFriendIds = user.get().getFriends();
        if (userFriendIds.contains(friendId)) {
            throw new DuplicateException("Friend with id " + friendId + " already exists for user with id " + userId);
        }

        userFriendIds.add(friendId);
        Set<Long> friendsIds = friend.get().getFriends();
        friendsIds.add(userId);

        userStorage.updateUser(user.get());
        userStorage.updateUser(friend.get());
    }

    public void deleteFriend(long userId, long friendId) throws NotFoundException {
        Optional<User> user = userStorage.getUser(userId);
        if (user.isEmpty()) {
            throw new NotFoundException("User with id " + userId + " not found");
        }
        Optional<User> friend = userStorage.getUser(friendId);
        if (friend.isEmpty()) {
            throw new NotFoundException("Friend with id " + friendId + " not found");
        }

        Set<Long> userFriendIds = user.get().getFriends();
        userFriendIds.remove(friendId);
        Set<Long> friendsIds = friend.get().getFriends();
        friendsIds.remove(userId);

        userStorage.updateUser(user.get());
        userStorage.updateUser(friend.get());
    }


}
