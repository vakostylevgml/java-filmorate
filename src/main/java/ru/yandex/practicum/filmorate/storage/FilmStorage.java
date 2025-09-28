package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(Film film);

    Film getFilmById(int id);

    Collection<Film> findAll();

    void like(User user, Film film);

    void unLike(User user, Film film);

    List<Film> getMostLiked(int count);
}
