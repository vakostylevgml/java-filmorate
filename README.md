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


