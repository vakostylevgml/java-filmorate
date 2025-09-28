package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.GenreId;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdatedFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.MpaRating;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.Arrays;
import java.util.LinkedHashSet;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Component
public final class FilmMapper {
    private static MpaService mpaService;
    private static GenreService genreService;

    @Autowired
    public FilmMapper(MpaService mpaS, GenreService genreS) {
        mpaService = mpaS;
        genreService = genreS;
    }

    public static Film mapToFilm(NewFilmRequest request) {
        Film film = Film.builder()
                .name(request.getName())
                .description(request.getDescription())
                .releaseDate(request.getReleaseDate())
                .duration(request.getDuration())
                .build();

        if (request.getMpa() != null) {
            log.info("Mpa is present for {}", request.getName());
            MpaRating mpaRating = MpaMapper.mapToRating(mpaService.getRatingById(request.getMpa().getId()));
            film.setMpa(mpaRating);
            log.info("MPA set = {}", film.getMpa().getId());
        }

        if (request.getGenres() != null && !request.getGenres().isEmpty()) {
            log.info("Genre is present for {}", request.getName());
            LinkedHashSet<Genre> genreSet = new LinkedHashSet<>();
            for (int genreId : request.getGenres().stream().mapToInt(GenreId::getId).toArray()) {
                Genre genre = genreService.getGenreById(genreId);
                genreSet.add(genre);
            }
            film.setGenres(genreSet);
            log.info("Genres are set = {}", Arrays.asList(film.getGenres().toArray()));
        } else {
            log.info("Genres not set for {}, size = {}", request.getName(), film.getGenres().size());
        }
        return film;
    }

    public static Film mapToFilm(UpdatedFilmRequest request) {
        Film film = Film.builder()
                .id(request.getId())
                .name(request.getName())
                .description(request.getDescription())
                .releaseDate(request.getReleaseDate())
                .duration(request.getDuration())
                .build();

        if (request.getMpa() != null) {
            MpaRating mpaRating = MpaMapper.mapToRating(mpaService.getRatingById(request.getMpa().getId()));
            film.setMpa(mpaRating);
        }

        if (request.getGenres() != null && !request.getGenres().isEmpty()) {
            LinkedHashSet<Genre> genreSet = new LinkedHashSet<>();
            for (int genreId : request.getGenres().stream().mapToInt(GenreId::getId).toArray()) {
                Genre genre = genreService.getGenreById(genreId);
                genreSet.add(genre);
            }
            film.setGenres(genreSet);
        }
        return film;
    }

    public static FilmDto mapToDto(Film film) {
        FilmDto fIlmDto = FilmDto.builder()
                .id(film.getId())
                .name(film.getName())
                .description(film.getDescription())
                .releaseDate(film.getReleaseDate())
                .duration(film.getDuration()).build();

        if (film.getMpa() != null) {
            fIlmDto.setMpa(MpaMapper.mapToDto(film.getMpa()));
        }

        if (!film.getGenres().isEmpty()) {
            fIlmDto.setGenres(film.getGenres());
        }

        return fIlmDto;
    }
}