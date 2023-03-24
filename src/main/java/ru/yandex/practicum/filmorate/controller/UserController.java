package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.dao.UserDao;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final UserDao userDao;
    private final UserService userService;

    /**
     * Добавление пользователя
     */
    @PostMapping(value = "/users")
    public User create(@RequestBody User user) {
        return userDao.createUser(user);
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
    @PutMapping(value = "/users")
    public User put(@RequestBody User user) {
        return userDao.updateUser(user);
    }

    /**
     * Получение всех пользователей
     */
    @GetMapping("/users")
    public List<User> getAll() {
        return userDao.findAllUsers();
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

}
