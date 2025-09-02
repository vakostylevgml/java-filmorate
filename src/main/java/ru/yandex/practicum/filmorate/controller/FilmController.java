package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.except.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private static long ids = 0;
    private static final Map<Long, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Find all films");
        return films.values();
    }

    @PostMapping
    public Film saveFilm(@Valid @RequestBody Film film) {
        log.info("Save a film: {}", film);
        if (films.containsKey(film.getId())) {
            throw new ValidationException("Film with id " + film.getId() + " already exists");
        }
        film.setId(++ids);
        films.put(film.getId(), film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Update a film: {}", film);
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("Film with id " + film.getId() + " doesn't exist");
        }
        films.put(film.getId(), film);
        return film;
    }
}
