package ru.yandex.practicum.filmorate.dal.user;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Component
public class UserListExtractor implements ResultSetExtractor<List<User>> {

    @Override
    public List<User> extractData(ResultSet resultSet) throws SQLException {
        List<User> users = new ArrayList<>();
        while (resultSet.next()) {
            int userId = resultSet.getInt("id");
            User user;
            Timestamp birthday = resultSet.getTimestamp("birthday");
            user = User.builder()
                    .id(userId)
                    .login(resultSet.getString("login"))
                    .email(resultSet.getString("email"))
                    .birthday(birthday.toLocalDateTime().toLocalDate())
                    .name(resultSet.getString("name")).build();
            users.add(user);
        }
        return users;
    }
}