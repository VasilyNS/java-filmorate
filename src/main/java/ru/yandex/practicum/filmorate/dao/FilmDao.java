package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

/**
 * Интерфейс для определения методов добавления,
 * удаления и модификации объектов
 */
public interface FilmDao {

    Film createFilm(Film film);

    Film getById(int id);

    Film updateFilm(Film film);

    List<Film> findAllFilms();

    void addLike(int id, int userId);

    void delLike(int id, int userId);

    List<Film> findPopular(int count, int genreId, int year);

    List<Film> findByDirWithSort(int id, String sortBy);

    void deleteFilm(int id);

    List<Film> getCommonFilms(int userId, int friendId);

    List<Film> getRecommendations(int userId);

}
