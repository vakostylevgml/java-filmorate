package ru.yandex.practicum.filmorate.dal.mpa;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.util.List;
import java.util.Optional;

@Repository
public class MpaRepository extends BaseRepository<MpaRating> {
    private static final String FIND_ALL_QUERY = "SELECT * FROM rating";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM rating WHERE id = ?";

    public MpaRepository(JdbcTemplate jdbc, RowMapper<MpaRating> mapper, ResultSetExtractor<List<MpaRating>> extractor) {
        super(jdbc, mapper, extractor);
    }

    public Optional<MpaRating> getRatingById(int id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public List<MpaRating> findAll() {
        return findMany(FIND_ALL_QUERY);
    }
}