package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdatedFilmRequest;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@Validated
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public Collection<FilmDto> findAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public FilmDto getFilm(@PathVariable("id") int filmId) {
        return filmService.getFilmById(filmId);
    }

    @PostMapping
    public FilmDto create(@Valid @RequestBody NewFilmRequest newFilm) {
        return filmService.addFilm(newFilm);
    }

    @PutMapping
    public FilmDto update(@Valid @RequestBody UpdatedFilmRequest updatedFilm) {
        return filmService.updateFilm(updatedFilm);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") @Positive int filmId) {
        log.info("Delete film with id {}", filmId);
        filmService.deleteFilm(filmId);
    }

    @PutMapping("/{id}/like/{userId}")
    public void like(@PathVariable("id") int filmId, @PathVariable("userId") int userId) {
        filmService.like(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void unLike(@PathVariable("id") int filmId, @PathVariable("userId") int userId) {
        log.info("UNKIKE CALL");
        filmService.unLike(filmId, userId);
    }

    @GetMapping("/director/{directorId}")
    public List<FilmDto> getFilmsByDirector(
            @PathVariable("directorId") int directorId,
            @RequestParam String sortBy) {
        return filmService.getFilmsByDirector(directorId, sortBy);
    }

    @GetMapping("/popular")
    public List<FilmDto> getPopular(@RequestParam(defaultValue = "10") @Positive(message = "Count must be positive") int count,
                                    @RequestParam(required = false) @Positive(message = "GenreId must be positive") Integer genreId,
                                    @RequestParam(required = false) @Positive(message = "Year must be positive") Integer year) {
        return filmService.getPopularFilmsByParameters(genreId, year, count);
    }
}