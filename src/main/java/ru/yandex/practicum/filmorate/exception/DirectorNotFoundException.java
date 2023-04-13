package ru.yandex.practicum.filmorate.exception;

public class DirectorNotFoundException extends RuntimeException {

    public DirectorNotFoundException(int id) {
        super("No director with id=" + id);
    }

}
