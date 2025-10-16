package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.director.NewDirectorRequest;
import ru.yandex.practicum.filmorate.dto.director.UpdatedDirectorRequest;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@RestController
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    @Autowired
    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @GetMapping
    public List<DirectorDto> findAll() {
        return directorService.findAll();
    }

    @GetMapping("/{id}")
    public DirectorDto getDirector(@PathVariable("id") int id) {
        return directorService.getDirectorById(id);
    }

    @PostMapping
    public DirectorDto create(@Valid @RequestBody NewDirectorRequest request) {
        return directorService.addDirector(request);
    }

    @PutMapping
    public DirectorDto update(@Valid @RequestBody UpdatedDirectorRequest request) {
        return directorService.updateDirector(request);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") int id) {
        directorService.deleteDirector(id);
    }
}