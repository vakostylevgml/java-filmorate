package ru.yandex.practicum.filmorate.dal.review;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ReviewListResultSetExtractor implements ResultSetExtractor<List<Review>> {
    @Override
    public List<Review> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<Review> result = new ArrayList<>();
        while (rs.next()) {
            Review review = Review.builder()
                    .id(rs.getInt("id"))
                    .userId(rs.getInt("user_id"))
                    .filmId(rs.getInt("film_id"))
                    .content(rs.getString("content"))
                    .isPositive(rs.getInt("ispositive") == 1)
                    .useful(rs.getInt("ld"))
                    .build();
            result.add(review);
        }
        return result;
    }
}