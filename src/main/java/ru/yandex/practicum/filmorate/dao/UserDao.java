package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

/**
 * Интерфейс для определения методов добавления,
 * удаления и модификации объектов
 */
public interface UserDao {

    User createUser(User user);

    User getById(int id);

    User updateUser(User user);

    List<User> findAllUsers();

    void addToFriend(int id1, int id2);

    List<User> findFriends(int id);

    List<User> findCommonFriends(int id1, int id2);

    void deleteFromFriends(int id1, int id2);

}
