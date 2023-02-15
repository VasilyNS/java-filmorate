package ru.yandex.practicum.filmorate.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.constant.Constants;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import static org.junit.jupiter.api.Assertions.*;

class FilmValidatorsTest {

    private Film film;

    @BeforeEach
    void InitEach() {
        film = new Film();
        film.setName("nisi eiusmod");
        film.setDescription("*".repeat(Constants.MAX_FILM_DESC_LENGTH));
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
                Validators.filmValidation(film);
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
                        Validators.filmValidation(film);
                    }
                });

        assertEquals("Name of the movie cannot be empty", e.getMessage());
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
                        film.setDescription("*".repeat(Constants.MAX_FILM_DESC_LENGTH + 1));
                        Validators.filmValidation(film);
                    }
                });

        assertEquals("Maximum length of the movie description is 200 characters", e.getMessage());
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
                        Validators.filmValidation(film);
                    }
                });

        assertEquals("Date of the movie's release cannot be earlier than December 28, 1895", e.getMessage());
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
                        Validators.filmValidation(film);
                    }
                });

        assertEquals("Movie duration must be positive", e.getMessage());

        final ValidationException e2 = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        film.setDuration(-10);
                        Validators.filmValidation(film);
                    }
                });

        assertEquals("Movie duration must be positive", e2.getMessage());
    }

}