## Диаграмма БД

<img width="1108" height="779" alt="image" src="https://github.com/user-attachments/assets/351707de-9e40-4ebc-9034-c19d9089e773" />

Диаграмма по [ссылке] (https://dbdiagram.io/d/Filmorate-68c86bb61ff9c616bdcb263c).

## Примеры запросов

### Поиск фильмов с самым большим количеством лайков
`SELECT f.id,
       f.name,
       COUNT(l.user_id) AS likes_count
FROM films f
LEFT JOIN likes l ON f.id = l.film_id
GROUP BY f.id, f.name
ORDER BY likes_count DESC;
`

### Запросы на дружбу (для пользователя с id=1)
`
SELECT u.id, u.login
FROM friends f
JOIN users u ON u.id = f.friend_id
WHERE f.user_id = 1;
`

### Взаимные друзья (пользователь с id=1)
`
SELECT u.id, u.login
FROM friends f1
JOIN friends f2 
  ON f1.friend_id = f2.user_id
 AND f1.user_id = f2.friend_id
JOIN users u ON u.id = f1.friend_id
WHERE f1.user_id = 1;
`

### Все жанры (фильм с id=1)
`
SELECT g.id, g.title
FROM film_genre fg
JOIN genres g ON g.id = fg.genre_id
WHERE fg.film_id = 1;
`

