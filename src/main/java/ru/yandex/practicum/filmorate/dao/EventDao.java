package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventDao {
    void createFeed(Event event);

    List<Event> getFeed(int userid);
}
