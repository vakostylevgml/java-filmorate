package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UsersControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void testGetBasePathOk() {
        Assertions.assertTrue(this.restTemplate
                .getForEntity("http://localhost:" + port + "/users", String.class)
                .getStatusCode()
                .is2xxSuccessful());
    }

    @Test
    void testPostUserOk() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin("test");
        user.setBirthday(LocalDate.of(1990, 1, 1));


        ResponseEntity<User> response = this.restTemplate.postForEntity("http://localhost:" + port + "/users", user,
                User.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(user.getLogin(), response.getBody().getLogin());
    }

    @Test
    void testUpdateUserOk() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin("test");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        ResponseEntity<User> response = this.restTemplate
                .postForEntity("http://localhost:" + port + "/users", user, User.class);
        long id = response.getBody().getId();
        user.setId(id);
        user.setLogin("updatedl");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<User> requestEntity = new HttpEntity<>(user, headers);
        ResponseEntity<User> responseUpdates = restTemplate.exchange(
                "http://localhost:" + port + "/users",
                HttpMethod.PUT,
                requestEntity,
                User.class);
        Assertions.assertEquals(HttpStatus.OK, responseUpdates.getStatusCode());
        Assertions.assertEquals(user.getLogin(), responseUpdates.getBody().getLogin());
    }

    @Test
    void testUpdateUserWhichWasntAddedFail() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin("test");
        user.setBirthday(LocalDate.of(1990, 1, 1));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<User> requestEntity = new HttpEntity<>(user, headers);
        ResponseEntity<User> responseUpdates = restTemplate.exchange(
                "http://localhost:" + port + "/users",
                HttpMethod.PUT,
                requestEntity,
                User.class);
        Assertions.assertEquals(HttpStatus.NOT_FOUND, responseUpdates.getStatusCode());
    }

    @Test
    void testPostUserWrongLoginFail() {
        User user = new User();
        user.setEmail("test@test.com");
        user.setLogin("l o g i n");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        ResponseEntity<User> response = this.restTemplate.postForEntity("http://localhost:" + port + "/users",
                user, User.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testPostUserBadEmailFail() {
        User user = new User();
        user.setEmail("tecom");
        user.setLogin("l");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        ResponseEntity<User> response = this.restTemplate.postForEntity("http://localhost:" + port + "/users",
                user, User.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testPostUserBornInFutureFail() {
        User user = new User();
        user.setEmail("tec@com.ru");
        user.setLogin("l");
        user.setBirthday(LocalDate.of(2030, 1, 1));
        ResponseEntity<User> response = this.restTemplate.postForEntity("http://localhost:" + port + "/users",
                user, User.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}

