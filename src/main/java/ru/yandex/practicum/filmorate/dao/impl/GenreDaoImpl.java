package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.exception.GenreBookNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.GenreBook;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class GenreDaoImpl implements GenreDao {

    private final JdbcTemplate jdbcTemplate;

    public List<GenreBook> findAllGenreBook(){
        String sql = "SELECT * FROM genre_book";
        List<GenreBook> allGenres = jdbcTemplate.query(sql, (rs, rowNum) -> makeGenreBook(rs));
        return allGenres;
    }

    public GenreBook getGenreById(int id) {
        String sql = "SELECT * FROM genre_book WHERE genre_id = ?";
        List<GenreBook> allGenres = jdbcTemplate.query(sql, (rs, rowNum) -> makeGenreBook(rs), id);
        if (allGenres.size() == 0) {
            throw new GenreBookNotFoundException(id);
        } else {
            return allGenres.get(0);
        }
    }

    public String getGenreNameById(int id) {
        return getGenreById(id).getName();
    }

    public void addGenresToDbForFilm(int filmId, List<GenreBook> genres) {
        for (GenreBook genre : genres) {
            try{
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

    public List<Genre> findAllGenresForFilm(int filmId){
        String sql = "SELECT * FROM genre WHERE film_id = ?";
        List<Genre> allGenres = jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs), filmId);
        return allGenres;
    }

    private GenreBook makeGenreBook(ResultSet rs) throws SQLException {
        return new GenreBook(rs.getInt("genre_id"), rs.getString("name"));
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        return new Genre(rs.getInt("film_id"), rs.getInt("genre_id"));
    }

}
