package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.Validators;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс для реализации логики хранения, обновления и поиска объектов
 */
@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    protected Map<Integer, Film> films = new HashMap<>();
    protected Integer id = 1;

    public Collection<Film> findAllFilms() {
        log.info("List of all movies has been sent");
        return films.values();
    }

    public Film createFilm(Film film) {
        Validators.filmValidation(film);
        film.setId(id);
        films.put(id++, film);
        log.info("New movie created with id=" + (id-1));
        return film;
    }

    public Film updateFilm(Film film) {
        Validators.filmValidation(film);
        if (!films.containsKey(film.getId())) {
            throw new ObjectNotFoundException("No movie with this id");
        }
        films.put(film.getId(), film);
        log.info("Updated film with id=" + film.getId());
        return film;
    }

    public Film findById(int id) {
        if (!films.containsKey(id)) {
            throw new ObjectNotFoundException("No film with this id");
        }
        return films.get(id);
    }

}
