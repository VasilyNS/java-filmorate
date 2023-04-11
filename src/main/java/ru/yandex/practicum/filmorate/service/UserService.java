package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.UserDao;

import java.util.List;

/**
 * Класс для реализации операций с пользователями, такими как
 * добавление в друзья, удаление из друзей, вывод списка общих друзей.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDao userDao;

    public User getById(int id) {
        return userDao.getById(id);
    }

    public void addToFriend(int id1, int id2) {
        userDao.addToFriend(id1, id2);
    }

    public List<User> findFriends(int id) {
        return userDao.findFriends(id);
    }

    public List<User> findCommonFriends(int id1, int id2) {
        return userDao.findCommonFriends(id1, id2);
    }

    public void deleteFromFriends(int id1, int id2) {
        userDao.deleteFromFriends(id1, id2);
    }

    public void deleteUser(int id) {
        userDao.deleteUser(id);
    }
}
