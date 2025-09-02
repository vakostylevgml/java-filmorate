package ru.yandex.practicum.filmorate.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FilmReleaseDateValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface FilmReleaseConstraint {
    String message() default "Release date should be after 28 dec 1895";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}