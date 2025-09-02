package ru.yandex.practicum.filmorate.except;

public class DuplicateException extends RuntimeException {
    public DuplicateException(String message) {
        super(message);
    }
}
