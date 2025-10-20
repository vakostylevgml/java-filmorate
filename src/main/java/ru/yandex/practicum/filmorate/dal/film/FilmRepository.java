package ru.yandex.practicum.filmorate.dal.film;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.except.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository
public class FilmRepository extends BaseRepository<Film> implements FilmStorage {
    private static final Logger log = LoggerFactory.getLogger(FilmRepository.class);

    private static final String FIND_ALL_FILMS = """
             SELECT fl.*, fg.GENRE_ID, rte.NAME as MPANAME, g.NAME as GNAME, fd.director_id, d.name as director_name
             FROM films fl
                             LEFT JOIN film_genre fg ON fg.film_id = fl.ID
                             LEFT JOIN rating rte ON rte.ID = fl.rating_id
                            LEFT JOIN genre g on g.id = fg.GENRE_ID
                            LEFT JOIN film_director fd ON fd.film_id = fl.id
                            LEFT JOIN directors d ON d.id = fd.director_id
                             ORDER BY fl.id;
            \s""";
    private static final String FIND_FILM_BY_ID = """
            SELECT fl.*, fg.GENRE_ID, rte.NAME as MPANAME, g.NAME as GNAME, fd.director_id, d.name as director_name
            FROM films fl
                        LEFT JOIN film_genre fg ON fg.film_id = fl.ID
                        LEFT JOIN rating rte ON rte.ID = fl.rating_id
                        LEFT JOIN genre g on g.id = fg.GENRE_ID
                        LEFT JOIN film_director fd ON fd.film_id = fl.id
                        LEFT JOIN directors d ON d.id = fd.director_id
                        WHERE fl.id = ?
            """;

    private static final String INSERT_FILM = """
            INSERT INTO films (name, description, release_date, duration, rating_id) VALUES (?, ?, ?, ?, ?)
            """;

    private static final String UPDATE_FILM = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
            "duration = ?, rating_id = ?  WHERE id = ?";

    private static final String DELETE_FILMS = "DELETE FROM films WHERE id = ?";

    private static final String GET_POPULAR_FILMS = """
            WITH top_films AS (
                       SELECT fl.id, COALESCE(flikes.lksc, 0) AS lksc
                       FROM films fl
                       LEFT JOIN (
                           SELECT film_id, COUNT(user_id) AS lksc
                           FROM likes
                           GROUP BY film_id
                       ) AS flikes ON fl.id = flikes.film_id
                       WHERE (? IS NULL OR EXISTS (
                                 SELECT 1 FROM film_genre fg2
                                 WHERE fg2.film_id = fl.id AND fg2.genre_id = ?
                             ))
                         AND (? IS NULL OR EXTRACT(YEAR FROM fl.release_date) = ?)
                       ORDER BY lksc DESC NULLS LAST, fl.id ASC
                       LIMIT ?
                   )
                   SELECT fl.*,\s
                          fg.genre_id,\s
                          rte.name AS mpaname,\s
                          g.name  AS gname,\s
                          fd.director_id,\s
                          d.name  AS director_name
                   FROM top_films tf
                   JOIN films fl              ON fl.id = tf.id
                   LEFT JOIN film_genre fg    ON fg.film_id = fl.id
                   LEFT JOIN genre g          ON g.id = fg.genre_id
                   LEFT JOIN rating rte       ON rte.id = fl.rating_id
                   LEFT JOIN film_director fd ON fd.film_id = fl.id
                   LEFT JOIN directors d      ON d.id = fd.director_id
                   ORDER BY tf.lksc DESC, fl.id ASC, fg.genre_id;
            """;

    private static final String LIKE = "MERGE INTO likes(user_id, film_id)" +
            " VALUES (?, ?)";

    private static final String UNLIKE = "DELETE FROM likes WHERE user_id = ? AND  film_id = ?";
    private static final String DELETE_ALL_LIKES_FROM_FILM = "DELETE FROM likes WHERE film_id = ?"; //used if film is deleted

    private static final String MERGE_GENRE_TO_FILM = "MERGE INTO film_genre (genre_id, film_id) VALUES(?, ?)";
    private static final String DELETE_ALL_GENRES_FROM_FILM = "DELETE FROM film_genre WHERE film_id = ?";
    private static final String GET_COMMON_FILMS_OF_TWO_USERS = """
            SELECT fl.*, fg.GENRE_ID, rte.NAME as MPANAME, g.NAME as GNAME, fd.director_id, d.name as director_name
            FROM films fl
            LEFT JOIN film_genre fg ON fg.film_id = fl.ID
            LEFT JOIN rating rte ON rte.ID = fl.rating_id
            LEFT JOIN genre g on g.id = fg.GENRE_ID
            LEFT JOIN film_director fd ON fd.film_id = fl.id
            LEFT JOIN directors d ON d.id = fd.director_id
            WHERE fl.id IN (
                SELECT l1.film_id
                FROM likes l1
                INNER JOIN likes l2 ON l1.film_id = l2.film_id
                WHERE l1.user_id = ? AND l2.user_id = ?
            )
            ORDER BY (
                SELECT COUNT(*)
                FROM likes l
                WHERE l.film_id = fl.id
            ) DESC
            """;

    private static final String GET_FILMS_BY_DIRECTOR = """
            SELECT fl.*, fg.GENRE_ID, rte.NAME as MPANAME, g.NAME as GNAME, fd.director_id, d.name as director_name
            FROM films fl
            LEFT JOIN film_genre fg ON fg.film_id = fl.ID
            LEFT JOIN rating rte ON rte.ID = fl.rating_id
            LEFT JOIN genre g on g.id = fg.GENRE_ID
            LEFT JOIN film_director fd ON fd.film_id = fl.id
            LEFT JOIN directors d ON d.id = fd.director_id
            WHERE fd.director_id = ?
            ORDER BY %s
            """;

    private static final String MERGE_DIRECTOR_TO_FILM = "MERGE INTO film_director (director_id, film_id) VALUES(?, ?)";
    private static final String DELETE_ALL_DIRECTORS_FROM_FILM = "DELETE FROM film_director WHERE film_id = ?";
    private static final String GET_LIKED_FILM_IDS_BY_USER = "SELECT film_id FROM likes WHERE user_id = ?";

    private static final String SEARCH_BY_TITLE = """
            SELECT fl.*, fg.GENRE_ID, rte.NAME as MPANAME, g.NAME as GNAME, fd.director_id, d.name as director_name
            FROM films fl
            LEFT JOIN film_genre fg ON fg.film_id = fl.ID
            LEFT JOIN rating rte ON rte.ID = fl.rating_id
            LEFT JOIN genre g on g.id = fg.GENRE_ID
            LEFT JOIN film_director fd ON fd.film_id = fl.id
            LEFT JOIN directors d ON d.id = fd.director_id
            LEFT JOIN (SELECT film_id, COUNT(user_id) AS like_count
                        FROM likes
                        GROUP BY film_id
                        ) AS flikes ON (fl.id = flikes.film_id)
            WHERE LOWER(fl.name) LIKE '%' || ? || '%'
            ORDER BY flikes.like_count DESC NULLS LAST
            """;

    private static final String SEARCH_BY_DIRECTOR = """
            SELECT fl.*, fg.GENRE_ID, rte.NAME as MPANAME, g.NAME as GNAME, fd.director_id, d.name as director_name
            FROM films fl
            LEFT JOIN film_genre fg ON fg.film_id = fl.ID
            LEFT JOIN rating rte ON rte.ID = fl.rating_id
            LEFT JOIN genre g on g.id = fg.GENRE_ID
            LEFT JOIN film_director fd ON fd.film_id = fl.id
            LEFT JOIN directors d ON d.id = fd.director_id
            LEFT JOIN (SELECT film_id, COUNT(user_id) AS like_count
                        FROM likes
                        GROUP BY film_id
                        ) AS flikes ON (fl.id = flikes.film_id)
            WHERE LOWER(d.name) LIKE '%' || ? || '%'
            ORDER BY flikes.like_count DESC NULLS LAST
            """;

    private static final String SEARCH_BY_TITLE_AND_DIRECTOR = """
            SELECT fl.*, fg.GENRE_ID, rte.NAME as MPANAME, g.NAME as GNAME, fd.director_id, d.name as director_name
            FROM films fl
            LEFT JOIN film_genre fg ON fg.film_id = fl.ID
            LEFT JOIN rating rte ON rte.ID = fl.rating_id
            LEFT JOIN genre g on g.id = fg.GENRE_ID
            LEFT JOIN film_director fd ON fd.film_id = fl.id
            LEFT JOIN directors d ON d.id = fd.director_id
            LEFT JOIN (SELECT film_id, COUNT(user_id) AS like_count
                        FROM likes
                        GROUP BY film_id
                        ) AS flikes ON (fl.id = flikes.film_id)
            WHERE LOWER(fl.name) LIKE '%' || ? || '%' OR LOWER(d.name) LIKE '%' || ? || '%'
            ORDER BY flikes.like_count DESC NULLS LAST
            """;

    public FilmRepository(JdbcTemplate jdbc, RowMapper<Film> mapper, ResultSetExtractor<List<Film>> extractor) {
        super(jdbc, mapper, extractor);
    }

    @Override
    public Set<Integer> getLikedFilmIdsByUser(int userId) {
        return new HashSet<>(jdbc.queryForList(GET_LIKED_FILM_IDS_BY_USER, Integer.class, userId));
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

        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            batchUpdate(MERGE_DIRECTOR_TO_FILM, id,
                    film.getDirectors().stream().mapToInt(Director::getId).toArray());
        }

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

        delete(DELETE_ALL_DIRECTORS_FROM_FILM, film.getId());
        if (film.getDirectors() != null && !film.getDirectors().isEmpty()) {
            batchUpdate(MERGE_DIRECTOR_TO_FILM, film.getId(),
                    film.getDirectors().stream().mapToInt(Director::getId).toArray());
        }

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
        delete(DELETE_ALL_DIRECTORS_FROM_FILM, film.getId());
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

    public List<Film> getCommonFilms(int userId, int friendId) {
        return findMany(GET_COMMON_FILMS_OF_TWO_USERS, userId, friendId);
    }

    public List<Film> getFilmsByDirector(int directorId, String sortBy) {
        String orderClause = switch (sortBy.toLowerCase()) {
            case "year" -> "fl.release_date";
            case "likes" -> """
                    (SELECT COUNT(*) FROM likes WHERE film_id = fl.id) DESC,
                    fl.release_date DESC
                    """;
            default -> throw new IllegalArgumentException("Invalid sort parameter: " + sortBy);
        };
        String query = String.format(GET_FILMS_BY_DIRECTOR, orderClause);
        return findMany(query, directorId);
    }

    public List<Film> getPopularFilmsByParameters(Integer genreId, Integer year, int count) {
        return findMany(GET_POPULAR_FILMS, genreId, genreId, year, year, count);
    }

    @Override
    public List<Film> searchFilms(String query, String[] searchTypes) {
        String searchTerm = "%" + query.toLowerCase() + "%";
        boolean searchByTitle = false;
        boolean searchByDirector = false;

        for (String type : searchTypes) {
            if ("title".equalsIgnoreCase(type.trim())) {
                searchByTitle = true;
            }
            if ("director".equalsIgnoreCase(type.trim())) {
                searchByDirector = true;
            }
        }

        if (searchByTitle && searchByDirector) {
            return findMany(SEARCH_BY_TITLE_AND_DIRECTOR, searchTerm, searchTerm);
        } else if (searchByTitle) {
            return findMany(SEARCH_BY_TITLE, searchTerm);
        } else if (searchByDirector) {
            return findMany(SEARCH_BY_DIRECTOR, searchTerm);
        } else {
            return List.of();
        }
    }
}