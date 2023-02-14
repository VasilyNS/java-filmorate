package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.constant.Constants;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    Film film;
    final FilmController filmController = new FilmController();

    @BeforeEach
    public void InitEach() {
        film = new Film();
        film.setName("nisi eiusmod");
        film.setDescription("*".repeat(Constants.MAX_FILM_DESC_LENGHT));
        film.setReleaseDate(Constants.FIRST_FILM_DATE);
        film.setDuration(100);
    }

    /**
     * Корректно заполненный объект фильма не должен вызывать исключений
     */
    @Test
    void filmValidationTestForCorrectData() {
        assertDoesNotThrow(new Executable() {
            @Override
            public void execute() {
                filmController.filmValidation(film);
            }
        });
    }

    /**
     * Некорректно заполненный объект фильма, пункт ТЗ:
     * название не может быть пустым;
     */
    @Test
    void filmValidationTestForIncorrectName() {
        final ValidationException e = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        film.setName("");
                        filmController.filmValidation(film);
                    }
                });

        assertEquals("Название фильма не может быть пустым", e.getMessage());
    }

    /**
     * Некорректно заполненный объект фильма, пункт ТЗ:
     * максимальная длина описания — 200 символов
     */
    @Test
    void filmValidationTestForIncorrectBigDesc() {
        final ValidationException e = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        film.setDescription("*".repeat(Constants.MAX_FILM_DESC_LENGHT+1));
                        filmController.filmValidation(film);
                    }
                });

        assertEquals("Максимальная длина описания фильма — 200 символов", e.getMessage());
    }

    /**
     * Некорректно заполненный объект фильма, пункт ТЗ:
     * дата релиза — не раньше 28 декабря 1895 года;
     */
    @Test
    void filmValidationTestForIncorrectReleaseDate() {
        final ValidationException e = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        film.setReleaseDate(Constants.FIRST_FILM_DATE.minusDays(1));
                        filmController.filmValidation(film);
                    }
                });

        assertEquals("Дата релиза фильма не может быть раньше 28 декабря 1895 года", e.getMessage());
    }

    /**
     * Некорректно заполненный объект фильма, пункт ТЗ:
     * продолжительность фильма должна быть положительной.
     */
    @Test
    void filmValidationTestForIncorrectDuration() {
        final ValidationException e = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        film.setDuration(0); // Ноль не является положительным числом
                        filmController.filmValidation(film);
                    }
                });

        assertEquals("Продолжительность фильма должна быть положительной", e.getMessage());

        final ValidationException e2 = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        film.setDuration(-10);
                        filmController.filmValidation(film);
                    }
                });

        assertEquals("Продолжительность фильма должна быть положительной", e2.getMessage());
    }

}