package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FilmService filmService;

    /**
     * Добавление пользователя
     */
    @PostMapping("/users")
    public User create(@RequestBody User user) {
        return userService.createUser(user);
    }

    /**
     * Получение пользователя по id
     */
    @GetMapping("/users/{id}")
    public User getById(@PathVariable int id) {
        return userService.getById(id);
    }

    /**
     * Обновление пользователя
     */
    @PutMapping("/users")
    public User put(@RequestBody User user) {
        return userService.updateUser(user);
    }

    /**
     * Получение всех пользователей
     */
    @GetMapping("/users")
    public List<User> getAll() {
        return userService.findAllUsers();
    }

    /**
     * Добавление в друзья пользователей с id и friendId
     */
    @PutMapping("/users/{id}/friends/{friendId}")
    public void addToFriend(@PathVariable int id, @PathVariable int friendId) {
        userService.addToFriend(id, friendId);
    }

    /**
     * Список друзей пользователя с id
     */
    @GetMapping("/users/{id}/friends")
    public List<User> getFriends(@PathVariable int id) {
        return userService.findFriends(id);
    }

    /**
     * Получение списка друзей, общих с другим пользователем
     */
    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.findCommonFriends(id, otherId);
    }

    /**
     * Удаление из друзей
     */
    @DeleteMapping("/users/{id}/friends/{friendId}")
    public void deleteFromFriends(@PathVariable int id, @PathVariable int friendId) {
        userService.deleteFromFriends(id, friendId);
    }

    /**
     * Удаление пользователя
     */
    @DeleteMapping("/users/{userId}")
    public void deleteUser(@PathVariable int userId) {
        userService.deleteUser(userId);
    }

    /**
     * получение рекомендаций фильмов для пользователя
     */
    @GetMapping("/users/{id}/recommendations")
    public List<Film> getRecommendations(@PathVariable int id) {
        return filmService.getRecommendations(id);
    }


    /**
     * Получение ленты событий пользователя
     */
    @GetMapping("/users/{id}/feed")
    public List<Event> getUserFeed(@PathVariable int id) {
        return userService.getUserFeed(id);
    }
}
