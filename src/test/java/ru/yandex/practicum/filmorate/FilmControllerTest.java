package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.web.util.UriComponentsBuilder;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.net.URI;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FilmControllerTest {
    String baseUrl;
    HttpHeaders headers;
    URI uri;

    @Autowired
    private FilmController controller;

    @Autowired
    private UserService userService;

    @Autowired
    private FilmService filmService;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;


    @Test
    void contextLoads() {
        assertThat(controller).isNotNull();
    }

    @BeforeAll
    void setUp() throws Exception {
        baseUrl = "http://localhost:" + port + "/films";
        headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        uri = new URI(baseUrl);
    }

    @Test
    void emptyFilmIsNotAdded() {
        HttpEntity<String> request = new HttpEntity<>("", headers);
        Assertions.assertTrue(this.restTemplate.postForEntity(uri, request, String.class).getStatusCode().is5xxServerError());
    }

    @Test
    void filmWithWrongReleaseDateIsNotAdded() {
        Film film = Film.builder().name("name").description("desc")
                .releaseDate(LocalDate.of(1895,12,28)).duration(1).build();
        HttpEntity<Film> request = new HttpEntity<>(film, headers);
        Assertions.assertTrue(this.restTemplate.postForEntity(uri, request, String.class).getStatusCode().is4xxClientError());
    }

    @Test
    void filmWithNoTitleIsNotAdded() {
        Film film = Film.builder().name("").description("desc")
                .releaseDate(LocalDate.of(2703,12,12)).duration(1).build();
        HttpEntity<Film> request = new HttpEntity<>(film, headers);
        Assertions.assertTrue(this.restTemplate.postForEntity(uri, request, String.class).getStatusCode().is4xxClientError());
    }

    @Test
    void filmWithNoVeryLongDescriptionIsNotAdded() {
        StringBuilder sb = new StringBuilder();
        sb.repeat("D", 201);
        Film film = Film.builder().name("name").description(sb.toString())
                .releaseDate(LocalDate.of(2703,12,12)).duration(1).build();
        HttpEntity<Film> request = new HttpEntity<>(film, headers);
        Assertions.assertTrue(this.restTemplate.postForEntity(uri, request, String.class).getStatusCode().is4xxClientError());
    }

    @Test
    void filmWithNegativeDurationIsNotAdded() {
        Film film = Film.builder().name("name").description("d")
                .releaseDate(LocalDate.of(2703,12,12)).duration(-1).build();
        HttpEntity<Film> request = new HttpEntity<>(film, headers);
        Assertions.assertTrue(this.restTemplate.postForEntity(uri, request, String.class).getStatusCode().is4xxClientError());
    }

    @Test
    void filmWithZeroDurationIsNotAdded() {
        Film film = Film.builder().name("name").description("d")
                .releaseDate(LocalDate.of(2703,12,12)).duration(0).build();
        HttpEntity<Film> request = new HttpEntity<>(film, headers);
        Assertions.assertTrue(this.restTemplate.postForEntity(uri, request, String.class).getStatusCode().is4xxClientError());
    }

    @Test
    void voidFilmUpdateNotOk() {
        HttpEntity<String> request = new HttpEntity<>("", headers);
        Assertions.assertTrue(this.restTemplate.exchange(uri, HttpMethod.PUT, request, String.class)
                .getStatusCode().is5xxServerError());
    }

    @Test
    void likeAndUnlikeUnexistingFilmIsNotOk() {
        NewUserRequest nur1 = NewUserRequest.builder().name("User 1").birthday(LocalDate.now())
                .email("user1@gmail.com").login("user1").build();

        UserDto user1 = userService.addUser(nur1);

        URI uri1 = UriComponentsBuilder
                .fromUriString(baseUrl + "/{id}/like/{userId}")
                .encode()
                .buildAndExpand(777, user1.getId())
                .toUri();
        HttpEntity<String> request = new HttpEntity<>("", headers);

        ResponseEntity<String> response = this.restTemplate.exchange(uri1, HttpMethod.PUT, request, String.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        ResponseEntity<String> response1 = this.restTemplate.exchange(uri1, HttpMethod.DELETE, request, String.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response1.getStatusCode());
    }
}