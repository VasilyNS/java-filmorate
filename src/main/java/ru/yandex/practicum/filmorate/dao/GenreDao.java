package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.GenreBook;

import java.util.List;

public interface GenreDao {

    List<GenreBook> findAllGenreBook();

    GenreBook getGenreById(int id);

    String getGenreNameById(int id);

    void addGenresToDbForFilm(int filmId, List<GenreBook>  genres);

    void delAllGenresInDbForFilm(int filmId);

    List<GenreBook> findAllGenresForFilm(int filmId);

}
