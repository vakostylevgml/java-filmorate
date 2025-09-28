package ru.yandex.practicum.filmorate.dal.film;

import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

@Component
public class FilmListExtractor implements ResultSetExtractor<List<Film>> {
    @Override
    public List<Film> extractData(ResultSet resultSet) throws SQLException {
        LinkedHashMap<Integer, Film> filmsMap = new LinkedHashMap<>();

        while (resultSet.next()) {
            int filmId = resultSet.getInt("id");
            Film film;
            LinkedHashSet<Genre> genreSet = new LinkedHashSet<>();

            if (!filmsMap.containsKey(filmId)) {
                Timestamp releaseDate = resultSet.getTimestamp("release_date");
                film = Film.builder()
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
                filmsMap.put(filmId, film);
                genreSet = new LinkedHashSet<>();
            } else {
                film = filmsMap.get(filmId);
            }

            int genreId = resultSet.getInt("genre_id");
            String genreName = resultSet.getString("gname");

            if (genreId > 0) {
                Genre genre = Genre.builder()
                        .id(genreId)
                        .name(genreName).build();
                genreSet.add(genre);
            }
            film.addGenres(genreSet);
        }
        return new ArrayList<>(filmsMap.values());
    }
}