package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.dao.FilmDao;

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

    public Film getById(int id) {
        return filmDao.getById(id);
    }

    public void addLike(int id, int userId) {
        filmDao.addLike(id, userId);
    }

    public void delLike(int id, int userId) {
        filmDao.delLike(id, userId);
    }

    public List<Film> findPopular(int count) {
        return filmDao.findPopular(count);
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

}
