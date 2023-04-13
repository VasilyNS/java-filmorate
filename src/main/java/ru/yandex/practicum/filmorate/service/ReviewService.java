package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

/**
 * Класс для реализации операций с отзывами: добавление, удаление, изменение поиск отзывов,
 * добавление и удаление лайка или дизлайка к отзывам,
 * Каждый пользователь может поставить лайк отзыву только один раз.
 */
@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewDao reviewDao;

    public Review create(Review review) {
        return reviewDao.create(review);
    }

    public Review put(Review review) {
        return reviewDao.put(review);
    }

    public void deleteReview(int id) {
        reviewDao.deleteReview(id);
    }

    public Review findById(int id) {
        return reviewDao.findById(id);
    }

    public List<Review> findAllReviewsByFilmId(Integer count, Integer filmId) {
        return reviewDao.findAllReviewsByFilmId(count, filmId);
    }

    public void addLike(int id, int userId) {
        reviewDao.addEstimation(id, userId, true);
    }

    public void addDislike(int id, int userId) {
        reviewDao.addEstimation(id, userId, false);
    }

    public void deleteLike(int id, int userId) {
        reviewDao.deleteEstimation(id, userId, true);
    }

    public void deleteDislike(int id, int userId) {
        reviewDao.deleteEstimation(id, userId, false);
    }
}
