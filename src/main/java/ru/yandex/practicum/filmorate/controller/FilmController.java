package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.Validators;

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
    private Integer id = 1;

    @GetMapping("/films")
    public Collection<Film> findAll() {
        log.info("List of all movies has been sent");
        return films.values();
    }

    @PostMapping(value = "/films")
    Film create(@RequestBody Film film) {
        Validators.filmValidation(film);
        film.setId(id);
        films.put(id++, film);
        log.info("New movie created with id=" + (id-1));
        return film;
    }

    @PutMapping(value = "/films")
    Film put(@RequestBody Film film) {
        Validators.filmValidation(film);
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("No movie with this id");
        }
        films.put(film.getId(), film);
        log.info("Updated film with id=" + film.getId());
        return film;
    }

}
