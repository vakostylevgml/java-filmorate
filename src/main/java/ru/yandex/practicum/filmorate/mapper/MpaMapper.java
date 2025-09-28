package ru.yandex.practicum.filmorate.mapper;

import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.model.MpaRating;

public final class MpaMapper {
    public static MpaDto mapToDto(MpaRating rating) {
        return MpaDto.builder().id(rating.getId()).name(rating.getName()).build();
    }

    public static MpaRating mapToRating(MpaDto dto) {
        return MpaRating.builder().id(dto.getId()).name(dto.getName()).build();
    }
}