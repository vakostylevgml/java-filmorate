package ru.yandex.practicum.filmorate;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Sprint 11 todo's
 * @TODO filmstorage
 * @TODO userstorage
 *
 * @TODO userservice: add, delete, findAll
 * @TODO filmservice: like, mostpop
 *
 * @TODO inject
 * @TODO full rest
 * @TODO logging
 */

@SpringBootApplication
public class FilmorateApplication {
	public static void main(String[] args) {
		SpringApplication.run(FilmorateApplication.class, args);
	}

}
