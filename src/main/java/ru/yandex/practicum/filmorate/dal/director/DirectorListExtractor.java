package ru.yandex.practicum.filmorate.dal.director;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class DirectorListExtractor implements ResultSetExtractor<List<Director>> {
    @Override
    public List<Director> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<Director> directors = new ArrayList<>();
        while (rs.next()) {
            Director director = Director.builder()
                    .id(rs.getInt("id"))
                    .name(rs.getString("name"))
                    .build();
            directors.add(director);
        }
        return directors;
    }
}