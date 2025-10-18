package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdatedFilmRequest;
import ru.yandex.practicum.filmorate.except.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final DirectorService directorService;
    private final GenreService genreService;

    public FilmDto addFilm(NewFilmRequest filmRequest) {
        Film film = FilmMapper.mapToFilm(filmRequest);
        filmStorage.addFilm(film);
        return FilmMapper.mapToDto(film);
    }

    public FilmDto updateFilm(UpdatedFilmRequest updatedFilmRequest) {
        Film film = FilmMapper.mapToFilm(updatedFilmRequest);
        filmStorage.updateFilm(film);
        return FilmMapper.mapToDto(film);
    }

    public void deleteFilm(int filmId) {
        Film film = filmStorage.getFilmById(filmId);
        filmStorage.deleteFilm(film);
    }

    public Collection<FilmDto> findAll() {
        return filmStorage.findAll().stream().map(FilmMapper::mapToDto).collect(Collectors.toList());
    }

    public void like(int filmId, int userId) {
        User liker = userStorage.findUser(userId).orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        Film film = filmStorage.getFilmById(filmId);
        filmStorage.like(liker, film);
    }

    public void unLike(int filmId, int userId) {
        User unliker = userStorage.findUser(userId).orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        Film film = filmStorage.getFilmById(filmId);
        filmStorage.unLike(unliker, film);
    }

    public void removeAllUserLikes(int userId) {
        throw new UnsupportedOperationException();
    }

    public FilmDto getFilmById(int id) {
        return FilmMapper.mapToDto(filmStorage.getFilmById(id));
    }

    public List<FilmDto> getCommonFilms(int userId, int friendId) {
        userStorage.findUser(userId).orElseThrow(() -> {
            log.warn("GET /common: user with ID {} not found", userId);
            return new NotFoundException("User with id " + userId + " not found");
        });
        userStorage.findUser(friendId).orElseThrow(() -> {
            log.warn("GET /common: friend with ID {} not found", friendId);
            return new NotFoundException("User with id " + friendId + " not found");
        });
        List<FilmDto> commonFilms = filmStorage.getCommonFilms(userId, friendId).stream()
                .map(FilmMapper::mapToDto)
                .collect(Collectors.toList());
        log.info("GET /common: found {} common films for users {} and {}", commonFilms.size(), userId, friendId);
        return commonFilms;
    }

    public List<FilmDto> getFilmsByDirector(int directorId, String sortBy) {
        directorService.getDirectorById(directorId);
        if (!sortBy.equals("year") && !sortBy.equals("likes")) {
            throw new IllegalArgumentException("Sort parameter must be 'year' or 'likes'");
        }
        return filmStorage.getFilmsByDirector(directorId, sortBy).stream()
                .map(FilmMapper::mapToDto)
                .toList();
    }

    public List<FilmDto> getPopularFilmsByParameters(Integer genreId, Integer year, int count) {
        if (genreId != null) {
            genreService.getGenreById(genreId);
        }
        return filmStorage.getPopularFilmsByParameters(genreId, year, count).stream()
                .map(FilmMapper::mapToDto)
                .toList();
    }

    public List<FilmDto> searchFilms(String query, String by) {
        String[] searchTypes = by.split(",");
        List<Film> films = filmStorage.searchFilms(query, searchTypes);
        return films.stream()
                .map(FilmMapper::mapToDto)
                .collect(Collectors.toList());
    }
}