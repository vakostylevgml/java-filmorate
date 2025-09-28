package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdatedFilmRequest;
import ru.yandex.practicum.filmorate.except.BadRequestException;
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
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private MpaService mpaService;
    private GenreService genreService;

    @Autowired
    public FilmService(FilmStorage filmStorage,
                       UserStorage userStorage,
                       MpaService mpaService,
                       GenreService genreService) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.mpaService = mpaService;
        this.genreService = genreService;
        FilmMapper mapper = new FilmMapper(mpaService, genreService);
    }

    public FilmDto addFilm(NewFilmRequest filmRequest) {
        try {
            Film film = FilmMapper.mapToFilm(filmRequest);
            filmStorage.addFilm(film);
            return FilmMapper.mapToDto(film);
        } catch (
                NotFoundException notFoundException) {
            throw new BadRequestException(notFoundException.getMessage());
        }
    }

    public FilmDto updateFilm(UpdatedFilmRequest updatedFilmRequest) {
        try {
            Film film = FilmMapper.mapToFilm(updatedFilmRequest);
            filmStorage.updateFilm(film);
            return FilmMapper.mapToDto(film);
        } catch (
                NotFoundException notFoundException) {
            throw new BadRequestException(notFoundException.getMessage());
        }
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

    public List<FilmDto> getMostLiked(int count) {
        return filmStorage.getMostLiked(count).stream().map(FilmMapper::mapToDto).toList();
    }

    public void removeAllUserLikes(int userId) {
        throw new UnsupportedOperationException();
    }

    public FilmDto getFilmById(int id) {
        return FilmMapper.mapToDto(filmStorage.getFilmById(id));
    }
}