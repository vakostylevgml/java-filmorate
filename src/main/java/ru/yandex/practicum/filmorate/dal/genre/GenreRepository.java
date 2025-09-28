package ru.yandex.practicum.filmorate.dal.genre;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public class GenreRepository extends BaseRepository<Genre> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM genre";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM genre WHERE id = ?";

    public GenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper, ResultSetExtractor<List<Genre>> extractor) {
        super(jdbc, mapper, extractor);
    }

    public Optional<Genre> getGenreById(int id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public Collection<Genre> findAll() {
        return findMany(FIND_ALL_QUERY);
    }
}