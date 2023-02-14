package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.constant.Constants;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * добавление фильма;
 * обновление фильма;
 * получение всех фильмов.
 */
@Slf4j
@RestController
public class FilmController {

    private Map<Integer, Film> films = new HashMap<>();
    Integer id = 1;

    @GetMapping("/films")
    public Collection<Film> findAll() {
        log.info("Отправлен список всех фильмов");
        return films.values();
    }

    @PostMapping(value = "/films")
    Film create(@RequestBody Film film) {
        filmValidation(film);
        film.setId(id);
        films.put(id++, film);
        log.info("Создан новый фильм с id=" + (id-1));
        return film;
    }

    @PutMapping(value = "/films")
    Film put(@RequestBody Film film) {
        filmValidation(film);
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("Фильм с таким id отсутствует");
        }
        films.put(film.getId(), film);
        log.info("Обновлен фильм с id=" + film.getId());
        return film;
    }

    /**
     * название не может быть пустым;
     * максимальная длина описания — 200 символов;
     * дата релиза — не раньше 28 декабря 1895 года (считается днём рождения кино.);
     * продолжительность фильма должна быть положительной.
     */
    public void filmValidation(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > Constants.MAX_FILM_DESC_LENGHT) {
            throw new ValidationException("Максимальная длина описания фильма — 200 символов");
        }
        if (film.getReleaseDate().isBefore(Constants.FIRST_FILM_DATE)) {
            throw new ValidationException("Дата релиза фильма не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }

}
