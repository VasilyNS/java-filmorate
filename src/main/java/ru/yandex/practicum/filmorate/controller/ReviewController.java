package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * Добавление нового отзыва
     */
    @PostMapping
    public Review create(@Valid @RequestBody Review review) {
        return reviewService.create(review);
    }

    /**
     * Редактирование уже имеющегося отзыва
     */
    @PutMapping
    public Review put(@Valid @RequestBody Review review) {
        return reviewService.put(review);
    }


    /**
     * Удаление уже имеющегося отзыва
     */
    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable int id) {
        reviewService.deleteReview(id);
    }

    /**
     * Получение отзыва по идентификатору
     */
    @GetMapping("/{id}")
    public Review findById(@PathVariable int id) {
        return reviewService.findById(id);
    }

    /**
     * Получение всех отзывов по идентификатору фильма, если фильм не указан, то все. Если кол-во не указано, то 10
     */
    @GetMapping
    public List<Review> findReviewsByFilmId(@RequestParam(defaultValue = "10", required = false) Integer count, @RequestParam(defaultValue = "0", required = false) Integer filmId) {

        if (count < 1) {
            throw new ValidationException("the count for must be less than 1");
        }
        if (filmId < 0) {
            throw new ValidationException("the filmId for must be less than 0");
        }

        return reviewService.findAllReviewsByFilmId(count, filmId);
    }

    /**
     * Пользователь ставит лайк отзыву
     */
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        reviewService.addLike(id, userId);
    }

    /**
     * Пользователь ставит дизлайк отзыву
     */
    @PutMapping("/{id}/dislike/{userId}")
    public void addDislike(@PathVariable int id, @PathVariable int userId) {
        reviewService.addDislike(id, userId);
    }

    /**
     * Пользователь удаляет лайк отзыву
     */
    @DeleteMapping("{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        reviewService.deleteLike(id, userId);
    }

    /**
     * Пользователь удаляет дизлайк отзыву
     */
    @DeleteMapping("{id}/dislike/{userId}")
    public void deleteDislike(@PathVariable int id, @PathVariable int userId) {
        reviewService.deleteDislike(id, userId);
    }
}
