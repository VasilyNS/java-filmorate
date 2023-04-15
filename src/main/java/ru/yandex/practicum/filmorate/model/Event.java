package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.enums.FeedEventType;
import ru.yandex.practicum.filmorate.enums.FeedOperation;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
public class Event {
    private final long timestamp;
    private final int userId;
    private final FeedEventType eventType;
    private final FeedOperation operation;
    private long eventId;
    private final long entityId;
}