package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.DirectorBook;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.DirectorService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    @GetMapping("/directors")
    public List<DirectorBook> findAllDirectors() {
        return directorService.findAllDirectors();
    }

    @GetMapping("/directors/{id}")
    public DirectorBook getDirectorById(@PathVariable int id) {
        return directorService.getDirectorById(id);
    }

    @PostMapping("/directors")
    public DirectorBook create(@RequestBody DirectorBook directorBook) {
        return directorService.createDirector(directorBook);
    }

    @PutMapping("/directors")
    public DirectorBook put(@RequestBody DirectorBook directorBook) {
        return directorService.updateDirector(directorBook);
    }

    /**
     * Возвращает список фильмов по указанному режиссеру с указанием сортировки
     * /films/director/:directorId?sortBy=year
     * или
     * /films/director/:directorId?sortBy=likes
     */
    @GetMapping("/films/director/{id}")
    public List<Film> getByDir(@PathVariable int id, @RequestParam String sortBy) {
        return directorService.findByDirWithSort(id, sortBy);
    }

    @DeleteMapping("/directors/{id}")
    public void delByDir(@PathVariable int id) {
        directorService.delById(id);
    }

}

