package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.review.ReviewRepository;
import ru.yandex.practicum.filmorate.dto.review.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dto.review.UpdatedReviewRequest;
import ru.yandex.practicum.filmorate.except.NotFoundException;
import ru.yandex.practicum.filmorate.except.ValidationException;
import ru.yandex.practicum.filmorate.mapper.ReviewMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.event.EventType;
import ru.yandex.practicum.filmorate.model.event.OperationType;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public ReviewDto getReviewById(int id) {
        return reviewRepository.getReviewById(id).map(ReviewMapper::mapToDto).orElseThrow(() ->
                new NotFoundException("Review with id + " + id + "not found"));
    }

    public List<ReviewDto> getReviewsByFilmId(int id, int limit) {
        Film film = filmStorage.getFilmById(id);
        return reviewRepository.getReviewsByFilmId(id, limit).stream().map(ReviewMapper::mapToDto).toList();
    }

    public List<ReviewDto> getAllReviews() {
        return reviewRepository.getAllReviews().stream().map(ReviewMapper::mapToDto).toList();
    }

    public ReviewDto createReview(NewReviewRequest request) {
        Film film = filmStorage.getFilmById(request.getFilmId());
        User user = userStorage.findUser(request.getUserId()).orElseThrow(() ->
                new NotFoundException("Couldn't add review from unexisting user with id = " + request.getUserId()));
        Review review = reviewRepository.createReview(ReviewMapper.mapToReview(request));
        userStorage.addEvent(user.getId(), review.getId(), EventType.REVIEW, OperationType.ADD);
        return ReviewMapper.mapToDto(review);
    }

    public ReviewDto updateReview(UpdatedReviewRequest request) {
        Review old = reviewRepository.getReviewById(request.getReviewId()).orElseThrow(
                () -> new NotFoundException("Review with id " + request.getReviewId() + " not found"));
        Review review = reviewRepository.updateReview(ReviewMapper.mapToReview(request));
        userStorage.addEvent(old.getUserId(), review.getId(), EventType.REVIEW, OperationType.UPDATE);
        return ReviewMapper.mapToDto(review);
    }

    public void deleteReview(int id) {
        Review review = reviewRepository.getReviewById(id).orElseThrow(() ->
                new ValidationException("Couldn't delete unexisting review with id = " + id));
        reviewRepository.deleteReview(id);
        userStorage.addEvent(review.getUserId(), review.getId(), EventType.REVIEW, OperationType.REMOVE);
    }

    public void addLikeToReview(int userId, int reviewId) {
        User user = userStorage.findUser(userId).orElseThrow(() ->
                new NotFoundException("Couldn't add review from unexisting user with id = " + userId));
        Review review = reviewRepository.getReviewById(reviewId).orElseThrow(() ->
                new NotFoundException("Couldn't add like unexisting review with id = " + reviewId));
        reviewRepository.addLikeToReview(userId, reviewId);
    }

    public void addDislikeToReview(int userId, int reviewId) {
        User user = userStorage.findUser(userId).orElseThrow(() ->
                new NotFoundException("Couldn't add review from unexisting user with id = " + userId));
        Review review = reviewRepository.getReviewById(reviewId).orElseThrow(() ->
                new NotFoundException("Couldn't add dislike unexisting review with id = " + reviewId));
        reviewRepository.addDislikeToReview(userId, reviewId);
    }

    public void removeLikeDislikeFromReview(int userId, int reviewId) {
        User user = userStorage.findUser(userId).orElseThrow(() ->
                new NotFoundException("Couldn't add review from unexisting user with id = " + userId));
        Review review = reviewRepository.getReviewById(reviewId).orElseThrow(() ->
                new NotFoundException("Couldn't remove like/dislike unexisting review with id = " + reviewId));
        reviewRepository.removeLikeDislikeFromReview(userId, reviewId);
    }

}