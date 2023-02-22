package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.List;

@RestController
public class UserController {
    private final UserStorage userStorage;
    private final UserService userService;

    UserController(UserStorage userStorage, UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
    }

    /**
     * Получение всех пользователей
     * @return
     */
    @GetMapping("/users")
    Collection<User> findAll() {
        return userStorage.findAllUsers();
    }

    /**
     * Добавление пользователя
     * @param user
     * @return
     */
    @PostMapping(value = "/users")
    User create(@RequestBody User user) {
        return userStorage.createUser(user);
    }

    /**
     * Обновление пользователя
     * @param user
     * @return
     */
    @PutMapping(value = "/users")
    User put(@RequestBody User user) {
        return userStorage.updateUser(user);
    }

    /**
     * Получение пользователя по id
     * @param id
     * @return
     */
    @GetMapping("/users/{id}")
    public User findById(@PathVariable int id) {
        return userService.findById(id);
    }

    /**
     * Добавление в друзья пользователей с id и friendId
     * @param id
     * @param friendId
     * @return
     */
    @PutMapping("/users/{id}/friends/{friendId}")
    public User addToFriend(@PathVariable int id, @PathVariable int friendId) {
        return userService.addToFriend(id, friendId);
    }

    /**
     * Получение списка друзей, общих с другим пользователем
     * @param id
     * @param otherId
     * @return
     */
    @GetMapping("/users/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    /**
     * Список друзей пользователя с id
     * @param id
     * @return
     */
    @GetMapping("/users/{id}/friends")
    public List<User> getFriends(@PathVariable int id) {
        return userService.getFriends(id);
    }

    /**
     * Удаление из друзей
     * @param id
     * @param friendId
     * @return
     */
    @DeleteMapping("/users/{id}/friends/{friendId}")
    public User deleteFromFriends(@PathVariable int id, @PathVariable int friendId) {
        return userService.deleteFromFriends(id, friendId);
    }

}
