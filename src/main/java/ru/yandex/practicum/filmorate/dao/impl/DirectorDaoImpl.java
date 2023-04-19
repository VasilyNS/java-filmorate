package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.exception.DirectorNotFoundException;
import ru.yandex.practicum.filmorate.model.DirectorBook;
import ru.yandex.practicum.filmorate.validation.Validators;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class DirectorDaoImpl implements DirectorDao {

    private final JdbcTemplate jdbcTemplate;

    public List<DirectorBook> findAllDirectors() {
        String sql = "SELECT * FROM director_book";

        log.info("(VS17) List of all Directors has been sent");
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirectorBook(rs));
    }

    public DirectorBook getDirectorById(int id) {
        String sql = "SELECT * FROM director_book WHERE dir_id = ?";
        List<DirectorBook> allDirectorBooks = jdbcTemplate.query(sql, (rs, rowNum) -> makeDirectorBook(rs), id);

        if (allDirectorBooks.size() == 0) {
            throw new DirectorNotFoundException(id);
        } else {
            log.info("(VS18) DirectorBook was gotten with id=" + id);
            return allDirectorBooks.get(0);
        }
    }



    public DirectorBook createDirector(DirectorBook directorBook) {
        Validators.directorBookValidation(directorBook);

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("director_book")
                .usingGeneratedKeyColumns("dir_id");

        int id = simpleJdbcInsert.executeAndReturnKey(directorBook.toMap()).intValue();
        directorBook.setId(id);

        log.info("(VS19) New directorBook was created with id=" + id);
        return directorBook;
    }

    public void delAllDirectorsInDbForFilm(int filmId) {
        String sql = "DELETE FROM director WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    public DirectorBook updateDirector(DirectorBook directorBook) {
        Validators.directorBookValidation(directorBook);
        DirectorBook checkDirectorBook = getDirectorById(directorBook.getId());

        String sql = "UPDATE director_book SET " +
                "name = ? " +
                "WHERE dir_id = ?";
        jdbcTemplate.update(sql,
                directorBook.getName(),
                directorBook.getId()
        );

        log.info("(VS20) DirectorBook was updated with id=" + directorBook.getId());
        return directorBook;
    }

    public void addDirectorsToDbForFilm(int filmId, List<DirectorBook> directorBooks) {
        for (DirectorBook dBook : directorBooks) {
            try {
                String sql = "INSERT INTO director(film_id, dir_id) VALUES (?, ?)";
                jdbcTemplate.update(sql, filmId, dBook.getId());
            } catch (Exception e) {
                // Пример обработки ошибок записи в SQL, таких, как дубль вызывающий primary key violation
                log.warn("Error on write to DB: " + e.getMessage());
            }
        }
    }

    public List<DirectorBook> findAllDirectorBooksForFilm(int filmId) {
        String sql = "SELECT d.dir_id AS dir_id, db.name AS name FROM director AS d " +
                "LEFT JOIN director_book AS db ON d.dir_id = db.dir_id " +
                "WHERE d.film_id = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeDirectorBook(rs), filmId);
    }

    public void delById(int id) {
        String sql = "DELETE FROM director WHERE dir_id = ?";
        jdbcTemplate.update(sql, id);

        sql = "DELETE FROM director_book WHERE dir_id = ?";
        jdbcTemplate.update(sql, id);

        log.info("(VS21) Director was deleted with id=" + id);
    }

    private DirectorBook makeDirectorBook(ResultSet rs) throws SQLException {
        return new DirectorBook(rs.getInt("dir_id"), rs.getString("name"));
    }

}
