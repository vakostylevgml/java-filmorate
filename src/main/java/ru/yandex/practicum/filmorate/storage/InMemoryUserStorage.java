package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.except.DuplicateException;
import ru.yandex.practicum.filmorate.except.NotFoundException;
import ru.yandex.practicum.filmorate.except.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryUserStorage implements UserStorage {
    private static final Map<Long, User> users = new HashMap<>();
    private static long ids = 0;

    @Override
    public Optional<User> getUser(long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public long addUser(User user) throws DuplicateException {
        if (users.containsKey(user.getId())) {
            throw new DuplicateException("User with id " + user.getId() + " already exists");
        }
        user.setId(++ids);
        users.put(user.getId(), user);
        return user.getId();
    }

    @Override
    public void updateUser(User user) throws NotFoundException{
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("User with id " + user.getId() + " doesn't exist");
        }
        users.put(user.getId(), user);
    }

    @Override
    public void deleteUser(User user) {
        users.remove(user.getId());
    }
}
