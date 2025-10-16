package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.director.NewDirectorRequest;
import ru.yandex.practicum.filmorate.dto.director.UpdatedDirectorRequest;
import ru.yandex.practicum.filmorate.model.Director;

public final class DirectorMapper {
    public static Director mapToDirector(NewDirectorRequest request) {
        return Director.builder()
                .name(request.getName())
                .build();
    }

    public static Director mapToDirector(UpdatedDirectorRequest request) {
        return Director.builder()
                .id(request.getId())
                .name(request.getName())
                .build();
    }

    public static DirectorDto mapToDto(Director director) {
        return DirectorDto.builder()
                .id(director.getId())
                .name(director.getName())
                .build();
    }
}