package ru.yandex.practicum.filmorate.dal.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.except.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;

@Qualifier("h2FilmStorage")
@Repository
public class FilmRepository extends BaseRepository<Film> implements FilmStorage {
    private static final Logger log = LoggerFactory.getLogger(FilmRepository.class);

    private static final String FIND_ALL_FILMS = """
             SELECT fl.*, fg.GENRE_ID, rte.NAME as MPANAME, g.NAME as GNAME FROM films fl
                             LEFT JOIN film_genre fg ON fg.film_id = fl.ID
                             LEFT JOIN rating rte ON rte.ID = fl.rating_id
                            LEFT JOIN genre g on g.id = fg.GENRE_ID
                             ORDER BY fl.id;
            \s""";
    private static final String FIND_FILM_BY_ID = """
            SELECT fl.*, fg.GENRE_ID, rte.NAME as MPANAME, g.NAME as GNAME FROM films fl
                        LEFT JOIN film_genre fg ON fg.film_id = fl.ID
                        LEFT JOIN rating rte ON rte.ID = fl.rating_id
                        LEFT JOIN genre g on g.id = fg.GENRE_ID
                        WHERE fl.id = ?
            """;

    private static final String INSERT_FILM = """
            INSERT INTO films (name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)
            """;

    private static final String UPDATE_FILM = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
            "duration = ?, rating_id = ?  WHERE id = ?";

    private static final String DELETE_FILMS = "DELETE FROM films WHERE id = ?";

    private static final String GET_MOST_LIKED = """
             SELECT fl.*, fg.GENRE_ID, rte.NAME as MPANAME, g.NAME as GNAME FROM films fl
                             LEFT JOIN film_genre fg ON fg.film_id = fl.ID
                             LEFT JOIN rating rte ON rte.ID = fl.rating_id
                            LEFT JOIN genre g on g.id = fg.GENRE_ID
                             LEFT JOIN (SELECT film_id, COUNT (user_id) AS lksc
                                                            FROM likes
                                                            GROUP BY film_id
                                                            ) AS flikes ON (fl.id = flikes.film_id)
                                                        ORDER BY flikes.lksc DESC
                                                        LIMIT ?
            \s""";

    private static final String LIKE = "MERGE INTO likes(user_id, film_id)" +
            " VALUES (?, ?)";

    private static final String UNLIKE = "DELETE FROM likes WHERE user_id = ? AND  film_id = ?";
    private static final String DELETE_ALL_LIKES_FROM_FILM = "DELETE FROM likes WHERE film_id = ?"; //used if film is deleted

    private static final String MERGE_GENRE_TO_FILM = "MERGE INTO film_genre (genre_id, film_id) VALUES(?, ?)";
    private static final String DELETE_ALL_GENRES_FROM_FILM = "DELETE FROM film_genre WHERE film_id = ?";

    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper, ResultSetExtractor<List<Film>> extractor) {
        super(jdbc, mapper, extractor);
    }

    @Override
    public Film addFilm(Film film) {
        int id;
        id = insert(
                INSERT_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId());

        film.setId(id);

        if (!film.getGenres().isEmpty()) {
            batchUpdate(MERGE_GENRE_TO_FILM, id, film.getGenres().stream().mapToInt(Genre::getId).toArray());
        }
        log.info("Added film with id = {}", id);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        update(
                UPDATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        delete(DELETE_ALL_GENRES_FROM_FILM, film.getId());

        if (!film.getGenres().isEmpty()) {
            batchUpdate(MERGE_GENRE_TO_FILM, film.getId(), film.getGenres().stream()
                    .mapToInt(Genre::getId)
                    .toArray());
        }
        return film;
    }

    @Override
    public void deleteFilm(Film film) {
        delete(DELETE_ALL_GENRES_FROM_FILM, film.getId());
        delete(DELETE_ALL_LIKES_FROM_FILM, film.getId());
        delete(DELETE_FILMS, film.getId());
    }

    @Override
    public Film getFilmById(int id) {
        return findOne(FIND_FILM_BY_ID, id)
                .orElseThrow(() -> new NotFoundException("Film with id " + id + " not found"));
    }

    @Override
    public Collection<Film> findAll() {
        return findMany(FIND_ALL_FILMS);
    }

    public void like(User user, Film film) {
        update(LIKE, user.getId(), film.getId());
    }

    public void unLike(User user, Film film) {
        delete(UNLIKE, user.getId(), film.getId());
    }

    public List<Film> getMostLiked(int limit) {
        return findMany(GET_MOST_LIKED, limit);
    }
}