package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.DirectorBook;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface DirectorDao {

    List<DirectorBook> findAllDirectors();

    DirectorBook getDirectorById(int id);

    DirectorBook createDirector(DirectorBook directorBook);

    DirectorBook updateDirector(DirectorBook directorBook);

    void delAllDirectorsInDbForFilm(int filmId);

    void addDirectorsToDbForFilm(int filmId, List<DirectorBook> directorBooks);

    List<DirectorBook> findAllDirectorBooksForFilm(int filmId);

    void delById(int id);

}
