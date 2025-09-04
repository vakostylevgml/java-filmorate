package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.Optional;

public interface FilmStorage {
    Optional<Film> findFilm(long id);

    Collection<Film> findAll();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(long id);
}
