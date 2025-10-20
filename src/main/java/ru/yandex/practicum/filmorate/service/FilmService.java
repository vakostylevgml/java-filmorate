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
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.OperationType;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;
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
        //request film to check it existence, if film doesn't found NFE will be thrown
        Film existing = filmStorage.getFilmById(updatedFilmRequest.getId());

        //perform update
        filmStorage.updateFilm(FilmMapper.mapToFilm(updatedFilmRequest));

        //return updated
        return FilmMapper.mapToDto(filmStorage.getFilmById(updatedFilmRequest.getId()));
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
        userStorage.addEvent(userId, film.getId(), EventType.LIKE, OperationType.ADD);
    }

    public void unLike(int filmId, int userId) {
        User unliker = userStorage.findUser(userId).orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
        Film film = filmStorage.getFilmById(filmId);
        filmStorage.unLike(unliker, film);
        userStorage.addEvent(userId, film.getId(), EventType.LIKE, OperationType.REMOVE);
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

    public List<FilmDto> getRecommendations(int userId) {
        userStorage.findUser(userId).orElseThrow(() ->
                new NotFoundException("User with id " + userId + " not found")
        );

        Set<Integer> targetLiked = filmStorage.getLikedFilmIdsByUser(userId);

        if (targetLiked.isEmpty()) {
            return List.of();
        }

        List<User> allUsers = userStorage.findAll().stream()
                .filter(user -> user.getId() != userId)
                .toList();

        if (allUsers.isEmpty()) {
            return List.of();
        }

        Map<Integer, Integer> commonCount = new HashMap<>();
        for (User other : allUsers) {
            Set<Integer> otherLiked = filmStorage.getLikedFilmIdsByUser(other.getId());
            int common = (int) targetLiked.stream().filter(otherLiked::contains).count();
            if (common > 0) {
                commonCount.put(other.getId(), common);
            }
        }

        if (commonCount.isEmpty()) {
            return List.of();
        }

        int maxCommon = Collections.max(commonCount.values());

        Set<Integer> similarUserIds = commonCount.entrySet().stream()
                .filter(e -> e.getValue() == maxCommon)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        Set<Integer> recommendedFilmIds = new HashSet<>();
        for (int similarId : similarUserIds) {
            Set<Integer> likedBySimilar = filmStorage.getLikedFilmIdsByUser(similarId);
            likedBySimilar.removeAll(targetLiked); // убираем уже лайкнутые
            recommendedFilmIds.addAll(likedBySimilar);
        }

        return recommendedFilmIds.stream()
                .map(id -> filmStorage.getFilmById(id))
                .map(FilmMapper::mapToDto)
                .collect(Collectors.toList());
    }
}