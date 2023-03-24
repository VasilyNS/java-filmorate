package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.Validators;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Класс для реализации логики хранения В ПАМЯТИ обновления и поиска объектов
 * Устарел. Использование новых бинов с аннотацией @Primary
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryFilmDao implements FilmDao {

    protected Map<Integer, Film> films = new HashMap<>();
    protected Integer id = 1;

    private final UserDao userDao;

    public Film createFilm(Film film) {
        Validators.filmValidation(film);
        film.setId(id);
        films.put(id++, film);
        log.info("New film created with id=" + (id-1));
        return film;
    }

    public Film getById(int id) {
        if (!films.containsKey(id)) {
            throw new FilmNotFoundException(id);
        }
        return films.get(id);
    }

    public Film updateFilm(Film film) {
        Validators.filmValidation(film);
        if (!films.containsKey(film.getId())) {
            throw new FilmNotFoundException(film.getId());
        }
        films.put(film.getId(), film);
        log.info("Updated film with id=" + film.getId());
        return film;
    }

    public List<Film> findAllFilms() {
        log.info("List of all movies has been sent");
        return new ArrayList<>(films.values());
    }

    public void addLike(int id, int userId) {
        Film film = getById(id);
        User user = userDao.getById(userId);
        film.getLikes().add(userId);
    }

    public void delLike(int id, int userId) {
        Film film = getById(id);
        User user = userDao.getById(userId);
        film.getLikes().remove(userId);
    }

    public List<Film> findPopular(int count) {
        List<Film> popFilms = new ArrayList<>(findAllFilms());
        return popFilms.stream()
                .sorted(Comparator.comparingInt(Film::getLikesCount).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

}
