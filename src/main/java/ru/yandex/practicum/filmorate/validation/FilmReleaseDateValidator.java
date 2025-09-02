package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class FilmReleaseDateValidator implements
        ConstraintValidator<FilmReleaseConstraint, LocalDate> {
    private static final LocalDate MIN_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public boolean isValid(LocalDate localDate,
      ConstraintValidatorContext cxt) {
        return localDate != null && localDate.isAfter(MIN_DATE);
    }

}