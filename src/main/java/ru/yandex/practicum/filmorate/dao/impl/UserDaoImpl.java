package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.Friend;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.Validators;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Primary
@Slf4j
@Component
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {

    private final JdbcTemplate jdbcTemplate;

    public User createUser(User user) {
        Validators.userValidation(user);

        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");

        int id = simpleJdbcInsert.executeAndReturnKey(user.toMap()).intValue();
        user.setId(id);

        log.info("(VS1) New user was created with id=" + id);
        return user;
    }

    public User getById(int id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        List<User> allUsers = jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), id);

        if (allUsers.size() == 0) {
            throw new UserNotFoundException(id);
        } else {
            log.info("(VS2) User was gotten with id=" + id);
            return allUsers.get(0);
        }

    }

    public void checkUser(int id) {
        User check = getById(id);
    }

    public User updateUser(User user) {
        Validators.userValidation(user);
        checkUser(user.getId());

        String sql = "UPDATE users SET " +
                "email = ?, login = ?, name = ?, birthday = ? " +
                "WHERE user_id = ?";
        jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );

        log.info("(VS3) User was updated with id=" + user.getId());
        return user;
    }

    public List<User> findAllUsers() {
        String sql = "SELECT * FROM users";
        List<User> allUsers = jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));

        log.info("(VS4) List of all users has been sent");
        return allUsers;
    }

    public void addToFriend(int id1, int id2) {
        checkUser(id1);
        checkUser(id2);

        String sql = "INSERT INTO friend(user_id_1, user_id_2) VALUES (?, ?)";
        jdbcTemplate.update(sql, id1, id2);

        log.info("(VS5) User with id1=" + id1 + " became a friend of the user with id2=" + id2);
    }

    public List<User> findFriends(int id) {
        checkUser(id);
        List<User> userFriends = new ArrayList<>();

        String sql = "SELECT user_id_2 FROM friend WHERE user_id_1 = ?";
        List<Friend> friends = jdbcTemplate.query(sql, (rs, rowNum) -> makeFriend(rs), id);

        for (Friend friend : friends) {
            userFriends.add(getById(friend.getId2()));
        }

        log.info("(VS6) List of friends has been sent for user with id=" + id);
        return userFriends;
    }

    public List<User> findCommonFriends(int id1, int id2) {
        checkUser(id1);
        checkUser(id2);

        List<User> usersCommonFriends = new ArrayList<>();
        String sql = "(SELECT user_id_2 FROM friend WHERE user_id_1 = ?)" +
                "INTERSECT" +
                "(SELECT user_id_2 FROM friend WHERE user_id_1 = ?)";
        List<Friend> friends = jdbcTemplate.query(sql, (rs, rowNum) -> makeFriend(rs), id1, id2);

        for (Friend friend : friends) {
            usersCommonFriends.add(getById(friend.getId2()));
        }

        log.info("(VS7) List of common friends of users id1=" + id1 + " and id2=" + id2 + " has been sent");
        return usersCommonFriends;
    }

    public void deleteFromFriends(int id1, int id2) {
        checkUser(id1);
        checkUser(id2);

        String sqlQuery = "DELETE FROM friend WHERE user_id_1 = ? AND user_id_2 = ?";
        jdbcTemplate.update(sqlQuery, id1, id2);

        log.info("(VS8) User with id1=" + id1 + " has been removed from friends of user with id2=" + id2);
    }

    public void deleteUser(int id) {
        String sqlQuery = "DELETE FROM users WHERE user_id = ?";

        jdbcTemplate.update(sqlQuery, id);

        log.info("(RF1) User id: " + id + " deleted");
    }

    private User makeUser(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("email"),
                rs.getString("login"),
                rs.getString("name"),
                rs.getDate("birthday").toLocalDate()
        );
    }

    /**
     * В этой реализации makeFriend нужно только поле user_id_2, по нему строится список друзей.
     * Использование List<Friend>, а не List<Integer> в лямбде, сделано для масштабируемости проекта,
     * в будущем может возникнуть необходимость читать всё таблицу "friend", а не одну колонку.
     */
    private Friend makeFriend(ResultSet rs) throws SQLException {
        return new Friend(0, rs.getInt("user_id_2"));
    }

}
