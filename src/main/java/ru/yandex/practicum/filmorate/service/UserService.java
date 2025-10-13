package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdatedUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.except.DuplicateException;
import ru.yandex.practicum.filmorate.except.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto getUserByID(int userId) {
        Optional<User> user = userStorage.findUser(userId);
        if (user.isPresent()) {
            return UserMapper.mapToUserDto(user.get());
        } else {
            throw new NotFoundException("User with id " + userId + " not found");
        }
    }

    public UserDto addUser(NewUserRequest userRequest) {
        Optional<User> alreadyExistUser = userStorage.getUserByEmail(userRequest.getEmail());
        if (alreadyExistUser.isPresent()) {
            throw new DuplicateException("Email already in use");
        }
        User user = UserMapper.mapToUser(userRequest);
        userStorage.addUser(user);
        return UserMapper.mapToUserDto(user);
    }

    public UserDto updateUser(UpdatedUserRequest userRequest) {
        Optional<User> toBeUpdated = userStorage.findUser(userRequest.getId());
        if (toBeUpdated.isPresent()) {
            User user = UserMapper.mapToUser(userRequest);
            userStorage.updateUser(user);
            return UserMapper.mapToUserDto(user);
        } else {
            log.warn("Can't update user with id = {}. User doesn't exist", userRequest.getId());
            throw new NotFoundException("Can't update user with id = " + userRequest.getId() + ": user doesn't exist");
        }
    }

    public void deleteUser(int userId) {
        Optional<User> user = userStorage.findUser(userId);
        user.ifPresent(value -> userStorage.deleteUser(value.getId()));
    }

    public List<UserDto> findAll() {
        return userStorage.findAll()
                .stream()
                .map(UserMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public void addFriend(int userId, int friendId) throws NotFoundException {
        User user = userStorage.findUser(userId).orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        User friend = userStorage.findUser(friendId).orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        userStorage.addFriend(user.getId(), friend.getId());
    }

    public void deleteFriend(int userId, int friendId) throws NotFoundException {
        User user = userStorage.findUser(userId).orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        User friend = userStorage.findUser(friendId).orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        userStorage.deleteFriend(user.getId(), friend.getId());
    }

    public Set<UserDto> getFriends(int id) throws NotFoundException {
        User user = userStorage.findUser(id).orElseThrow(() -> new NotFoundException("User with id " + id + " not found"));
        return userStorage.getFriends(id).stream().map(UserMapper::mapToUserDto).collect(Collectors.toSet());
    }

    public Set<UserDto> getCommonFriends(int id1, int id2) throws NotFoundException {
        User userId1 = userStorage.findUser(id1).orElseThrow(() -> new NotFoundException("User with id " + id1 + " not found"));
        User userId2 = userStorage.findUser(id2).orElseThrow(() -> new NotFoundException("User with id " + id2 + " not found"));
        return userStorage.getCommonFriends(id1, id2).stream().map(UserMapper::mapToUserDto).collect(Collectors.toSet());
    }



}
