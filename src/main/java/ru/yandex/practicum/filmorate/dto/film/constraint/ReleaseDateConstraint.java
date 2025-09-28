package ru.yandex.practicum.filmorate.dto.film.constraint;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ReleaseDateValidator.class)
@Target({ ElementType.METHOD, ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ReleaseDateConstraint {
    String message() default "Invalid release date";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}