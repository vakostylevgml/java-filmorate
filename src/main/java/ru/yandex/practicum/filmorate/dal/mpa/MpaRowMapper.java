package ru.yandex.practicum.filmorate.dal.mpa;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.MpaRating;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class MpaRowMapper implements RowMapper<MpaRating> {
    @Override
    public MpaRating mapRow(ResultSet rs, int rowNum) throws SQLException, IllegalArgumentException {
        return MpaRating.builder().id(rs.getInt("id")).name(rs.getString("name")).build();
    }
}