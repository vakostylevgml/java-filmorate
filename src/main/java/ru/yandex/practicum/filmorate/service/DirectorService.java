package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.director.DirectorRepository;
import ru.yandex.practicum.filmorate.dto.director.DirectorDto;
import ru.yandex.practicum.filmorate.dto.director.NewDirectorRequest;
import ru.yandex.practicum.filmorate.dto.director.UpdatedDirectorRequest;
import ru.yandex.practicum.filmorate.except.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorRepository directorRepository;

    public DirectorDto getDirectorById(int id) {
        return directorRepository.getDirectorById(id)
                .map(DirectorMapper::mapToDto)
                .orElseThrow(() -> new NotFoundException("Director with id " + id + " not found"));
    }

    public List<DirectorDto> findAll() {
        return directorRepository.findAll().stream()
                .map(DirectorMapper::mapToDto)
                .toList();
    }

    public DirectorDto addDirector(NewDirectorRequest request) {
        Director director = DirectorMapper.mapToDirector(request);
        directorRepository.addDirector(director);
        return DirectorMapper.mapToDto(director);
    }

    public DirectorDto updateDirector(UpdatedDirectorRequest request) {
        directorRepository.getDirectorById(request.getId())
                .orElseThrow(() -> new NotFoundException("Director with id " + request.getId() + " not found"));

        Director director = DirectorMapper.mapToDirector(request);
        directorRepository.updateDirector(director);
        return DirectorMapper.mapToDto(director);
    }

    public void deleteDirector(int id) {
        directorRepository.getDirectorById(id)
                .orElseThrow(() -> new NotFoundException("Director with id " + id + " not found"));
        if (directorRepository.isDirectorUsedInFilms(id)) {
            throw new IllegalStateException("Cannot delete director - used in films");
        }
        directorRepository.deleteDirector(id);
    }
}