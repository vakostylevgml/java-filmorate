package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FilmsControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void testGetBasePathOk() throws Exception {
        Assertions.assertTrue(this.restTemplate
                .getForEntity("http://localhost:" + port + "/films", String.class)
                .getStatusCode()
                .is2xxSuccessful());
    }

    @Test
    void testPostFilmOk() throws Exception {
        Film film = new Film();
        film.setName("testf");
        film.setDuration(30);
        film.setReleaseDate(LocalDate.now());
        ResponseEntity<Film> response = this.restTemplate.postForEntity("http://localhost:" + port + "/films", film, Film.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(film.getName(), response.getBody().getName());
    }

    @Test
    void testUpdateFilmOk() throws Exception {
        Film film = new Film();
        film.setName("testf");
        film.setDuration(30);
        film.setReleaseDate(LocalDate.now());
        ResponseEntity<Film> response = this.restTemplate
                .postForEntity("http://localhost:" + port + "/films", film, Film.class);
        long id = response.getBody().getId();
        film.setId(id);
        film.setName("updated");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Film> requestEntity = new HttpEntity<>(film, headers);
        ResponseEntity<Film> responseUpdates = restTemplate.exchange(
                "http://localhost:" + port + "/films",
                HttpMethod.PUT,
                requestEntity,
                Film.class);
        Assertions.assertEquals(HttpStatus.OK, responseUpdates.getStatusCode());
        Assertions.assertEquals(film.getName(), responseUpdates.getBody().getName());
    }

}
