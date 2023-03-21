INSERT INTO RATING_BOOK (name) VALUES ('G');
INSERT INTO RATING_BOOK (name) VALUES ('PG');
INSERT INTO RATING_BOOK (name) VALUES ('PG-13');
INSERT INTO RATING_BOOK (name) VALUES ('R');
INSERT INTO RATING_BOOK (name) VALUES ('NC-17');
---------------------------------------------------------------------------------
INSERT INTO GENRE_BOOK (name) VALUES ('Комедия');
INSERT INTO GENRE_BOOK (name) VALUES ('Драма');
INSERT INTO GENRE_BOOK (name) VALUES ('Мультфильм');
INSERT INTO GENRE_BOOK (name) VALUES ('Триллер');
INSERT INTO GENRE_BOOK (name) VALUES ('Документальный');
INSERT INTO GENRE_BOOK (name) VALUES ('Боевик');
---------------------------------------------------------------------------------
INSERT INTO film (name, description, release_date, rating_id, duration)
VALUES ('Name film #1 qewrds', 'Desc film #1', '2015-12-21', 1, 121);

INSERT INTO film (name, description, release_date, rating_id, duration)
VALUES ('Name film #2 fdcrtr', 'Desc film #2', '2015-12-22', 1, 122);

INSERT INTO film (name, description, release_date, rating_id, duration)
VALUES ('Name film #3 xfdrvt', 'Desc film #3', '2015-12-23', 2, 123);

INSERT INTO film (name, description, release_date, rating_id, duration)
VALUES ('Name film #4 gfgrtt', 'Desc film #4', '2015-12-24', 4, 124);
---------------------------------------------------------------------------------
INSERT INTO users (email, login, name, birthday)
VALUES ('User1@mail.ru', 'user1login', 'user1name', '2000-11-21');

INSERT INTO users (email, login, name, birthday)
VALUES ('User2@mail.ru', 'user2login', 'user2name', '2000-11-22');

INSERT INTO users (email, login, name, birthday)
VALUES ('User3@mail.ru', 'user3login', 'user3name', '2000-11-23');

INSERT INTO users (email, login, name, birthday)
VALUES ('User4@mail.ru', 'user4login', 'user4name', '2000-11-24');

INSERT INTO users (email, login, name, birthday)
VALUES ('User5@mail.ru', 'user5login', 'user5name', '2000-11-25');
---------------------------------------------------------------------------------
INSERT INTO GENRE (film_id, genre_id) VALUES (1, 1);
INSERT INTO GENRE (film_id, genre_id) VALUES (1, 2);
INSERT INTO GENRE (film_id, genre_id) VALUES (1, 3);
INSERT INTO GENRE (film_id, genre_id) VALUES (1, 4);
INSERT INTO GENRE (film_id, genre_id) VALUES (2, 5);
INSERT INTO GENRE (film_id, genre_id) VALUES (3, 5);
INSERT INTO GENRE (film_id, genre_id) VALUES (3, 6);
INSERT INTO GENRE (film_id, genre_id) VALUES (4, 1);
INSERT INTO GENRE (film_id, genre_id) VALUES (4, 3);
INSERT INTO GENRE (film_id, genre_id) VALUES (4, 6);
---------------------------------------------------------------------------------
INSERT INTO LIKES (film_id, user_id) VALUES (1, 1);
INSERT INTO LIKES (film_id, user_id) VALUES (1, 2);
INSERT INTO LIKES (film_id, user_id) VALUES (1, 3);
INSERT INTO LIKES (film_id, user_id) VALUES (1, 5);
INSERT INTO LIKES (film_id, user_id) VALUES (2, 1);
INSERT INTO LIKES (film_id, user_id) VALUES (2, 5);
INSERT INTO LIKES (film_id, user_id) VALUES (3, 1);
INSERT INTO LIKES (film_id, user_id) VALUES (3, 3);
INSERT INTO LIKES (film_id, user_id) VALUES (4, 1);
INSERT INTO LIKES (film_id, user_id) VALUES (4, 2);
INSERT INTO LIKES (film_id, user_id) VALUES (4, 3);
---------------------------------------------------------------------------------
INSERT INTO FRIEND (user_id_1, user_id_2, confirm) VALUES (1, 2, TRUE);
INSERT INTO FRIEND (user_id_1, user_id_2, confirm) VALUES (1, 3, TRUE);
INSERT INTO FRIEND (user_id_1, user_id_2, confirm) VALUES (1, 4, FALSE);
INSERT INTO FRIEND (user_id_1, user_id_2, confirm) VALUES (1, 5, TRUE);
INSERT INTO FRIEND (user_id_1, user_id_2, confirm) VALUES (2, 5, TRUE);
INSERT INTO FRIEND (user_id_1, user_id_2, confirm) VALUES (5, 3, TRUE);
