package ru.yandex.practicum.filmorate.dal.director;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class DirectorRepository extends BaseRepository<Director> {

    private static final String FIND_ALL_QUERY = "SELECT * FROM directors ORDER BY id";
    private static final String FIND_BY_ID_QUERY = "SELECT * FROM directors WHERE id = ?";
    private static final String FIND_BY_IDS_QUERY = "SELECT * FROM directors WHERE id IN (%s)";
    private static final String INSERT_QUERY = "INSERT INTO directors (name) VALUES (?)";
    private static final String UPDATE_QUERY = "UPDATE directors SET name = ? WHERE id = ?";
    private static final String DELETE_QUERY = "DELETE FROM directors WHERE id = ?";
    private static final String COUNT_FILMS_BY_DIRECTOR = "SELECT COUNT(*) FROM film_director WHERE director_id = ?";

    public DirectorRepository(JdbcTemplate jdbc,
                              RowMapper<Director> mapper,
                              ResultSetExtractor<List<Director>> extractor) {
        super(jdbc, mapper, extractor);
    }

    public Optional<Director> getDirectorById(int id) {
        return findOne(FIND_BY_ID_QUERY, id);
    }

    public List<Director> getDirectorsByIds(List<Integer> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        String placeholders = String.join(",", Collections.nCopies(ids.size(), "?"));
        String query = String.format(FIND_BY_IDS_QUERY, placeholders);
        return findMany(query, ids.toArray());
    }

    public List<Director> findAll() {
        return findMany(FIND_ALL_QUERY);
    }

    public Director addDirector(Director director) {
        int id = insert(INSERT_QUERY, director.getName());
        director.setId(id);
        return director;
    }

    public void updateDirector(Director director) {
        update(UPDATE_QUERY, director.getName(), director.getId());
    }

    public void deleteDirector(int id) {
        Integer filmCount = jdbc.queryForObject(COUNT_FILMS_BY_DIRECTOR, Integer.class, id);
        delete(DELETE_QUERY, id);
    }
}