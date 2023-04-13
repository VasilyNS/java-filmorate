package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

/**
 * Интерфейс для определения методов добавления,
 * удаления и модификации отзывов и лайков/дизлайков к ним
 */
public interface ReviewDao {
    Review create(Review review);

    List<Review> findAllReviewsByFilmId(Integer count, Integer filmId);

    Review put(Review review);

    void deleteReview(int id);

    Review findById(int id);

    void addEstimation(int id, int userId, boolean isPositive);

    void deleteEstimation(int id, int userId, boolean isPositive);
}
