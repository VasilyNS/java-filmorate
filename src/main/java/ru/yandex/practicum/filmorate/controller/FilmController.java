package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class FilmController {
    private final FilmStorage filmStorage;
    private final FilmService filmService;

    /**
     * Получние списка всех фильмов
     * @return
     */
    @GetMapping("/films")
    public Collection<Film> getAll() {
        return filmStorage.findAllFilms();
    }

    /**
     * Добавление фильма
     * @param film
     * @return
     */
    @PostMapping(value = "/films")
    Film create(@RequestBody Film film) {
        return filmStorage.createFilm(film);
    }

    /**
     * Обновление фильма с использованием id в body
     * @param film
     * @return
     */
    @PutMapping(value = "/films")
    Film put(@RequestBody Film film) {
        return filmStorage.updateFilm(film);
    }

    /**
     * получение фильма по id
     * @param id
     * @return
     */
    @GetMapping("/films/{id}")
    public Film getById(@PathVariable int id) {
        return filmService.getById(id);
    }

    /**
     * Пользователь ставит лайк фильму
     */
    @PutMapping(value = "/films/{id}/like/{userId}")
    public Film addLike(@PathVariable int id, @PathVariable int userId) {
        return filmService.addLike(id, userId);
    }

    /**
     * Пользователь удаляет лайк
     */
    @DeleteMapping(value = "/films/{id}/like/{userId}")
    public Film delLike(@PathVariable int id, @PathVariable int userId) {
        return filmService.delLike(id, userId);
    }

    /**
     * Возвращает список из первых count фильмов по количеству лайков.
     * Если значение параметра count не задано, то count = 10.
     */
    @GetMapping("/films/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10", required = false) Integer count) {
        return filmService.findPopular(count);
    }

}
