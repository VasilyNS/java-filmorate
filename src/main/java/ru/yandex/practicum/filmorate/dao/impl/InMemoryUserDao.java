package ru.yandex.practicum.filmorate.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.Validators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Класс для реализации логики хранения В ПАМЯТИ, обновления и поиска объектов
 * Устарел. Использование новых бинов с аннотацией @Primary
 */
@Slf4j
@Component
public class InMemoryUserDao implements UserDao {

    protected Map<Integer, User> users = new HashMap<>();
    protected Integer id = 1;

    public User createUser(User user) {
        Validators.userValidation(user);
        user.setId(id);
        users.put(id++, user);
        log.info("New user was created with id=" + (id - 1));
        return user;
    }

    public User getById(int id) {
        if (!users.containsKey(id)) {
            throw new UserNotFoundException(id);
        }
        return users.get(id);
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

    public List<User> findAllUsers() {
        log.info("List of all users has been sent");
        return new ArrayList<>(users.values());
    }

    public void addToFriend(int id1, int id2){
        User user1 = getById(id1);
        User user2 = getById(id2);
        user1.getFriends().add(id2);
        user2.getFriends().add(id1);
    }

    public List<User> findFriends(int id) {
        List<User> friends = new ArrayList<>();
        User user1 = getById(id);
        for (Integer f : user1.getFriends()) {
            User friend = getById(f);
            friends.add(friend);
        }
        return friends;
    }

    public List<User> findCommonFriends(int id1, int id2) {
        List<User> commonFriends = new ArrayList<>();
        User user1 = getById(id1);
        User user2 = getById(id2);
        for (Integer f : user1.getFriends()) {
            User userMbFriend = getById(f);
            if (f != id1 && f != id2 && user2.getFriends().contains(f)) {
                commonFriends.add(userMbFriend);
            }
        }
        return commonFriends;
    }

    public void deleteFromFriends(int id1, int id2) {
        User user1 = getById(id1);
        User user2 = getById(id2);
        user1.getFriends().remove(id2);
        user2.getFriends().remove(id1);
    }

}
