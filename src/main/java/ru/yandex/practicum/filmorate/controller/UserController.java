package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.Validators;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * создание пользователя;
 * обновление пользователя;
 * получение списка всех пользователей.
 */
@Slf4j
@RestController
public class UserController {

    private Map<Integer, User> users = new HashMap<>();
    private Integer id = 1;

    @GetMapping("/users")
    public Collection<User> findAll() {
        log.info("List of all users has been sent");
        return users.values();
    }

    @PostMapping(value = "/users")
    User create(@RequestBody User user) {
        Validators.userValidation(user);
        user.setId(id);
        users.put(id++, user);
        log.info("New user was created with id=" + (id-1));
        return user;
    }

    @PutMapping(value = "/users")
    User put(@RequestBody User user) {
        Validators.userValidation(user);
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("No user with this id");
        }
        users.put(user.getId(), user);
        log.info("Updated user with id=" + user.getId());
        return user;
    }

}
