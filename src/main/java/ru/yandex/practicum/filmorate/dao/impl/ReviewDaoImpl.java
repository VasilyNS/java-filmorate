package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.ReviewDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exception.ReviewNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Primary
@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewDaoImpl implements ReviewDao {

    private final JdbcTemplate jdbcTemplate;
    private final FilmDao filmDao;
    private final UserDao userDao;

    @Override
    public Review create(Review review) {
        Film film = filmDao.getById(review.getFilmId());
        User user = userDao.getById(review.getUserId());

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("REVIEW_BOOK")
                .usingGeneratedKeyColumns("review_id");

        Map<String, Object> newReview = new HashMap<>(
                Map.of("content", review.getContent(),
                        "is_positive", review.getIsPositive()
                ));

        int id = simpleJdbcInsert.executeAndReturnKey(newReview).intValue();

        review.setReviewId(id);

        String sqlQuery = "insert into USER_FILM_REVIEW(user_id, film_id, review_id) " +
                "values (?, ?, ?)";

        jdbcTemplate.update(sqlQuery,
                review.getUserId(),
                review.getFilmId(),
                review.getReviewId());

        log.info("(DV1) New review was created with id=" + id);
        return review;
    }

    @Override
    public List<Review> findAllReviewsByFilmId(Integer count, Integer filmId) {
        List<Review> result;

        if (filmId == 0) {
            String sqlQuery = "select rb.review_id, rb.content, rb.is_positive, ufr.film_id, ufr.user_id \n" +
                    "from REVIEW_BOOK as rb\n" +
                    "left outer join USER_FILM_REVIEW as ufr on rb.review_id = ufr.review_id \n" +
                    "limit ?;";

            result = jdbcTemplate.query(sqlQuery, this::mapRowToReview, count);
        } else {
            Film film = filmDao.getById(filmId);

            String sqlQuery = "SELECT rb.review_id, rb.content, rb.is_positive, ufr.film_id, ufr.user_id \n" +
                    "FROM REVIEW_BOOK as rb\n" +
                    "LEFT OUTER JOIN USER_FILM_REVIEW AS ufr ON rb.review_id = ufr.review_id \n" +
                    "where ufr.film_id = ?" +
                    "limit ?;";

            result = jdbcTemplate.query(sqlQuery, this::mapRowToReview, filmId, count);
        }

        return result.stream().sorted((Comparator.comparingInt(Review::getUseful)).reversed()).collect(Collectors.toList());
    }

    @Override
    public Review put(Review review) {
        Film film = filmDao.getById(review.getFilmId());
        User user = userDao.getById(review.getUserId());

        if (review.getReviewId() == null) {
            return create(review);
        } else if (findById(review.getReviewId()) != null) {
            jdbcTemplate.update("update REVIEW_BOOK set content = ?, is_positive = ? where review_id = ?",
                    review.getContent(), review.getIsPositive(), review.getReviewId());

            log.info("(DV2) Review was updated with id=" + review.getReviewId());
            return findById(review.getReviewId());
        } else {
            throw new ReviewNotFoundException(review.getReviewId());
        }
    }

    @Override
    public void deleteReview(int reviewId) {
        Review review = findById(reviewId);

        jdbcTemplate.update("delete from USER_FILM_REVIEW where review_id = ?", reviewId);
        jdbcTemplate.update("delete from REVIEW_ESTIMATION where review_id = ?", reviewId);
        jdbcTemplate.update("delete from REVIEW_BOOK where review_id = ?", reviewId);

        log.info("(DV3) Review was deleted with id=" + reviewId);
    }

    @Override
    public Review findById(int reviewId) {
        SqlRowSet reviewRows = jdbcTemplate.queryForRowSet("select rb.review_id, rb.content, rb.is_positive, " +
                "ufr.film_id, ufr.user_id \n" +
                "from REVIEW_BOOK as rb\n" +
                "left outer join USER_FILM_REVIEW as ufr on rb.review_id = ufr.review_id \n" +
                "where rb.review_id = ?", reviewId);

        if (reviewRows.next()) {
            log.info("(DV4) Review was gotten with id=" + reviewId);
            return Review.builder()
                    .reviewId(reviewRows.getInt("review_id"))
                    .content(reviewRows.getString("content"))
                    .isPositive(reviewRows.getBoolean("is_positive"))
                    .useful(usefulRows(reviewId))
                    .filmId(reviewRows.getInt("film_id"))
                    .userId(reviewRows.getInt("user_id"))
                    .build();
        } else {
            throw new ReviewNotFoundException(reviewId);
        }
    }

    @Override
    public void addEstimation(int reviewId, int userId, boolean isPositive) {
        User user = userDao.getById(userId);
        Review review = findById(reviewId);

        jdbcTemplate.update("insert into review_estimation(user_id, review_id, is_positive) " +
                "values (?, ?, ?)", userId, reviewId, isPositive);

        log.info("(DV6) User reaction to the review has been added. reviewId="
                + reviewId + " userId=" + userId);
    }

    @Override
    public void deleteEstimation(int reviewId, int userId, boolean isPositive) {
        Review review = findById(reviewId);

        jdbcTemplate.update("delete from review_estimation " +
                "where user_id = ? and review_id = ? and review_id = ?", userId, reviewId, isPositive);

        log.info("(DV7) User reaction to the review has been deleted. reviewId="
                + reviewId + " userId=" + userId);
    }

    private Review mapRowToReview(ResultSet resultSet, int rowNum) throws SQLException {
        int reviewId = resultSet.getInt("review_id");

        return Review.builder()
                .reviewId(reviewId)
                .content(resultSet.getString("content"))
                .isPositive(resultSet.getBoolean("is_positive"))
                .useful(usefulRows(reviewId))
                .filmId(resultSet.getInt("film_id"))
                .userId(resultSet.getInt("user_id"))
                .build();
    }

    private int usefulRows(int reviewId) {
        SqlRowSet usefulRows = jdbcTemplate.queryForRowSet("select re.review_id, " +
                "(coalesce(l.likes, 0) - coalesce(d.dislikes, 0)) as useful\n" +
                "from review_estimation as re \n" +
                "left outer join (select review_id, count(is_positive) AS likes from review_estimation " +
                "where is_positive = true group by review_id) as l on re.review_id = l.review_id\n" +
                "left outer join (select review_id, count(is_positive) as dislikes from review_estimation " +
                "where is_positive = false group by review_id) as d on re.review_id = d.review_id\n" +
                "where re.review_id = ?\n" +
                "group by re.review_id;", reviewId);

        if (usefulRows.next()) {
            return usefulRows.getInt("useful");
        } else {
            return 0;
        }
    }
}
