package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.genre.GenreRepository;
import ru.yandex.practicum.filmorate.except.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Service
public class GenreService {
    private final GenreRepository genreRepository;

    @Autowired
    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public Genre getGenreById(int id) {
        return genreRepository.getGenreById(id).orElseThrow(() ->
                new NotFoundException("Genre with ID = " + id + "doesn't exist"));
    }

    public List<Genre> findAll() {
        return genreRepository.findAll().stream().toList();
    }
}