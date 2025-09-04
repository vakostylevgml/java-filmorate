package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.except.DuplicateException;
import ru.yandex.practicum.filmorate.except.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Service
public class FIlmService {
    private final int DEF_POP_LIST_SIZE;
    private final FilmStorage filmStorage;

    @Autowired
    public FIlmService(FilmStorage filmStorage, @Value("${films.defaults.defpopsize}") int defPopListSize) {
        this.filmStorage = filmStorage;
        DEF_POP_LIST_SIZE = defPopListSize;
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

    public void likeFilm(long filmId, long userId) {
        try {
            Film film = findFilm(filmId);
            Set<Long> likes = film.getLikes();
            likes.add(userId);
            film.setLikes(likes);
            filmStorage.updateFilm(film);
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new NotFoundException(illegalArgumentException.getMessage());
        }
    }

    public void unlikeFilm(long filmId, long userId) {
        try {
            Film film = findFilm(filmId);
            Set<Long> likes = film.getLikes();
            likes.remove(userId);
            film.setLikes(likes);
            filmStorage.updateFilm(film);
        } catch (IllegalArgumentException illegalArgumentException) {
            throw new NotFoundException(illegalArgumentException.getMessage());
        }
    }

    public List<Film> getMostPopular(int limit) {
        return findAll().stream()
                .sorted(Comparator.comparingInt(Film::getLikesCount).reversed())
                .limit(limit)
                .toList();
    }

    public List<Film> getMostPopular() {
       return getMostPopular(DEF_POP_LIST_SIZE);
    }


}
