package ru.yandex.practicum.filmorate.dal.film;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedHashSet;

@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Timestamp releaseDate = resultSet.getTimestamp("release_date");
        int filmId = resultSet.getInt("id");

        Film film = Film.builder()
                .id(filmId)
                .name(resultSet.getString("name"))
                .description(resultSet.getString("description"))
                .releaseDate(releaseDate.toLocalDateTime().toLocalDate())
                .duration(resultSet.getInt("duration"))
                .build();

        if (resultSet.getInt("rating_id") > 0) {
            MpaRating mpaRating = MpaRating.builder()
                    .id(resultSet.getInt("rating_id"))
                    .name(resultSet.getString("mpaname")).build();
            film.setMpa(mpaRating);
        }

        LinkedHashSet<Genre> genreSet = new LinkedHashSet<>();

        do {
            int genreId = resultSet.getInt("genre_id");
            String genreName = resultSet.getString("gname");

            if (genreId > 0) {
                Genre genre = Genre.builder()
                        .id(genreId)
                        .name(genreName).build();
                genreSet.add(genre);
            }

        } while (resultSet.next());

        film.addGenres(genreSet);

        return film;
    }
}