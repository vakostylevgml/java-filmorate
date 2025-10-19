package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface FilmStorage {
    Film addFilm(Film film);

    Film updateFilm(Film film);

    void deleteFilm(Film film);

    Film getFilmById(int id);

    Collection<Film> findAll();

    void like(User user, Film film);

    void unLike(User user, Film film);

    List<Film> getCommonFilms(int userId, int friendId);

    List<Film> getFilmsByDirector(int directorId, String sortBy);

    List<Film> getPopularFilmsByParameters(Integer genreId, Integer year, int limit);

    List<Film> searchFilms(String query, String[] searchTypes);

    Set<Integer> getLikedFilmIdsByUser(int userId);
}
