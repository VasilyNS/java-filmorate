package ru.yandex.practicum.filmorate.validation;

import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.Constants;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.DirectorBook;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Slf4j
public class Validators {

    /**
     * Проверка фильма:
     * название не может быть пустым;
     * максимальная длина описания — 200 символов;
     * дата релиза — не раньше 28 декабря 1895 года (считается днём рождения кино.);
     * продолжительность фильма должна быть положительной.
     */
    static public void filmValidation(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Name of the movie cannot be empty");
        }

        if (film.getDescription().length() > Constants.MAX_FILM_DESC_LENGTH) {
            throw new ValidationException("Maximum length of the movie description is 200 characters");
        }

        if (film.getReleaseDate().isBefore(Constants.FIRST_FILM_DATE)) {
            throw new ValidationException("Date of the movie's release cannot be earlier than December 28, 1895");
        }

        if (film.getDuration() <= 0) {
            throw new ValidationException("Movie duration must be positive");
        }
    }

    /**
     * Проверка пользователя:
     * электронная почта не может быть пустой и должна содержать символ @;
     * логин не может быть пустым и содержать пробелы;
     * имя для отображения может быть пустым — в таком случае будет использован логин;
     * дата рождения не может быть в будущем.
     */
    static public void userValidation(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Email cannot be blank and must contain the '@' symbol");
        }

        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Login cannot be empty or contain spaces");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("User with id=" + user.getId() + " with blank name, login used");
        }

        LocalDate nd = LocalDate.now();
        if (nd.isBefore(user.getBirthday())) {
            throw new ValidationException("Date of birth cannot be in the future");
        }
    }

    /**
     * Проверка для режиссеров
     */
    static public void directorBookValidation(DirectorBook directorBook) {
        if (directorBook.getName() == null || directorBook.getName().isBlank()) {
            throw new ValidationException("Director's name cannot be blank or space");
        }
    }

}
