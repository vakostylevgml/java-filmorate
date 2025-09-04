package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private static final Map<Long, Film> films = new HashMap<>();
    private static long ids = 0;

    @Override
    public Optional<Film> findFilm(long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film addFilm(Film film) {
        Objects.requireNonNull(film);

        if (films.containsKey(film.getId())) {
            throw new IllegalArgumentException("Film with id " + film.getId() + " already exists");
        }
        film.setId(++ids);
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        Objects.requireNonNull(film);

        if (!films.containsKey(film.getId())) {
            throw new IllegalArgumentException("Film with id " + film.getId() + " doesn't exist");
        }
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void deleteFilm(long id) {
        films.remove(id);
    }
}
