CREATE TABLE IF NOT EXISTS users (
                                     id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                     email VARCHAR(255) NOT NULL,
    login VARCHAR(255) NOT NULL,
    password VARCHAR(40),
    name VARCHAR(255) NOT NULL,
    birthday TIMESTAMP WITH TIME ZONE NOT NULL);

create unique index if not exists USER_EMAIL_UINDEX on USERS (email);
create unique index if not exists USER_LOGIN_UINDEX on USERS (login);


CREATE TABLE IF NOT EXISTS rating (
                                      id INTEGER PRIMARY KEY,
                                      name VARCHAR(50) NOT NULL);

CREATE TABLE IF NOT EXISTS genre (
                                     id INTEGER PRIMARY KEY,
                                     name VARCHAR(50) NOT NULL);

CREATE TABLE IF NOT EXISTS directors (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL);

CREATE TABLE IF NOT EXISTS films (
                                     id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                     name VARCHAR(255) NOT NULL,
    description VARCHAR(200) NOT NULL,
    release_date TIMESTAMP WITH TIME ZONE NOT NULL,
                               duration INTEGER NOT NULL,
                               rating_id INTEGER REFERENCES rating (id) ON DELETE RESTRICT);

CREATE TABLE IF NOT EXISTS likes (
                                     user_id INTEGER REFERENCES users (id) ON DELETE RESTRICT,
    film_id INTEGER REFERENCES films (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, film_id));

CREATE TABLE IF NOT EXISTS film_genre (
                                          film_id INTEGER REFERENCES films (id) ON DELETE CASCADE,
    genre_id INTEGER REFERENCES genre (id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, genre_id));

CREATE TABLE IF NOT EXISTS friends (
                                       user_id_1 INTEGER REFERENCES users (id) ON DELETE RESTRICT,
    user_id_2 INTEGER REFERENCES users (id) ON DELETE RESTRICT);

CREATE TABLE IF NOT EXISTS film_director (
    film_id INTEGER REFERENCES films (id) ON DELETE CASCADE,
    director_id INTEGER REFERENCES directors (id) ON DELETE CASCADE,
    PRIMARY KEY (film_id, director_id)
);

CREATE TABLE IF NOT EXISTS reviews (
                                       id INTEGER PRIMARY KEY AUTO_INCREMENT,
                                       user_id INTEGER REFERENCES users(id),
    film_id INTEGER REFERENCES films(id),
    content VARCHAR(255),
    isPositive integer
    );

CREATE TABLE IF NOT EXISTS review_likes_dislikes (
    user_id INTEGER REFERENCES users(id) ON DELETE CASCADE,
    review_id INTEGER REFERENCES reviews(id) ON DELETE CASCADE,
    like_dislike INTEGER,
    PRIMARY KEY (user_id, review_id)
    );