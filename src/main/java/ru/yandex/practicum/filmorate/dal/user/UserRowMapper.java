package ru.yandex.practicum.filmorate.dal.user;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@Component
public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Timestamp birthday = resultSet.getTimestamp("birthday");
        int userId = resultSet.getInt("id");

        return User.builder()
                .id(userId)
                .login(resultSet.getString("login"))
                .email(resultSet.getString("email"))
                .birthday(birthday.toLocalDateTime().toLocalDate())
                .name(resultSet.getString("name")).build();
    }
}