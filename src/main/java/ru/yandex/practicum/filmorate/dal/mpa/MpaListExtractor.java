package ru.yandex.practicum.filmorate.dal.mpa;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class MpaListExtractor implements ResultSetExtractor<List<MpaRating>> {
    @Override
    public List<MpaRating> extractData(ResultSet rs) throws SQLException, DataAccessException, IllegalArgumentException {
        List<MpaRating> mpaRatings = new ArrayList<>();
        while (rs.next()) {
            MpaRating rating = MpaRating.builder().id(rs.getInt("id")).name(rs.getString("name")).build();
            mpaRatings.add(rating);
        }
        return mpaRatings;
    }
}