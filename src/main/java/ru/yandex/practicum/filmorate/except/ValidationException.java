package ru.yandex.practicum.filmorate.except;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
