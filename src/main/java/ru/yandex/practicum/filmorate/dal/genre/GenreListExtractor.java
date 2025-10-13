package ru.yandex.practicum.filmorate.dal.genre;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class GenreListExtractor implements ResultSetExtractor<List<Genre>> {
    @Override
    public List<Genre> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<Genre> genreList = new ArrayList<>();

        while (rs.next()) {
            Genre genre = Genre.builder().id(rs.getInt("id")).name(rs.getString("name")).build();
            genreList.add(genre);
        }
        return genreList;
    }
}