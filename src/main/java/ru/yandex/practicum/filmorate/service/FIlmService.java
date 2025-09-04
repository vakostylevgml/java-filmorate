package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.except.DuplicateException;
import ru.yandex.practicum.filmorate.except.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
public class FIlmService {
    private final FilmStorage filmStorage;

    @Autowired
    public FIlmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public Film findFilm(long id) {
        return filmStorage.findFilm(id)
                .orElseThrow(() -> new NotFoundException("Film with id " + id + "not found"));
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film addFilm(Film film) {
        Objects.requireNonNull(film);

        try {
            return filmStorage.addFilm(film);
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new DuplicateException(illegalArgumentException.getMessage());
        }
    }

    public Film updateFilm(Film film) {
        Objects.requireNonNull(film);

        try {
            return filmStorage.updateFilm(film);
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new NotFoundException(illegalArgumentException.getMessage());
        }
    }

    public void deleteFilm(long id) {
        filmStorage.deleteFilm(id);
    }

    public void likeFilm(long filmId, long userId) {}

    public void unlikeFilm(long filmId, long userId) {}

    public List<Film> getMostPopular(int limit) {
        return List.of();
    }

}
