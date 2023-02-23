package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

/**
 * Интерфейс для определения методов добавления,
 * удаления и модификации объектов
 */
public interface UserStorage {

    Collection<User> findAllUsers();

    User createUser(User user);

    User updateUser(User user);

    User getById(int id);

}
