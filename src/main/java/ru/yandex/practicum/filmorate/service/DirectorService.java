package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorDao directorDao;
    private final FilmDao filmDao;

    public List<Film> findByDirWithSort(int id, String sortBy) {
        return filmDao.findByDirWithSort(id, sortBy);
    }

}
