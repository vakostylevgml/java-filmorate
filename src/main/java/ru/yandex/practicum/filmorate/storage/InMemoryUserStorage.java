package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private static final Map<Long, User> users = new HashMap<>();
    private static long ids = 0;

    @Override
    public Optional<User> findUser(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User addUser(User user) {
        Objects.requireNonNull(user);

        if (users.containsKey(user.getId())) {
            throw new IllegalArgumentException("User with id " + user.getId() + " already exists");
        }
        user.setId(++ids);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        Objects.requireNonNull(user);

        if (!users.containsKey(user.getId())) {
            throw new IllegalArgumentException("User with id " + user.getId() + " doesn't exist");
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(long id) {
        users.remove(id);
    }

    @Override
    public Set<User> getFriends(long id) {
        if (!users.containsKey(id)) {
            throw new IllegalArgumentException("User with id " + id + " doesn't exist");
        }
        User user = users.get(id);
       return user.getFriends().stream()
                        .map(this::findUser)
                        .flatMap(Optional::stream)
                        .collect(Collectors.toSet());
    }
}
