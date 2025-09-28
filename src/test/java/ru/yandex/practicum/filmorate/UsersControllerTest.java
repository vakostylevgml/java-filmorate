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
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdatedUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.net.URI;
import java.time.LocalDate;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UsersControllerTest {
    String baseUrl;
    HttpHeaders headers;
    URI uri;

    @Autowired
    private UserController controller;

    @Autowired
    private UserService service;

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    Random random = new Random();

    @Test
    void contextLoads() {
        assertThat(controller).isNotNull();
    }

    @BeforeAll
    void setUp() throws Exception {
        baseUrl = "http://localhost:" + port + "/users";
        headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        uri = new URI(baseUrl);
    }

    @Test
    void userIsAddedOk() {
        int x = random.nextInt();
        User user = User.builder().name("Name").birthday(LocalDate.now())
                .email(x + "kostylev@email.com").login(x + "kostylev").build();
        HttpEntity<User> request = new HttpEntity<>(user, headers);
        ResponseEntity<String> response = this.restTemplate.postForEntity(uri, request, String.class);
        Assertions.assertTrue(response.getStatusCode().is2xxSuccessful());
    }

    @Test
    void userIsAddedWithoutNameAndLoginIsUsed() {
        int x = random.nextInt();
        User user = User.builder().birthday(LocalDate.now())
                .email(x + "nonameuser@exampleml.com").login(x + "expl.login").build();
        HttpEntity<User> request = new HttpEntity<>(user, headers);
        Assertions.assertTrue(this.restTemplate.postForEntity(uri, request, String.class)
                .getStatusCode().is2xxSuccessful());
        Assertions.assertTrue(this.controller.findAll().stream()
                .anyMatch(u -> u.getEmail().equals(user.getEmail())));
        Assertions.assertEquals(user.getLogin(), this.controller.findAll().stream()
                .filter(u -> u.getEmail().equals(user.getEmail()))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("User not found")).getName());
    }

    @Test
    void userWithWrongEmailIsNotAdded() {
        int x = random.nextInt();
        User user = User.builder().birthday(LocalDate.now())
                .email(x + "email").login(x + "kostylev.v").build();

        HttpEntity<User> request = new HttpEntity<>(user, headers);

        Assertions.assertTrue(this.restTemplate.postForEntity(uri, request, String.class)
                .getStatusCode().is4xxClientError());
    }

    @Test
    void userWithWrongLoginIsNotAdded() {
        int x = random.nextInt();
        User user = User.builder().birthday(LocalDate.now())
                .email(x + "email@gmail.cn").login(x + "log in").build();

        HttpEntity<User> request = new HttpEntity<>(user, headers);

        Assertions.assertTrue(this.restTemplate.postForEntity(uri, request, String.class)
                .getStatusCode().is4xxClientError());
    }

    @Test
    void userBornInFutureIsNotAdded() {
        int x = random.nextInt();
        User user = User.builder().birthday(LocalDate.of(2100, 1, 1))
                .email(x + "email@gmail.ru").login(x + "mylogin").build();

        HttpEntity<User> request = new HttpEntity<>(user, headers);

        Assertions.assertTrue(this.restTemplate.postForEntity(uri, request, String.class)
                .getStatusCode().is4xxClientError());
    }

    @Test
    void userIsUpdatedOk() {
        int x = random.nextInt();
        NewUserRequest nur = NewUserRequest.builder().name("name to be changed").birthday(LocalDate.now())
                .email(x + "email@nottobechanged.com").login(x + "login-not-to-be-changed").build();
        UserDto added = this.controller.saveUser(nur);
        UpdatedUserRequest uur = UpdatedUserRequest.builder()
                .id(added.getId())
                .name("chngd name")
                .birthday(added.getBirthday())
                .email(added.getEmail())
                .login(added.getLogin()).build();
        HttpEntity<UpdatedUserRequest> request = new HttpEntity<>(uur, headers);
        Assertions.assertTrue(this.restTemplate.exchange(uri, HttpMethod.PUT, request, String.class)
                .getStatusCode().is2xxSuccessful());
        Assertions.assertEquals(uur.getName(), this.controller.findAll()
                .stream()
                .filter(f -> f.getId() == added.getId())
                .findFirst().orElseThrow(() -> new IllegalStateException("User not found"))
                .getName());
    }

    @Test
    void userUpdateWithoutIdNotOk() {
        int x = random.nextInt();
        NewUserRequest nur = NewUserRequest.builder().name("name to be changed").birthday(LocalDate.now())
                .email(x + "email@nottobechanged.com").login(x + "login-not-to-be-changed").build();
        UserDto added = this.controller.saveUser(nur);
        UpdatedUserRequest uur = UpdatedUserRequest.builder()
                .id(-1)
                .name("chngd name")
                .birthday(added.getBirthday())
                .email(added.getEmail())
                .login(added.getLogin()).build();

        HttpEntity<UpdatedUserRequest> request = new HttpEntity<>(uur, headers);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, this.restTemplate.exchange(uri, HttpMethod.PUT, request, String.class)
                .getStatusCode());
    }

    @Test
    void voidUserUpdateNotOk() {
        HttpEntity<String> request = new HttpEntity<>("", headers);
        Assertions.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, this.restTemplate.exchange(uri, HttpMethod.PUT, request, String.class)
                .getStatusCode());
    }

    @Test
    void addFriendRequestTestOk() {
        int x = random.nextInt();
        int y = random.nextInt();

        NewUserRequest nur1 = NewUserRequest.builder().name(x + "User 1").birthday(LocalDate.now())
                .email(x + "user1@gmail.com").login(x + "user1").build();
        NewUserRequest nur2 = NewUserRequest.builder().name(y + "User 2").birthday(LocalDate.now())
                .email(y + "user2@gmail.com").login(y + "user2").build();

        UserDto added1 = this.controller.saveUser(nur1);
        UserDto added2 = this.controller.saveUser(nur2);

        Assertions.assertEquals(0, service.getFriends(added1.getId()).size());
        Assertions.assertEquals(0, service.getFriends(added2.getId()).size());


        URI uri1 = UriComponentsBuilder
                .fromUriString(baseUrl + "/{id}/friends/{friendId}")
                .encode()
                .buildAndExpand(added1.getId(), added2.getId())
                .toUri();

        this.restTemplate.put(uri1, new HttpEntity<>(headers));

        Assertions.assertEquals(1, service.getFriends(added1.getId()).size());
        Assertions.assertEquals(0, service.getFriends(added2.getId()).size());

        URI uri2 = UriComponentsBuilder
                .fromUriString(baseUrl + "/{id}/friends/{friendId}")
                .encode()
                .buildAndExpand(added2.getId(), added1.getId())
                .toUri();

        this.restTemplate.put(uri2, new HttpEntity<>(headers));

        Assertions.assertEquals(1, service.getFriends(added1.getId()).size());
        Assertions.assertEquals(1, service.getFriends(added2.getId()).size());
    }

    @Test
    void deleteFriendsFromUnknownIdNotOk() {
        int x = random.nextInt();

        NewUserRequest nur = NewUserRequest.builder().name(x + "User 1").birthday(LocalDate.now())
                .email(x + "user1@gmail.com").login(x + "user1").build();

        UserDto added1 = this.controller.saveUser(nur);

        URI uri = UriComponentsBuilder
                .fromUriString(baseUrl + "/{id}/friends/{friendId}")
                .encode()
                .buildAndExpand(added1.getId(), 999999)
                .toUri();

        HttpEntity<String> request = new HttpEntity<>("", headers);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, this.restTemplate.exchange(uri, HttpMethod.DELETE, request,
                String.class).getStatusCode());

    }

    @Test
    void deleteFriendWithUnknownIdNotOk() {
        int x = random.nextInt();

        NewUserRequest nur = NewUserRequest.builder().name(x + "User 1").birthday(LocalDate.now())
                .email(x + "user1@gmail.com").login(x + "user1").build();

        UserDto added1 = this.controller.saveUser(nur);

        URI uri = UriComponentsBuilder
                .fromUriString(baseUrl + "/{id}/friends/{friendId}")
                .encode()
                .buildAndExpand(999999, added1.getId())
                .toUri();

        HttpEntity<String> request = new HttpEntity<>("", headers);

        Assertions.assertEquals(HttpStatus.NOT_FOUND, this.restTemplate.exchange(uri, HttpMethod.DELETE, request,
                String.class).getStatusCode());
    }
}