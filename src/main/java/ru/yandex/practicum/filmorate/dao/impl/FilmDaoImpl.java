package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.Validators;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Primary
@Slf4j
@Component
@RequiredArgsConstructor
public class FilmDaoImpl implements FilmDao {

    private final UserDao userDao;
    private final MpaDao mpaDao;
    private final GenreDao genreDao;
    private final DirectorDao directorDao;

    private final JdbcTemplate jdbcTemplate;

    public Film createFilm(Film film) {
        Validators.filmValidation(film);
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film")
                .usingGeneratedKeyColumns("film_id");
        int id = simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue();
        film.setId(id);

        // Запись жанров фильма в базу
        genreDao.addGenresToDbForFilm(id, film.getGenres());
        // Запись режиссеров фильма в базу
        directorDao.addDirectorsToDbForFilm(id, film.getDirectors());

        log.info("(VS9) New film created with id=" + id);
        Film filmUpd = getById(id);
        return filmUpd;
    }

    public Film getById(int id) {
        String sql = "SELECT * FROM film WHERE film_id = ?";
        List<Film> allFilms = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), id);

        if (allFilms.size() == 0) {
            throw new FilmNotFoundException(id);
        } else {
            log.info("(VS10) Film was gotten with id=" + id);
            return allFilms.get(0);
        }
    }

    public Film updateFilm(Film film) {
        Validators.filmValidation(film);
        Film checkFilm = getById(film.getId());

        String sql = "UPDATE film SET " +
                "name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? " +
                "WHERE film_id = ?";
        jdbcTemplate.update(sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId(),
                film.getId()
        );

        // Обновляем жанры в БД
        genreDao.delAllGenresInDbForFilm(film.getId());
        genreDao.addGenresToDbForFilm(film.getId(), film.getGenres());

        // Обновляем режиссеров в БД
        directorDao.delAllDirectorsInDbForFilm(film.getId());
        directorDao.addDirectorsToDbForFilm(film.getId(), film.getDirectors());

        // После возможного удаления неуникальных id жанров, что СУБД сделает автоматом на основе PK,
        // объект фильма надо просто перечитать из БД
        log.info("(VS11) User was updated with id=" + film.getId());
        return getById(film.getId());
    }

    public List<Film> findAllFilms() {
        String sql = "SELECT * FROM film";
        List<Film> allFilms = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));

        log.info("(VS12) List of all films has been sent");
        return allFilms;
    }

    public void addLike(int id, int userId) {
        Film checkFilm = getById(id);
        User checkUser = userDao.getById(userId);

        try {
            String sql = "INSERT INTO likes(film_id, user_id) VALUES (?, ?)";
            jdbcTemplate.update(sql, id, userId);
        } catch (Exception e) {
            log.warn("Error on write to DB: " + e.getMessage());
        }

        log.info("(VS13) Like for film id=" + id + " form user with id=" + userId);
    }


    public void delLike(int id, int userId) {
        Film checkFilm = getById(id);
        User checkUser = userDao.getById(userId);

        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, userId);

        log.info("(VS14) Like deleted for film id=" + id + " form user with id=" + userId);
    }

    /**
     * Методы типа rs.getInt("film_id") в makeFilm(rs) прекрасно принимают значения
     * столбцов вида "f.film_id", писать гигантскую конструкцию для всех столбцов
     * "SELECT f.film_id AS film_id, f.name AS name, .........…", не надо, хватает "SELECT f.*
     */
    public List<Film> findPopular(int count, int genreId, int year) {
        List<Film> popFilms;

        // если в запросе не передали жанр и год, то выводим все фильмы (VS15 - только первый блок if)
        if (genreId == 0 && year == 0) {
            String sql = "SELECT f.*, COUNT(l.user_id) AS c FROM film AS f " +
                    "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
                    "GROUP BY f.film_id " +
                    "ORDER BY c DESC " +
                    "LIMIT ?";
            popFilms = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), count);
        } else if (genreId == 0) {
            // если жанр не указан, то выводим по году
            String sql = "SELECT f.*, COUNT(l.user_id) AS c FROM film AS f " +
                    "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
                    "LEFT JOIN genre AS g ON f.film_id = g.film_id " +
                    "LEFT JOIN genre_book AS gb ON g.genre_id = gb.genre_id " +
                    "WHERE EXTRACT(YEAR FROM CAST(f.release_date AS date)) = ? " +
                    "GROUP BY f.film_id " +
                    "ORDER BY c DESC " +
                    "LIMIT ?";
            popFilms = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), year, count);
        } else if (year == 0) {
            // если год не указан, то выводим по жанру
            String sql = "SELECT f.*, COUNT(l.user_id) AS c FROM film AS f " +
                    "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
                    "LEFT JOIN genre AS g ON f.film_id = g.film_id " +
                    "LEFT JOIN genre_book AS gb ON g.genre_id = gb.genre_id " +
                    "WHERE g.genre_id = ? " +
                    "GROUP BY f.film_id, g.genre_id " +
                    "ORDER BY c DESC " +
                    "LIMIT ?";
            popFilms = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), genreId, count);
        } else {
            String sql = "SELECT f.*, COUNT(l.user_id) AS c FROM film AS f " +
                    "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
                    "LEFT JOIN genre AS g ON f.film_id = g.film_id " +
                    "LEFT JOIN genre_book AS gb ON g.genre_id = gb.genre_id " +
                    "WHERE g.genre_id = ? AND EXTRACT(YEAR FROM CAST(f.release_date AS date)) = ? " +
                    "GROUP BY f.film_id, g.genre_id " +
                    "ORDER BY c DESC " +
                    "LIMIT ?";
            popFilms = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), genreId, year, count);
        }

        log.info("(BE1) List of popular films has been sent, limit=" + count + ", genreId=" + genreId + ", year=" + year);
        return popFilms;
    }

    public void deleteFilm(int id) {
        String sqlQuery = "DELETE FROM film WHERE film_id = ?";

        jdbcTemplate.update(sqlQuery, id);

        log.info("(RF2) Film id: " + id + " deleted");
    }

    public List<Film> findByDirWithSort(int id, String sortBy) {
        if (sortBy.equals("year")) {
            String sql = "SELECT f.* FROM DIRECTOR AS d, FILM AS f WHERE d.DIR_ID = ? AND d.FILM_ID = f.FILM_ID  " +
                    "ORDER BY f.RELEASE_DATE ";
            List<Film> sortFilms = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), id);

            if (sortFilms.size() == 0) {
                throw new FilmNotFoundException(id);
            }

            log.info("List of films sorted by year has been sent, dir. id=" + id);
            return sortFilms;
        }

        if (sortBy.equals("likes")) {
            String sql = "SELECT f.*, d.DIR_ID , COUNT(l.user_id) AS c FROM film AS f " +
                    "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
                    "LEFT JOIN director AS d ON f.film_id = d.film_id " +
                    "WHERE d.DIR_ID = ? " +
                    "GROUP BY f.film_id " +
                    "ORDER BY c DESC";
            List<Film> sortFilms = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), id);

            if (sortFilms.size() == 0) {
                throw new FilmNotFoundException(id);
            }

            log.info("(VS16) List of films sorted by likes has been sent, dir. id=" + id);
            return sortFilms;
        }
        return new ArrayList<>();
    }

    public List<Film> getCommonFilms(int userId, int friendId) {
        String sqlQuery = "SELECT f.*, r.rate " +
                "FROM LIKES l " +
                "JOIN LIKES l2 ON l2.FILM_ID = l.FILM_ID " +
                "JOIN FILM f ON l.FILM_ID = f.FILM_ID " +
                "JOIN (SELECT l3.FILM_ID, COUNT(USER_ID) AS rate " +
                "FROM LIKES l3 " +
                "GROUP BY l3.FILM_ID) AS r ON r.film_id = l.FILM_ID " +
                "WHERE l.USER_ID = ? AND l2.USER_ID = ? " +
                "ORDER BY r.rate DESC";

        List<Film> commonFilms = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs), userId, friendId);

        log.info("(RF3) Getting common films");
        return commonFilms;
    }

    /**
     * Поиск пользователя с максимальным числом фильмов,
     * которые также есть у пользователя, для которого составляются рекомендации.
     * Далее поиск и возвращения фильмов, которые есть у этого пользователя,
     * но нет у того, для кого составляются рекомендации
     */
    public List<Film> getRecommendations(int userId) {
        List<Integer> userIdWithMaxCommonFilms = findUserIdWithMaxCommonFilms(userId);

        if (userIdWithMaxCommonFilms.size() == 0) {
            return new ArrayList<>();
        }

        List<Film> recommendations = getDifferentFilmsBetweenUsers(userIdWithMaxCommonFilms.get(0), userId);
        log.info("(AN1) List of recommended films has been formed");
        return recommendations;
    }

    /**
     * Поиск id пользователя с максимальным числом фильмов,
     * которые также есть у пользователя, для которого составляются рекомендации
     */
    private List<Integer> findUserIdWithMaxCommonFilms(int id) {
        String sqlFindUserWithMaxCommonFilms = "SELECT l.user_id " +
                "FROM likes AS l " +
                "JOIN ( " +
                "SELECT film_id AS filmId, user_id AS userId " +
                "FROM likes WHERE user_id = ? " +
                ") AS u ON l.film_id = u.filmId " +
                "WHERE NOT l.user_id = u.userId " +
                "GROUP BY l.user_id " +
                "ORDER BY COUNT(*) DESC " +
                "LIMIT 1";

        return jdbcTemplate.queryForList(sqlFindUserWithMaxCommonFilms, Integer.class, id);
    }

    /**
     * Поиск фильмов пользователя с id = fromUserId, которых нет у пользователя с id = toUserId
     */
    private List<Film> getDifferentFilmsBetweenUsers(int fromUserId, int toUserId) {
        String sqlGetDifferentFilmsBetweenUsers = "SELECT * " +
                "FROM film f " +
                "WHERE film_id IN ( " +
                "SELECT l.film_id " +
                "FROM likes AS l " +
                "WHERE l.user_id = ? " +
                "AND l.film_id NOT IN ( " +
                "SELECT film_id AS filmId " +
                "FROM likes WHERE user_id = ? " +
                ")" +
                ")";
        return jdbcTemplate.query(sqlGetDifferentFilmsBetweenUsers, (rs, rowNum) -> makeFilm(rs), fromUserId, toUserId);
    }

    /**
     * Поиск фильмов по части названия фильма
     */
    public List<Film> searchByName(String query) {
        String sqlQuery = "SELECT f.* FROM film AS f " +
                "WHERE LOWER(f.name) LIKE '%" + query.toLowerCase() + "%'";
        List<Film> films = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs));

        log.info("(AN2) Sent a list of all movies found by name");
        return films;
    }

    /**
     * Поиск фильмов по части имени режиссера
     */
    public List<Film> searchByDir(String query) {
        String sqlQuery = "SELECT f.* FROM film AS f " +
                "JOIN director AS d ON f.film_id = d.film_id " +
                "JOIN director_book AS db ON d.dir_id = db.dir_id " +
                "WHERE LOWER(db.name) LIKE '%" + query.toLowerCase() + "%'";
        List<Film> films = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeFilm(rs));

        log.info("(AN3) Sent a list of all movies found by name of director");
        return films;
    }

    /**
     * Создание из ResultSet сложного объекта - film, который включает подобъект:
     * "mpa": { "id": 3, "name": "PG-13" }
     * и список объектов:
     * "genres": [{"id": 1, "name": "Комедия"}, {"id": 2, "name": "Драма"}, ...]
     */
    private Film makeFilm(ResultSet rs) throws SQLException {
        Film film = new Film()
                .setId(rs.getInt("film_id"))
                .setName(rs.getString("name"))
                .setDescription(rs.getString("description"))
                .setReleaseDate(rs.getDate("release_date").toLocalDate())
                .setDuration(rs.getInt("duration"))
                .setMpa(mpaDao.getMpaById(rs.getInt("rating_id")))
                .setDirectors(directorDao.findAllDirectorBooksForFilm(rs.getInt("film_id")))
                .setGenres(genreDao.findAllGenresForFilm(rs.getInt("film_id")));

        return film;
    }

}
