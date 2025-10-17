package ru.yandex.practicum.filmorate.dal.review;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.BaseRepository;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

@Repository
public class ReviewRepository extends BaseRepository<Review> {
    private static final String INSERT_REVIEW = "INSERT INTO reviews (user_id, film_id, content, ispositive) " +
            " VALUES (?, ?, ?, ?)";
    private static final String UPDATE = "UPDATE reviews SET content = ?, ispositive = ? WHERE id = ?";
    private static final String DELETE_REVIEW = "DELETE FROM reviews WHERE id = ?";

    private static final String GET_REVIEW_BY_ID = """
            SELECT r.*, SUM(rld.like_dislike) AS ld FROM REVIEWS AS r
            LEFT JOIN REVIEW_LIKES_DISLIKES AS rld
            ON rld.review_id = r.id
            GROUP BY r.id
            HAVING r.id = ?;
            """;
    private static final String GET_ALL_REVIEWS_BY_FILM_ID = """
            SELECT r.*, SUM(rld.like_dislike) AS ld FROM REVIEWS AS r
            LEFT JOIN REVIEW_LIKES_DISLIKES AS rld
            ON rld.review_id = r.id
            GROUP BY r.id
            HAVING r.film_id = ?
            ORDER BY ld DESC
            LIMIT ?;
            """;

    private static final String GET_ALL_REVIEWS = """
            SELECT r.*, SUM(rld.like_dislike) AS ld FROM REVIEWS AS r
            LEFT JOIN REVIEW_LIKES_DISLIKES AS rld
            ON rld.review_id = r.id
            GROUP BY r.id
            ORDER BY ld DESC;
            """;

    private static final String INSERT_LIKE = "MERGE INTO review_likes_dislikes (user_id, review_id, like_dislike) " +
            " VALUES (?, ?, '1')";

    private static final String INSERT_DISLIKE = "MERGE INTO review_likes_dislikes (user_id, review_id, like_dislike) " +
            " VALUES (?, ?, '-1')";

    private static final String DELETE_LIKE_DISLIKE = "DELETE FROM review_likes_dislikes WHERE user_id = ? " +
            "AND review_id = ?";

    public ReviewRepository(JdbcTemplate jdbc, RowMapper<Review> mapper, ResultSetExtractor<List<Review>> listExtractor) {
        super(jdbc, mapper, listExtractor);
    }

    public Optional<Review> getReviewById(int id) {
        return findOne(GET_REVIEW_BY_ID, id);
    }

    public List<Review> getReviewsByFilmId(int id, int limit) {
        return findMany(GET_ALL_REVIEWS_BY_FILM_ID, id, limit);
    }

    public List<Review> getAllReviews() {
        return findMany(GET_ALL_REVIEWS);
    }

    public Review createReview(Review review) {
        int id = insert(
                INSERT_REVIEW,
                review.getUserId(),
                review.getFilmId(),
                review.getContent(),
                review.isPositive() ? 1 : 0);
        review.setId(id);
        return review;
    }

    public Review updateReview(Review review) {
        update(UPDATE,
                review.getContent(),
                review.isPositive() ? 1 : 0,
                review.getId());
        return review;
    }

    public void deleteReview(int id) {
        delete(DELETE_REVIEW, id);
    }

    public void addLikeToReview(int userId, int reviewId) {
        update(INSERT_LIKE, userId, reviewId);
    }

    public void addDislikeToReview(int userId, int reviewId) {
        update(INSERT_DISLIKE, userId, reviewId);
    }

    public void removeLikeDislikeFromReview(int userId, int reviewId) {
        delete(DELETE_LIKE_DISLIKE, userId, reviewId);
    }

}