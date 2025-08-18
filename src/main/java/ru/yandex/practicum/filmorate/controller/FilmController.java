package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@RestController
@RequestMapping("/films")
public class FilmController {
    private static Map<Long, Film> films = new HashMap<>();

    static {
        Film film1 = new Film();
        film1.setId(1);
        film1.setName("Film 1");
        Film film2 = new Film();
        film2.setId(2);
        film2.setName("Film 2");
        films.put(1L, film1);
        films.put(2L, film2);
    }

    @GetMapping
    public String findAll() {
        return films.values().toString();
    }

    @GetMapping(path = "/{id}")
    public String getFilmById(@PathVariable Long id) {
        return films.get(id).toString();
    }



}
