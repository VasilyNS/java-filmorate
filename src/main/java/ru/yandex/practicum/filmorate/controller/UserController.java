package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
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
    Integer id = 1;

    @GetMapping("/users")
    public Collection<User> findAll() {
        log.info("Отправлен список всех пользователей");
        return users.values();
    }

    @PostMapping(value = "/users")
    User create(@RequestBody User user) {
        userValidation(user);
        user.setId(id);
        users.put(id++, user);
        log.info("Создан новый пользователь с id=" + (id-1));
        return user;
    }

    @PutMapping(value = "/users")
    User put(@RequestBody User user) {
        userValidation(user);
        if (!users.containsKey(user.getId())) {
            throw new ValidationException("Пользователь с таким id отсутствует");
        }
        users.put(user.getId(), user);
        log.info("Обновлен пользователь с id=" + user.getId());
        return user;
    }

    /**
     * электронная почта не может быть пустой и должна содержать символ @;
     * логин не может быть пустым и содержать пробелы;
     * имя для отображения может быть пустым — в таком случае будет использован логин;
     * дата рождения не может быть в будущем.
     * @param user
     */
    public void userValidation(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ '@'");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Пользователь с id=" + user.getId() + " с пустым именем, использован login");
        }
        LocalDate nd = LocalDate.now();
        if (nd.isBefore(user.getBirthday())) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

}
