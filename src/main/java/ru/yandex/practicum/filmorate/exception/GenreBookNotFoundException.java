package ru.yandex.practicum.filmorate.exception;

public class GenreBookNotFoundException extends RuntimeException {

        public GenreBookNotFoundException(int id) {
            super("No genre with id=" + id);
        }

}
