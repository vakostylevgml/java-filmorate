package ru.yandex.practicum.filmorate.dal.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
public class UserRepository extends BaseRepository<User> implements UserStorage {
    private static final String FIND_ALL = "SELECT * FROM users";
    private static final String FIND_BY_EMAIL = "SELECT * FROM users WHERE email = ?";
    private static final String FIND_BY_ID = "SELECT * FROM users WHERE id = ?";
    private static final String INSERT = "INSERT INTO users(email, login, password, name, birthday)" +
            " VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE users SET login = ?, password = ?, email = ?, name = ?," +
            " birthday = ?  WHERE id = ?";
    private static final String DELETE_BY_ID = "DELETE FROM users WHERE id = ?";

    private static final String GET_FRIENDS = "SELECT * FROM users WHERE id IN (" +
            "SELECT user_id_2 FROM friends WHERE user_id_1 = ?)";
    private static final String ADD_FRIEND = "INSERT INTO friends(user_id_1, user_id_2) VALUES (?, ?)";
    private static final String DELETE_FRIEND = "DELETE FROM friends WHERE user_id_1 = ? AND user_id_2 = ?";
    private static final String DELETE_USER_FROM_ALL_FRIENDS = "DELETE FROM friends WHERE user_id_1 = ? OR user_id_2 = ?";
    private static final String COMMON_FRIENDS = """
            SELECT u.* FROM friends AS user_fr
            INNER JOIN friends AS friend_fr ON friend_fr.user_id_2 = user_fr.user_id_2
            INNER JOIN users AS u ON u.id = friend_fr.user_id_2
            WHERE user_fr.user_id_1 = ? AND friend_fr.user_id_1 = ?
            AND user_fr.user_id_2 <> friend_fr.user_id_1 AND friend_fr.user_id_1 <> user_fr.user_id_1""";

    public UserRepository(JdbcTemplate jdbc, RowMapper<User> mapper, ResultSetExtractor<List<User>> extractor) {
        super(jdbc, mapper, extractor);
    }

    @Override
    public User addUser(User user) {
        int id = insert(
                INSERT,
                user.getEmail(),
                user.getLogin(),
                user.getPassword(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        log.info("Added user with id = {}", id);
        return user;
    }

    @Override
    public User updateUser(User user) {
        update(
                UPDATE,
                user.getLogin(),
                user.getPassword(),
                user.getEmail(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        return user;
    }

    @Override
    public void deleteUser(int id) {
        delete(DELETE_USER_FROM_ALL_FRIENDS, id);
        delete(DELETE_BY_ID, id);
    }

    public void deleteFriend(int userId, int friendId) {
        delete(DELETE_FRIEND, userId, friendId);
    }

    @Override
    public Optional<User> findUser(int id) {
        return findOne(FIND_BY_ID, id);
    }

    @Override
    public List<User> findAll() {
        return findMany(FIND_ALL);
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return findOne(FIND_BY_EMAIL, email);
    }

    public void addFriend(int userId, int friendId) {
        update(ADD_FRIEND, userId, friendId);
    }

    public List<User> getFriends(int userId) {
        return findMany(GET_FRIENDS, userId);
    }

    public List<User> getCommonFriends(int userId, int userId2) {
        return findMany(COMMON_FRIENDS, userId, userId2);
    }
}