package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FIlmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FIlmService filmService;
    private final UserService userService;

    @Autowired
    public FilmController(FIlmService filmService, UserService userService) {
        this.filmService = filmService;
        this.userService = userService;
    }

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Find all films");
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film findFilm(@PathVariable long id) {
        log.info("Get film with id {} ", id);
        return filmService.findFilm(id);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable long id) {
        log.info("Delete film with id {} ", id);
        filmService.deleteFilm(id);
    }

    @PostMapping
    public Film saveFilm(@Valid @RequestBody Film film) {
        log.info("Save a film: {}", film);
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.info("Update a film: {}", film);
        return filmService.updateFilm(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void like(@PathVariable long id, @PathVariable long userId) {
        log.info("User with id {} liked movie with id {}", userId, id);
        User user = userService.findUser(userId);
        Film film = filmService.findFilm(id);
        filmService.likeFilm(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void unlike(@PathVariable long id, @PathVariable long userId) {
        log.info("User with id {} unliked movie with id {}", userId, id);
        User user = userService.findUser(userId);
        Film film = filmService.findFilm(id);
        filmService.unlikeFilm(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> findMostPop(@RequestParam(required = false) int count) {
        log.info("Mostpop requested");
        if (count == 0) {
            return filmService.getMostPopular();
        } else {
            return filmService.getMostPopular(count);
        }
    }
}
