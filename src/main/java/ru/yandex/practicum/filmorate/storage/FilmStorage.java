package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

/**
 * Интерфейс для определения методов добавления,
 * удаления и модификации объектов
 */
public interface FilmStorage {

    Collection<Film> findAllFilms();

    Film createFilm(Film film);

    Film updateFilm(Film film);

    Film getById(int id);

}
