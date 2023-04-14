package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.EventDao;
import ru.yandex.practicum.filmorate.enums.FeedEventType;
import ru.yandex.practicum.filmorate.enums.FeedOperation;
import ru.yandex.practicum.filmorate.model.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventDaoImpl implements EventDao {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public void createFeed(Event event) {
        String sqlQuery = "INSERT INTO feed " +
                "(time_stamp, user_id, event_type, operation, entity_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(
                sqlQuery,
                event.getTimestamp(),
                event.getUserId(),
                event.getEventType().toString(),
                event.getOperation().toString(),
                event.getEntityId()
        );

        log.info("Event id: " + event.getEventId() + " added to feed");
    }

    @Override
    public List<Event> getFeed(int userId) {
        String sqlQuery = "SELECT * " +
                "FROM feed " +
                "WHERE user_id = ?";

        List<Event> feed = jdbcTemplate.query(sqlQuery, (rs, rowNum) -> makeEvent(rs), userId);

        log.info("Getting feed by user id: " + userId);

        return feed;
    }

    private Event makeEvent(ResultSet rs) throws SQLException {
        return new Event(rs.getLong("time_stamp"),
                rs.getInt("user_id"),
                FeedEventType.valueOf(rs.getString("event_type")),
                FeedOperation.valueOf(rs.getString("operation")),
                rs.getLong("event_id"),
                rs.getLong("entity_id"));
    }
}
