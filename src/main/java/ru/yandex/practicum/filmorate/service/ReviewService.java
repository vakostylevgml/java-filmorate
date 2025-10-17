package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.review.ReviewStorage;
import ru.yandex.practicum.filmorate.dto.review.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dto.review.UpdatedReviewRequest;
import ru.yandex.practicum.filmorate.except.NotFoundException;
import ru.yandex.practicum.filmorate.except.ValidationException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public ReviewDto getReviewById(int id) {
        return reviewStorage.getReviewById(id).map(ReviewMapper::mapToDto).orElseThrow(() ->
                new NotFoundException("Review with id + " + id + "not found"));
    }

    public List<ReviewDto> getReviewsByFilmId(int id) {
        Film film = filmStorage.getFilmById(id);
        return reviewStorage.getReviewsByFilmId(id).stream().map(ReviewMapper::mapToDto).toList();
    }

    public ReviewDto createReview(NewReviewRequest request) {
        Film film = filmStorage.getFilmById(request.getFilmId());
        User user = userStorage.findUser(request.getUserId()).orElseThrow(() ->
                new NotFoundException("Couldn't add review from unexisting user with id = " + request.getUserId()));
        Review review = reviewStorage.createReview(ReviewMapper.mapToReview(request));
        return ReviewMapper.mapToDto(review);
    }

    public ReviewDto updateReview(UpdatedReviewRequest request) {
        Film film = filmStorage.getFilmById(request.getFilmId());
        User user = userStorage.findUser(request.getUserId()).orElseThrow(() ->
                new NotFoundException("Couldn't add review from unexisting user with id = " + request.getUserId()));
        Review review = reviewStorage.createReview(ReviewMapper.mapToReview(request));
        return ReviewMapper.mapToDto(reviewStorage.updateReview(ReviewMapper.mapToReview(request)));
    }

    public void deleteReview(int id) {
        Review review = reviewStorage.getReviewById(id).orElseThrow(() ->
                new ValidationException("Couldn't delete unexisting review with id = " + id));
        reviewStorage.deleteReview(id);
    }

    public void addLikeToReview(int userId, int reviewId) {
        User user = userStorage.findUser(userId).orElseThrow(() ->
                new NotFoundException("Couldn't add review from unexisting user with id = " + userId));
        Review review = reviewStorage.getReviewById(reviewId).orElseThrow(() ->
                new NotFoundException("Couldn't add like unexisting review with id = " + reviewId));
        reviewStorage.addLikeToReview(userId, reviewId);
    }

    public void addDislikeToReview(int userId, int reviewId) {
        User user = userStorage.findUser(userId).orElseThrow(() ->
                new NotFoundException("Couldn't add review from unexisting user with id = " + userId));
        Review review = reviewStorage.getReviewById(reviewId).orElseThrow(() ->
                new NotFoundException("Couldn't add dislike unexisting review with id = " + reviewId));
        reviewStorage.addDislikeToReview(userId, reviewId);
    }

    public void removeLikeDislikeFromReview(int userId, int reviewId) {
        User user = userStorage.findUser(userId).orElseThrow(() ->
                new NotFoundException("Couldn't add review from unexisting user with id = " + userId));
        Review review = reviewStorage.getReviewById(reviewId).orElseThrow(() ->
                new NotFoundException("Couldn't remove like/dislike unexisting review with id = " + reviewId));
        reviewStorage.removeLikeDislikeFromReview(userId, reviewId);
    }

}