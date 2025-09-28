package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.mpa.MpaRepository;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.except.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaRepository mpaRepository;

    public MpaDto getRatingById(int id) {
        return mpaRepository.getRatingById(id).map(MpaMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("MPA with ID = " + id + "doesn't exist"));
    }

    public List<MpaDto> findAll() {
        return mpaRepository.findAll().stream().map(MpaMapper::mapToDto).toList();
    }
}