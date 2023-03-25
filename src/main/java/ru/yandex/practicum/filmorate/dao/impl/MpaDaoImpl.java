package ru.yandex.practicum.filmorate.dao.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MpaDaoImpl implements MpaDao {

    private final JdbcTemplate jdbcTemplate;

    public List<Mpa> findAllMpa(){
        String sql = "SELECT * FROM rating_book";
        List<Mpa> allMpa = jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));

        log.info("List of all MPA has sent");
        return allMpa;
    }

    public Mpa getMpaById(int id) {
        String sql = "SELECT * FROM rating_book WHERE rating_id = ?";
        List<Mpa> allMpas = jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs), id);

        if (allMpas.size() == 0) {
            throw new MpaNotFoundException(id);
        } else {
            log.info("MPA was gotten with id=" + id);
            return allMpas.get(0);
        }
    }

    public String getMpaNameById(int id) {
        return getMpaById(id).getName();
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        return new Mpa(rs.getInt("rating_id"), rs.getString("name"));
    }

}
