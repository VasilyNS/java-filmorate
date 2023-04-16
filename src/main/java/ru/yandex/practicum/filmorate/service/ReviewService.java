package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.utils.DateUtils;
import ru.yandex.practicum.filmorate.dao.EventDao;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.enums.FeedEventType;
import ru.yandex.practicum.filmorate.enums.FeedOperation;
import ru.yandex.practicum.filmorate.model.Event;
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
    private final EventDao eventDao;

    public Review create(Review review) {
        Review addedReview = reviewDao.create(review);

        Event event = new Event(
                DateUtils.now().toEpochMilli(),
                addedReview.getUserId(),
                FeedEventType.REVIEW,
                FeedOperation.ADD,
                addedReview.getReviewId()
        );

        eventDao.createFeed(event);

        return addedReview;
    }

    public Review put(Review review) {
        Review updatedReview = reviewDao.put(review);

        Event event = new Event(
                DateUtils.now().toEpochMilli(),
                updatedReview.getUserId(),
                FeedEventType.REVIEW,
                FeedOperation.UPDATE,
                updatedReview.getReviewId()
        );

        eventDao.createFeed(event);

        return updatedReview;
    }

    public void deleteReview(int id) {
        Review review = findById(id);

        Event event = new Event(
                DateUtils.now().toEpochMilli(),
                review.getUserId(),
                FeedEventType.REVIEW,
                FeedOperation.REMOVE,
                review.getReviewId()
        );

        eventDao.createFeed(event);

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
