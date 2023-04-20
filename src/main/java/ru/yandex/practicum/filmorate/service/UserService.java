package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.util.DateUtils;
import ru.yandex.practicum.filmorate.dao.EventDao;
import ru.yandex.practicum.filmorate.enums.FeedEventType;
import ru.yandex.practicum.filmorate.enums.FeedOperation;
import ru.yandex.practicum.filmorate.model.Event;
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
    private final EventDao eventDao;

    public User createUser(User user) {
        return userDao.createUser(user);
    }

    public User getById(int id) {
        return userDao.getById(id);
    }

    public User updateUser(User user) {
        return userDao.updateUser(user);
    }

    public List<User> findAllUsers() {
        return userDao.findAllUsers();
    }

    public void addToFriend(int id1, int id2) {
        userDao.addToFriend(id1, id2);

        Event event = new Event(
                DateUtils.now().toEpochMilli(),
                id1,
                FeedEventType.FRIEND,
                FeedOperation.ADD,
                id2
        );

        eventDao.createFeed(event);
    }

    public List<User> findFriends(int id) {
        return userDao.findFriends(id);
    }

    public List<User> findCommonFriends(int id1, int id2) {
        return userDao.findCommonFriends(id1, id2);
    }

    public void deleteFromFriends(int id1, int id2) {
        userDao.deleteFromFriends(id1, id2);

        Event event = new Event(
                DateUtils.now().toEpochMilli(),
                id1,
                FeedEventType.FRIEND,
                FeedOperation.REMOVE,
                id2
        );

        eventDao.createFeed(event);
    }

    public void deleteUser(int id) {
        userDao.deleteUser(id);
    }

    public List<Event> getUserFeed(int id) {
        userDao.checkUser(id);
        return eventDao.getFeed(id);
    }
}
