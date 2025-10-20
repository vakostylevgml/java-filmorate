package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.review.NewReviewRequest;
import ru.yandex.practicum.filmorate.dto.review.ReviewDto;
import ru.yandex.practicum.filmorate.dto.review.UpdatedReviewRequest;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public ReviewDto createReview(@Valid @RequestBody NewReviewRequest request) {
        log.info("Create review with id {}", request);
        return reviewService.createReview(request);
    }

    @PutMapping
    public ReviewDto updateReview(@Valid @RequestBody UpdatedReviewRequest request) {
        log.warn("Update review with request: {}", request);
        return reviewService.updateReview(request);
    }

    @GetMapping
    public List<ReviewDto> getReviews(@RequestParam(required = false) Integer filmId, @RequestParam(required = false, defaultValue = "10") int count) {
        if (filmId == null) {
            log.info("Get all reviews");
            return reviewService.getAllReviews();
        } else {
            log.warn("Get reviews for movie with ID = {} and limit = {}", filmId, count);
            return reviewService.getReviewsByFilmId(filmId, count);
        }
    }

    @GetMapping("/{id}")
    public ReviewDto getReview(@PathVariable int id) {
        log.info("Get review with ID = {}", id);
        return reviewService.getReviewById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable int id) {
        log.info("Revmove review with ID = {}", id);
        reviewService.deleteReview(id);
    }

    @PutMapping("/{reviewId}/like/{userId}")
    public void addLike(@PathVariable("reviewId") int reviewId, @PathVariable int userId) {
        log.info("Like review with ID = {} from user with ID = {}", reviewId, userId);
        reviewService.addLikeToReview(userId, reviewId);
    }

    @DeleteMapping("/{reviewId}/like/{userId}")
    public void removeLike(@PathVariable("reviewId") int reviewId, @PathVariable int userId) {
        log.info("Remove like from review with ID = {} from user with ID = {}", reviewId, userId);
        reviewService.removeLikeDislikeFromReview(userId, reviewId);
    }

    @PutMapping("/{reviewId}/dislike/{userId}")
    public void addDislike(@PathVariable("reviewId") int reviewId, @PathVariable int userId) {
        log.info("Dislike review with ID = {} from user with ID = {}", reviewId, userId);
        reviewService.addDislikeToReview(userId, reviewId);
    }

    @DeleteMapping("/{reviewId}/dislike/{userId}")
    public void removeDislike(@PathVariable("reviewId") int reviewId, @PathVariable int userId) {
        log.info("Remove dislike from review with ID = {} from user with ID = {}", reviewId, userId);
        reviewService.removeLikeDislikeFromReview(userId, reviewId);
    }

}