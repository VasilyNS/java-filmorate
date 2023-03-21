package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.Validators;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс для реализации логики хранения, обновления и поиска объектов
 */
@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    protected Map<Integer, User> users = new HashMap<>();
    protected Integer id = 1;

    public Collection<User> findAllUsers() {
        log.info("List of all users has been sent");
        return users.values();
    }

    public User createUser(User user) {
        Validators.userValidation(user);
        user.setId(id);
        users.put(id++, user);
        log.info("New user was created with id=" + (id - 1));
        return user;
    }

    public User updateUser(User user) {
        Validators.userValidation(user);
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException(user.getId());
        }
        users.put(user.getId(), user);
        log.info("Updated user with id=" + user.getId());
        return user;
    }

    public User getById(int id) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException(id);
        }
        return users.get(id);
    }

}
