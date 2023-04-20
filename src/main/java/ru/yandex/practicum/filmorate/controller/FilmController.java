package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class FilmController {

    private final FilmService filmService;

    /**
     * Добавление фильма
     */
    @PostMapping("/films")
    public Film create(@RequestBody Film film) {
        return filmService.createFilm(film);
    }

    /**
     * получение фильма по id
     */
    @GetMapping("/films/{id}")
    public Film getById(@PathVariable int id) {
        return filmService.getById(id);
    }

    /**
     * Обновление фильма с использованием id в body
     */
    @PutMapping("/films")
    public Film put(@RequestBody Film film) {
        return filmService.updateFilm(film);
    }

    /**
     * Получение списка всех фильмов
     */
    @GetMapping("/films")
    public Collection<Film> getAll() {
        return filmService.findAllFilms();
    }

    /**
     * Пользователь ставит лайк фильму
     */
    @PutMapping("/films/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        filmService.addLike(id, userId);
    }

    /**
     * Пользователь удаляет лайк
     */
    @DeleteMapping("/films/{id}/like/{userId}")
    public void delLike(@PathVariable int id, @PathVariable int userId) {
        filmService.delLike(id, userId);
    }

    /**
     * Возвращает список самых популярных фильмов указанного жанра за нужный год.
     * Если жанр или год не указаны, то выводит список из первых count фильмов по количеству лайков.
     * Если значение параметра count не задано, то count = 10.
     */
    @GetMapping("/films/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10", required = false) Integer count,
                                 @RequestParam(defaultValue = "0", required = false) Integer genreId,
                                 @RequestParam(defaultValue = "0", required = false) Integer year) {
        return filmService.findPopular(count, genreId, year);
    }

    /**
     * Удаление фильма
     */
    @DeleteMapping("/films/{filmId}")
    public void deleteFilm(@PathVariable int filmId) {
        filmService.deleteFilm(filmId);
    }

    /**
     * Вывод общих фильмов с другим пользователем
     */
    @GetMapping("/films/common")
    public List<Film> getCommonFilms(@RequestParam int userId, @RequestParam int friendId) {
        return filmService.getCommonFilms(userId, friendId);
    }

    @GetMapping("/films/search")
    public List<Film> search(@RequestParam String query, @RequestParam List<String> by) {
        return filmService.search(query, by);
    }

}
