package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabase;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import ru.yandex.practicum.filmorate.dao.FilmDao;
import ru.yandex.practicum.filmorate.dao.GenreDao;
import ru.yandex.practicum.filmorate.dao.MpaDao;
import ru.yandex.practicum.filmorate.dao.UserDao;
import ru.yandex.practicum.filmorate.dao.impl.FilmDaoImpl;
import ru.yandex.practicum.filmorate.dao.impl.GenreDaoImpl;
import ru.yandex.practicum.filmorate.dao.impl.MpaDaoImpl;
import ru.yandex.practicum.filmorate.dao.impl.UserDaoImpl;
import ru.yandex.practicum.filmorate.model.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
/**
 * Две следующих аннотации есть в ТЗ, но по факту не используются
 * @AutoConfigureTestDatabase
 * @RequiredArgsConstructor(onConstructor_ = @Autowired)
 * Интеграционное тестирование выполнено в соответствии с официальной документацией
 * https://docs.spring.io/spring-framework/docs/4.2.x/spring-framework-reference/html/jdbc.html
 * Каждый тестовый метод при стартует с абсолютно одинаковым состоянием БД
 */
class FilmorateApplicationDbImplTest {

    private JdbcTemplate jdbcTemplate;
    private EmbeddedDatabase edb;

    private UserDao userDao;
    private FilmDao filmDao;
    private GenreDao genreDao;
    private MpaDao mpaDao;

    private User testuser;
    private Film testfilm;

    @BeforeEach
    public void setUp() {
        edb = new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .addDefaultScripts()
                .addScript("/testdata.sql")
                .build();
        jdbcTemplate = new JdbcTemplate(edb);

        userDao = new UserDaoImpl(jdbcTemplate);
        genreDao = new GenreDaoImpl(jdbcTemplate);
        mpaDao = new MpaDaoImpl(jdbcTemplate);
        filmDao = new FilmDaoImpl(userDao, mpaDao, genreDao, jdbcTemplate);

        testuser = new User(333, "test@E.RU", "testL", "testN",
                LocalDate.of(2001, 12, 25));
        testfilm = new Film(333, "testN", "testD", LocalDate.of(2001, 12, 25),
                177, new Mpa(1, ""), new ArrayList<GenreBook>());

    }

    @AfterEach
    public void shutDown() {
        edb.shutdown();
    }

    /**
     * Вывести в консоль список объектов для проверки:
     * ul.forEach(o->System.out.println(o.getName()));
     */
    @Test
    public void testUserDaoCreateUser() {
        List<User> ul = userDao.findAllUsers();
        assertEquals(5, ul.size());

        userDao.createUser(testuser);
        userDao.createUser(testuser);
        ul = userDao.findAllUsers();

        assertEquals(7, ul.size());

        Optional<User> u = Optional.of(userDao.getById(7));

        assertThat(u).isPresent()
                .hasValueSatisfying(o -> assertThat(o)
                        .hasFieldOrPropertyWithValue("name", "testN"));
    }

    @Test
    public void testUserDaoGetById() {
        List<User> ul = userDao.findAllUsers();
        assertEquals(5, ul.size());

        userDao.createUser(testuser);
        userDao.createUser(testuser);
        ul = userDao.findAllUsers();
        assertEquals(7, ul.size());

        Optional<User> u = Optional.of(userDao.getById(7));
        assertThat(u).isPresent()
                .hasValueSatisfying(o -> assertThat(o)
                        .hasFieldOrPropertyWithValue("name", "testN"));
    }

    @Test
    public void testUserDaoUpdateUser() {
        List<User> ul = userDao.findAllUsers();
        assertEquals(5, ul.size());

        Optional<User> u = Optional.of(userDao.getById(3));
        assertThat(u).isPresent()
                .hasValueSatisfying(o -> assertThat(o)
                        .hasFieldOrPropertyWithValue("name", "user3name"));

        testuser.setId(3);
        userDao.updateUser(testuser);

        u = Optional.of(userDao.getById(3));
        assertThat(u).isPresent()
                .hasValueSatisfying(o -> assertThat(o)
                        .hasFieldOrPropertyWithValue("name", "testN"));

        ul = userDao.findAllUsers();
        assertEquals(5, ul.size());
    }

    @Test
    public void testUserDaoFindAllUsers() {
        List<User> ul = userDao.findAllUsers();
        assertEquals(5, ul.size());

        Optional<User> u = Optional.of(userDao.getById(3));
        assertThat(u).isPresent()
                .hasValueSatisfying(o -> assertThat(o)
                        .hasFieldOrPropertyWithValue("name", "user3name"));
    }

    @Test
    public void testUserDaoAddToFriend() {
        List<User> ul = userDao.findAllUsers();
        assertEquals(5, ul.size());

        List<User> uf = userDao.findFriends(2);
        assertEquals(1, uf.size());

        Optional<User> u = Optional.of(uf.get(0));
        assertThat(u).isPresent()
                .hasValueSatisfying(o -> assertThat(o)
                        .hasFieldOrPropertyWithValue("name", "user5name"));

        userDao.addToFriend(2, 4);

        uf = userDao.findFriends(2);
        assertEquals(2, uf.size());

        u = Optional.of(uf.get(0));
        assertThat(u).isPresent()
                .hasValueSatisfying(o -> assertThat(o)
                        .hasFieldOrPropertyWithValue("name", "user4name"));
    }


    @Test
    public void testUserDaoFindFriends() {
        List<User> ul = userDao.findAllUsers();
        assertEquals(5, ul.size());

        List<User> uf = userDao.findFriends(1);
        assertEquals(4, uf.size());

        Optional<User> u = Optional.of(uf.get(0));
        assertThat(u).isPresent()
                .hasValueSatisfying(o -> assertThat(o)
                        .hasFieldOrPropertyWithValue("name", "user2name"));
    }

    @Test
    public void testUserDaoFindCommonFriends() {
        List<User> ul = userDao.findAllUsers();
        assertEquals(5, ul.size());

        List<User> ucf = userDao.findCommonFriends(1,2);
        assertEquals(1, ucf.size());

        Optional<User> u = Optional.of(ucf.get(0));
        assertThat(u).isPresent()
                .hasValueSatisfying(o -> assertThat(o)
                        .hasFieldOrPropertyWithValue("name", "user5name"));
    }

    @Test
    public void testUserDaoDeleteFromFriends() {
        List<User> ul = userDao.findAllUsers();
        assertEquals(5, ul.size());

        List<User> uf = userDao.findFriends(1);
        assertEquals(4, uf.size());

        userDao.deleteFromFriends(1,3);

        uf = userDao.findFriends(1);
        assertEquals(3, uf.size());
    }

    @Test
    public void testFilmDaoCreateFilm() {
        List<Film> fl = filmDao.findAllFilms();
        assertEquals(4, fl.size());

        filmDao.createFilm(testfilm);

        fl = filmDao.findAllFilms();
        assertEquals(5, fl.size());

        Optional<Film> f = Optional.of(filmDao.getById(5));
        assertThat(f).isPresent()
                .hasValueSatisfying(o -> assertThat(o)
                        .hasFieldOrPropertyWithValue("name", "testN"));
    }

    @Test
    public void testFilmDaoGetById() {
        List<Film> fl = filmDao.findAllFilms();
        assertEquals(4, fl.size());

        Optional<Film> f = Optional.of(filmDao.getById(4));
        assertThat(f).isPresent()
                .hasValueSatisfying(o -> assertThat(o)
                        .hasFieldOrPropertyWithValue("name", "Name film #4 gfgrtt"));
    }

    @Test
    public void testFilmDaoUpdateFilm() {
        List<Film> fl = filmDao.findAllFilms();
        assertEquals(4, fl.size());

        testfilm.setId(3);
        filmDao.updateFilm(testfilm);

        Optional<Film> f = Optional.of(filmDao.getById(3));
        assertThat(f).isPresent()
                .hasValueSatisfying(o -> assertThat(o)
                        .hasFieldOrPropertyWithValue("name", "testN"));
    }

    @Test
    public void testFilmDaoFindAllFilms() {
        List<Film> fl = filmDao.findAllFilms();
        assertEquals(4, fl.size());

        Optional<Film> f = Optional.of(filmDao.getById(4));
        assertThat(f).isPresent()
                .hasValueSatisfying(o -> assertThat(o)
                        .hasFieldOrPropertyWithValue("name", "Name film #4 gfgrtt"));
    }

    @Test
    public void testFilmDaoAddLike() {
        List<Film> fl = filmDao.findAllFilms();
        assertEquals(4, fl.size());

        List<Film> pfl = filmDao.findPopular(1);
        Optional<Film> f = Optional.of(pfl.get(0));
        assertThat(f).isPresent()
                .hasValueSatisfying(o -> assertThat(o)
                        .hasFieldOrPropertyWithValue("name", "Name film #1 qewrds"));
        assertEquals(1, pfl.size());

        pfl.forEach(o->System.out.println(o.getName()));
        System.out.println();

        filmDao.addLike(3, 1);
        filmDao.addLike(3, 2);
        filmDao.addLike(3, 3);
        filmDao.addLike(3, 4);
        filmDao.addLike(3, 5);

        pfl = filmDao.findPopular(1000);
        f = Optional.of(pfl.get(0));
        assertThat(f).isPresent()
                .hasValueSatisfying(o -> assertThat(o)
                        .hasFieldOrPropertyWithValue("name", "Name film #3 xfdrvt"));
    }

    @Test
    public void testFilmDaoDelLike() {
        List<Film> fl = filmDao.findAllFilms();
        assertEquals(4, fl.size());

        List<Film> pfl = filmDao.findPopular(1000);
        Optional<Film> f = Optional.of(pfl.get(0));
        assertThat(f).isPresent()
                .hasValueSatisfying(o -> assertThat(o)
                        .hasFieldOrPropertyWithValue("name", "Name film #1 qewrds"));

        filmDao.delLike(1,1);
        filmDao.delLike(1,2);
        filmDao.delLike(1,3);
        filmDao.delLike(1,4);

        pfl = filmDao.findPopular(1000);

        f = Optional.of(pfl.get(0));
        assertThat(f).isPresent()
                .hasValueSatisfying(o -> assertThat(o)
                        .hasFieldOrPropertyWithValue("name", "Name film #4 gfgrtt"));
    }

    @Test
    public void testFilmDaoFindPopular() {
        List<Film> fl = filmDao.findAllFilms();
        assertEquals(4, fl.size());

        List<Film> pfl = filmDao.findPopular(1);
        assertEquals(1, pfl.size());

        Optional<Film> f = Optional.of(pfl.get(0));
        assertThat(f).isPresent()
                .hasValueSatisfying(o -> assertThat(o)
                        .hasFieldOrPropertyWithValue("name", "Name film #1 qewrds"));
    }

    @Test
    public void testGenreDaoFindAllGenreBook() {
        List<GenreBook> gbl = genreDao.findAllGenreBook();
        assertEquals(6, gbl.size());

        Optional<GenreBook> gb = Optional.of(gbl.get(5));
        assertThat(gb).isPresent()
                .hasValueSatisfying(o -> assertThat(o)
                        .hasFieldOrPropertyWithValue("name", "Боевик"));
    }

    @Test
    public void testGenreDaoGetGenreById() {
        List<GenreBook> gbl = genreDao.findAllGenreBook();
        assertEquals(6, gbl.size());

        Optional<GenreBook> gb = Optional.of(genreDao.getGenreById(3));

        assertThat(gb).isPresent()
                .hasValueSatisfying(o -> assertThat(o)
                        .hasFieldOrPropertyWithValue("name", "Мультфильм"));
    }

    @Test
    public void testGenreDaoGetGenreNameById() {
        List<GenreBook> gbl = genreDao.findAllGenreBook();
        assertEquals(6, gbl.size());

        Optional<String> gn = Optional.of(genreDao.getGenreNameById(3));

        assertThat(gn).isPresent();
        assertEquals("Мультфильм", gn.get());
    }

    @Test
    public void testGenreDaoAddGenresToDbForFilm() {
        List<GenreBook> g = filmDao.getById(1).getGenres();
        // {0, Комедия}, {1, Драма}, {2, Мультфильм}, {3, Триллер}
        assertEquals(4, g.size());

        Optional<GenreBook> gtest = Optional.of(g.get(3));
        assertThat(gtest).isPresent()
                .hasValueSatisfying(o -> assertThat(o)
                        .hasFieldOrPropertyWithValue("name", "Триллер"));

        genreDao.delAllGenresInDbForFilm(1);
        g = filmDao.getById(1).getGenres();
        assertEquals(0, g.size());

        List<GenreBook> newGbl = new ArrayList<>();
        GenreBook newGb = new GenreBook(5, "Документальный");
        newGbl.add(newGb);

        genreDao.addGenresToDbForFilm(1, newGbl);
        g = filmDao.getById(1).getGenres();

        assertEquals(1, g.size());
        gtest = Optional.of(g.get(0));
        assertThat(gtest).isPresent()
                .hasValueSatisfying(o -> assertThat(o)
                        .hasFieldOrPropertyWithValue("id", 5)
                        .hasFieldOrPropertyWithValue("name", "Документальный"));
    }

    @Test
    public void testGenreDaoDelAllGenresInDbForFilm() {
        List<GenreBook> g = filmDao.getById(1).getGenres();
        assertEquals(4, g.size());

        genreDao.delAllGenresInDbForFilm(1);

        g = filmDao.getById(1).getGenres();
        assertEquals(0, g.size());
    }

    @Test
    public void testGenreDaoFindAllGenresForFilm() {
        List<GenreBook> g = genreDao.findAllGenresForFilm(1);
        // {1, Комедия}, {2, Драма}, {3, Мультфильм}, {4, Триллер}
        assertEquals(4, g.size());

        Optional<GenreBook> gtest = Optional.of(g.get(3));
        assertThat(gtest).isPresent();
        assertEquals(4, gtest.get().getId());
    }

    @Test
    public void testMpaDaoFindAllMpa() {
        List<Mpa> ml = mpaDao.findAllMpa();
        assertEquals(5, ml.size());

        Optional<Mpa> mtest = Optional.of(ml.get(4));
        assertThat(mtest).isPresent()
                .hasValueSatisfying(o -> assertThat(o)
                        .hasFieldOrPropertyWithValue("id", 5)
                        .hasFieldOrPropertyWithValue("name", "NC-17"));
    }

    @Test
    public void testMpaDaoGetMpaById() {
        Optional<Mpa> mtest = Optional.of(mpaDao.getMpaById(5));

        assertThat(mtest).isPresent()
                .hasValueSatisfying(o -> assertThat(o)
                        .hasFieldOrPropertyWithValue("id", 5)
                        .hasFieldOrPropertyWithValue("name", "NC-17"));
    }

    @Test
    public void testMpaDaoGetMpaNameById() {
        Optional<String> mtest = Optional.of(mpaDao.getMpaNameById(5));

        assertThat(mtest).isPresent()
                .hasValue("NC-17");
    }

}
