package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс для реализации операций с пользователями, такими как
 * добавление в друзья, удаление из друзей, вывод списка общих друзей.
 * Пользователям не надо одобрять заявки в друзья — добавляем сразу.
 * То есть если Лена стала другом Саши, то это значит,
 * что Саша теперь друг Лены.
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public User getById(int id) {
        return userStorage.getById(id);
    }

    public User addToFriend(int id, int friendId) {
        User user1 = getById(id);
        User user2 = getById(friendId);
        user1.getFriends().add(friendId);
        user2.getFriends().add(id);
        return user1;
    }

    public List<User> findCommonFriends(int id, int otherId) {
        List<User> commonFriends = new ArrayList<>();
        User user1 = getById(id);
        User user2 = getById(otherId);
        for (Integer f : user1.getFriends()) {
            User userMbFriend = getById(f);
            if (f != id && f != otherId && user2.getFriends().contains(f)) {
                commonFriends.add(userMbFriend);
            }
        }
        return commonFriends;
    }

    public List<User> findFriends(@PathVariable int id) {
        List<User> friends = new ArrayList<>();
        User user1 = getById(id);
        for (Integer f : user1.getFriends()) {
            User friend = getById(f);
            friends.add(friend);
        }
        return friends;
    }

    public User deleteFromFriends(@PathVariable int id, @PathVariable int friendId) {
        User user1 = getById(id);
        User user2 = getById(friendId);
        user1.getFriends().remove(friendId);
        user2.getFriends().remove(id);
        return user1;
    }

}
