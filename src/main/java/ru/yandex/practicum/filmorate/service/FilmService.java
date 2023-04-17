package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.util.DateUtils;
import ru.yandex.practicum.filmorate.dao.EventDao;
import ru.yandex.practicum.filmorate.enums.FeedEventType;
import ru.yandex.practicum.filmorate.enums.FeedOperation;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.Constants;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Класс для реализации операций с фильмами: добавление и удаление лайка,
 * вывод 10 наиболее популярных фильмов по количеству лайков.
 * Каждый пользователь может поставить лайк фильму только один раз.
 */
@Service
@RequiredArgsConstructor
public class FilmService {

    private final FilmDao filmDao;
    private final EventDao eventDao;

    public Film getById(int id) {
        return filmDao.getById(id);
    }

    public Film createFilm(Film film) {
        return filmDao.createFilm(film);
    }

    public Film updateFilm(Film film) {
        return filmDao.updateFilm(film);
    }

    public Collection<Film> findAllFilms() {
        return filmDao.findAllFilms();
    }

    public void addLike(int id, int userId) {
        filmDao.addLike(id, userId);

        Event event = new Event(
                DateUtils.now().toEpochMilli(),
                userId,
                FeedEventType.LIKE,
                FeedOperation.ADD,
                id
        );

        eventDao.createFeed(event);
    }

    public void delLike(int id, int userId) {
        filmDao.delLike(id, userId);

        Event event = new Event(
                DateUtils.now().toEpochMilli(),
                userId,
                FeedEventType.LIKE,
                FeedOperation.REMOVE,
                id
        );

        eventDao.createFeed(event);
    }

    public List<Film> findPopular(int count, int genreId, int year) {
        return filmDao.findPopular(count, genreId, year);
    }

    public void deleteFilm(int id) {
        filmDao.deleteFilm(id);
    }

    public List<Film> getCommonFilms(int userId, int friendId) {
        return filmDao.getCommonFilms(userId, friendId);
    }

    public List<Film> getRecommendations(int id) {
        return filmDao.getRecommendations(id);
    }

    public List<Film> search(String query, List<String> by) {
        List<Film> films = new ArrayList<>();

        if (by.contains(Constants.SEARCH_FILM_BY_DIRECTOR)) {
            films.addAll(filmDao.searchByDir(query));
        }
        if (by.contains(Constants.SEARCH_FILM_BY_NAME)) {
            films.addAll(filmDao.searchByName(query));
        }

        return films;
    }

}
