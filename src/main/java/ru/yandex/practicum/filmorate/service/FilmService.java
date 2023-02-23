package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Класс для реализации операций с фильмами: добавление и удаление лайка,
 * вывод 10 наиболее популярных фильмов по количеству лайков.
 * Каждый пользователь может поставить лайк фильму только один раз.
 */
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserService userService;

    public Film getById(int id) {
        return filmStorage.getById(id);
    }

    public Film addLike(int id, int userId) {
        Film film = getById(id);
        User user = userService.getById(userId);
        film.getLikes().add(userId);
        return film;
    }

    public Film delLike(int id, int userId) {
        Film film = getById(id);
        User user = userService.getById(userId);
        film.getLikes().remove(userId);
        return film;
    }

    public List<Film> findPopular(int count) {
        List<Film> popFilms = new ArrayList<>(filmStorage.findAllFilms());
        return popFilms.stream()
                .sorted(Comparator.comparingInt(Film::getLikesCount).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

}
