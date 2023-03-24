package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.*;
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
    private final JdbcTemplate jdbcTemplate;

    public Film createFilm(Film film) {
        Validators.filmValidation(film);
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("film")
                .usingGeneratedKeyColumns("film_id");
        int id = simpleJdbcInsert.executeAndReturnKey(film.toMap()).intValue();
        film.setId(id);
        // Запись в объект имени рейтинга
        int mpaId = film.getMpa().getId();
        film.getMpa().setName(mpaDao.getMpaNameById(mpaId));
        // Запись в объект имен жанров
        for (GenreBook genre : film.getGenres()) {
            genre.setName(genreDao.getGenreNameById(genre.getId()));
        }
        // Запись жанров фильма в базу
        genreDao.addGenresToDbForFilm(id, film.getGenres());
        log.info("New film created with id=" + id);
        return film;
    }

    public Film getById(int id) {
        String sql = "SELECT * FROM film WHERE film_id = ?";
        List<Film> allFilms = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), id);
        if (allFilms.size() == 0) {
            throw new FilmNotFoundException(id);
        } else {
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
        genreDao.delAllGenresInDbForFilm(film.getId());
        genreDao.addGenresToDbForFilm(film.getId(), film.getGenres());
        // После возможного удаления неуникальных id жанров, что СУБД сделает автоматом на основе PK,
        // объект фильма надо просто перечитать из БД
        return getById(film.getId());
    }

    public List<Film> findAllFilms() {
        String sql = "SELECT * FROM film";
        List<Film> allFilms = jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
        return allFilms;
    }

    public void addLike(int id, int userId) {
        Film checkFilm = getById(id);
        User checkUser = userDao.getById(userId);
        String sql = "INSERT INTO likes(film_id, user_id) VALUES (?, ?)";
        jdbcTemplate.update(sql, id, userId);
    }

    public void delLike(int id, int userId) {
        Film checkFilm = getById(id);
        User checkUser = userDao.getById(userId);
        String sql = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, userId);
    }

    public List<Film> findPopular(int count) {
        String sql = "SELECT f.film_id AS f, COUNT(l.user_id) AS c FROM film AS f " +
                     "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
                     "GROUP BY f.film_id " +
                     "ORDER BY c DESC " +
                     "LIMIT ?";
        List<Integer> popIds = jdbcTemplate.query(sql, (rs, rowNum) -> makeIdForFilm(rs), count);
        List<Film> popFilms = new ArrayList<>();
        for (Integer popId : popIds) {
            popFilms.add(getById(popId));
        }
        return popFilms;
    }

    /**
     * Создание из ResultSet сложного объекта - film, который включает подобъект:
     * "mpa": { "id": 3, "name": "PG-13" }
     * и список объектов:
     * "genres": [{"id": 1, "name": "Комедия"}, {"id": 2, "name": "Драма"}, ...]
     */
    private Film makeFilm(ResultSet rs) throws SQLException {
        Film film = new Film(
                rs.getInt("film_id"),
                rs.getString("name"),
                rs.getString("description"),
                rs.getDate("release_date").toLocalDate(),
                rs.getInt("duration")
        );
        // Добавляем в объект фильма объект mpa с id и именем рейтинга, имя рейтинга mpa читается из базы
        int mpa_id = rs.getInt("rating_id");
        Mpa mpa = mpaDao.getMpaById(mpa_id);
        film.setMpa(mpa);
        // Считываем все жанры для конкретного фильма из базы
        List<Genre> gs = genreDao.findAllGenresForFilm(film.getId());
        // Создаем массив справочника жанров (id жанра, имя жанра) для конкретного фильма
        List<GenreBook> gsForFilm = new ArrayList<>();
        for (Genre g : gs) {
            GenreBook gForFilm = new GenreBook(g.getGenreId(), genreDao.getGenreNameById(g.getGenreId()));
            gsForFilm.add(gForFilm);
        }
        // Записываем полученный массив жанров в фильм
        film.setGenres(gsForFilm);
        return film;
    }

    private Integer makeIdForFilm(ResultSet rs) throws SQLException {
        Integer id = rs.getInt("f");
        return id;
    }


}
