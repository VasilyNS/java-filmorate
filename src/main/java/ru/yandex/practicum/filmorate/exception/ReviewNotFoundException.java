package ru.yandex.practicum.filmorate.exception;

public class ReviewNotFoundException extends RuntimeException {

    public ReviewNotFoundException(int id) {
        super("No review with id=" + id);
    }

}
