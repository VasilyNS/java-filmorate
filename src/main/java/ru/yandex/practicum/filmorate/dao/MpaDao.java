package ru.yandex.practicum.filmorate.dao;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;


public interface MpaDao {
    List<Mpa> findAllMpa();
    Mpa getMpaById(int id);
    String getMpaNameById(int id);
}