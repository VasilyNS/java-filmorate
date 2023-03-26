package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.model.GenreBook;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreDao genreDao;

    public List<GenreBook> findAllGenreBook() {
        return genreDao.findAllGenreBook();
    }

    public GenreBook getGenreById(int id) {
        return genreDao.getGenreById(id);
    }

}
