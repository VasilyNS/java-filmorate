package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.*;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.validation.Validators;

import java.sql.ResultSet;
import java.sql.SQLException;
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

        log.info("New film created with id=" + id);
        Film filmUpd = getById(id);
        return filmUpd;
    }

    public Film getById(int id) {
        String sql = "SELECT * FROM film WHERE film_id = ?";
        List<Film> allFilms = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), id);

        if (allFilms.size() == 0) {
            throw new FilmNotFoundException(id);
        } else {
            log.info("Film was gotten with id=" + id);
            return allFilms.get(0);
        }
    }

    public Film updateFilm(Film film)
    {
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
        log.info("User was updated with id=" + film.getId());
        return getById(film.getId());
    }

    public List<Film> findAllFilms() {
        String sql = "SELECT * FROM film";
        List<Film> allFilms = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));

        log.info("List of all films has been sent");
        return allFilms;
    }

    public void addLike(int id, int userId) {
        Film checkFilm = getById(id);
        User checkUser = userDao.getById(userId);

        String sql = "INSERT INTO likes(film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, id, userId);

        log.info("Like for film id=" + id + " form user with id=" + userId);
    }

    public void delLike(int id, int userId) {
        Film checkFilm = getById(id);
        User checkUser = userDao.getById(userId);

        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, userId);

        log.info("Like deleted for film id=" + id + " form user with id=" + userId);
    }

    /**
     * Методы типа rs.getInt("film_id") в makeFilm(rs) прекрасно принимают значения
     * столбцов вида "f.film_id", писать гигантскую конструкцию для всех столбцов
     * "SELECT f.film_id AS film_id, f.name AS name, .........…", не надо, хватает "SELECT f.*
     */
    public List<Film> findPopular(int count) {
        String sql = "SELECT f.*, COUNT(l.user_id) AS c FROM film AS f " +
                     "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
                     "GROUP BY f.film_id " +
                     "ORDER BY c DESC " +
                     "LIMIT ?";
        List<Film> popFilms = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), count);

        log.info("List of popular films has been sent, limit=" + count);
        return popFilms;
    }

    public List<Film> findByDirWithSort(int id, String sortBy) {
        if (sortBy.equals("year")){
            String sql = "SELECT f.* FROM DIRECTOR AS d, FILM AS f WHERE d.DIR_ID = ? AND d.FILM_ID = f.FILM_ID  " +
                         "ORDER BY f.RELEASE_DATE ";
            List<Film> sortFilms = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), id);

            if (sortFilms.size() == 0) {
                throw new FilmNotFoundException(id);
            }

            log.info("List of films sorted by year has been sent, dir. id=" + id);
            return sortFilms;
        }

        if (sortBy.equals("likes")){
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

            log.info("List of films sorted by likes has been sent, dir. id=" + id);
            return sortFilms;
        }
        return null;
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
                 .setDirectors(directorDao.findAllDirectorBooksForFilm( rs.getInt("film_id") ))
                 .setGenres(genreDao.findAllGenresForFilm( rs.getInt("film_id") ));

        return film;
    }

}
