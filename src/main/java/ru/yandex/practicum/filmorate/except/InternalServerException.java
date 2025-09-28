package ru.yandex.practicum.filmorate.except;

public class InternalServerException extends RuntimeException {
    public InternalServerException(String message) {
        super(message);
    }
}