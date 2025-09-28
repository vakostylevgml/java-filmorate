package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mpa")
public class RatingController {
    private final MpaService mpaService;

    @Autowired
    public RatingController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping
    public List<MpaDto> findAll() {
        return mpaService.findAll();
    }

    @GetMapping("/{mpaId}")
    public MpaDto getMpaRating(@PathVariable("mpaId") int mpaId) {
        return mpaService.getRatingById(mpaId);
    }
}