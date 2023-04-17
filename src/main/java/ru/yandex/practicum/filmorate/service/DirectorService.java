package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.dao.DirectorDao;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.model.DirectorBook;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorDao directorDao;
    private final FilmDao filmDao;

    public List<DirectorBook> findAllDirectors() {
        return directorDao.findAllDirectors();
    }

    public DirectorBook getDirectorById(int id) {
        return directorDao.getDirectorById(id);
    }

    public DirectorBook createDirector(DirectorBook directorBook) {
        return directorDao.createDirector(directorBook);
    }

    public DirectorBook updateDirector(DirectorBook directorBook) {
        return directorDao.updateDirector(directorBook);
    }

    public void delById(int id) {
        directorDao.delById(id);
    }

    public List<Film> findByDirWithSort(int id, String sortBy) {
        return filmDao.findByDirWithSort(id, sortBy);
    }




}
