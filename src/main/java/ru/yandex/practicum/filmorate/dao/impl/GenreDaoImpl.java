package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exception.GenreBookNotFoundException;
import ru.yandex.practicum.filmorate.model.GenreBook;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class GenreDaoImpl implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    public List<GenreBook> findAllGenreBook() {
        String sql = "SELECT * FROM genre_book";

        log.info("(VS22) List of all Genres has been sent");
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenreBook(rs));
    }

    public GenreBook getGenreById(int id) {
        String sql = "SELECT * FROM genre_book WHERE genre_id = ?";
        List<GenreBook> allGenres = jdbcTemplate.query(sql, (rs, rowNum) -> makeGenreBook(rs), id);

        if (allGenres.size() == 0) {
            throw new GenreBookNotFoundException(id);
        } else {
            log.info("(VS23) Genre was gotten with id=" + id);
            return allGenres.get(0);
        }
    }

    public String getGenreNameById(int id) {
        return getGenreById(id).getName();
    }

    public void addGenresToDbForFilm(int filmId, List<GenreBook> genres) {
        for (GenreBook genre : genres) {
            try {
                String sql = "INSERT INTO genre(film_id, genre_id) VALUES (?, ?)";
                jdbcTemplate.update(sql, filmId, genre.getId());
            } catch (Exception e) {
                // Пример обработки ошибок записи в SQL, таких, как дубль жанра, вызывающий primary key violation
                log.warn("Error on write to DB: " + e.getMessage());
            }
        }
    }

    public void delAllGenresInDbForFilm(int filmId) {
        String sql = "DELETE FROM genre WHERE film_id = ?";
        jdbcTemplate.update(sql, filmId);
    }

    public List<GenreBook> findAllGenresForFilm(int filmId) {
        String sql = "SELECT g.genre_id AS genre_id, gb.name AS name FROM genre AS g " +
                "LEFT JOIN genre_book AS gb ON g.genre_id = gb.genre_id " +
                "WHERE g.film_id = ?";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenreBook(rs), filmId);
    }

    private GenreBook makeGenreBook(ResultSet rs) throws SQLException {
        return new GenreBook(rs.getInt("genre_id"), rs.getString("name"));
    }

}
