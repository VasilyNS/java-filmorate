package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.GenreBook;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping("/genres")
    public List<GenreBook> findAllGenreBook() {
        return genreService.findAllGenreBook();
    }

    @GetMapping("/genres/{id}")
    public GenreBook getGenreById(@PathVariable int id) {
        return genreService.getGenreById(id);
    }

}
