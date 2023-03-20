package ru.yandex.practicum.filmorate.exception;

public class FilmNotFoundException extends RuntimeException {

    public FilmNotFoundException(int id) {
        super("No movie with id=" + id);
    }

}
