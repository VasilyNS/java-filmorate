<h2 align="center">
    ����� �� ������� java-filmorate<br><br>
    <img src="db.png">
</h2>

<h3 align="center">
    �������� ������ � ��:
</h3>

- **"FILM"** - ��� ������ �������
- **"USERS"** - ��� ������������ �������
- **"GENRE"** - ������� ��� ������ ���� � ������, ��� ��� � ������ ����� ���� ����� ������, 
����� ����� ������ ���� ������ ��� ���� �������, ��������������, �� film_id ����� ��������
������ ���� ������ ��� ����� ������
- **"GENRE_BOOK"** - ���������� ������
- **"RATING_BOOK"** - ���������� ���������
- **"LIKES"** - ���������� ������� **"GENRE"** ��������� ������ � �������������, ������ ��� �����.
�� user_id ����� �������� ��� ����� (id �������), ������� �������� ���� ������������.
�� film_id ����� �������� ��� ����� (id �������������), ������� ������������ ��������� ���� ������. 
- **"FRIEND"** - ������� ��� �������� ������ ����� ��������������. ���� ������ - ���� ������ ������. ���� ����
**"confirm"** ���������� � **FALSE**, �� ������ �������������, �� ���� ��� user_id_1 ������ ������������ ��������
������, � ������� - ���. ��� �������� **TRUE** - user_id_2 ���� �������� ������ user_id_1.

*������� "LIKES" � "USERS" ������� �� ������������� �����, ����� �� ������������� � ������������������ ������� SQL.*

<h3 align="center">
    ������� �������� SQL-�������� � ��
</h3>

����� ���� ������� �� ��������, ������� ��������� � ���������, � �� ������ �� id:
```sql
SELECT f.name, f.description, rb.name AS RATING_NAME, f.release_date, f.duration FROM film AS f
LEFT JOIN rating_book AS rb ON f.rating_id = rb.rating_id
ORDER BY f.name 
```
����� ���� �������� ������ ��� ������ �� film_id (��� ������� 4):
```sql
SELECT f.film_id, f.name, gb.name FROM genre AS g 
LEFT JOIN genre_book AS gb ON gb.genre_id = g.genre_id
LEFT JOIN film AS f ON g.film_id = f.film_id
WHERE g.film_id = 4
```
����� ���� ���� �������������, ��� �������� ���� ������ �� film_id (��� ������� 4): 
```sql
SELECT u.name FROM likes AS l 
LEFT JOIN users AS u ON l.user_id = u.user_id
WHERE l.film_id = 4
```
����� ���� ���� ������ ������������ �� user_id (��� ������� 5):
```sql
SELECT u.name FROM users AS u WHERE u.user_id IN
(SELECT f.user_id_2 AS frend FROM friend AS f WHERE f.user_id_1 = 5
UNION ALL
SELECT f.user_id_1 AS frend FROM friend AS f WHERE f.user_id_2 = 5 AND confirm = TRUE)
```
����� ���� ����� ������ ���� ������������� (��� ������� 5 � 2):
```sql
(SELECT u.user_id AS id FROM users AS u WHERE u.user_id IN
(SELECT f.user_id_2 AS frend  FROM friend AS f 
WHERE f.user_id_1 = 5
UNION ALL
SELECT f.user_id_1 AS frend FROM friend AS f
WHERE f.user_id_2 = 5 AND confirm = TRUE))
INTERSECT
(SELECT u.user_id AS id FROM users AS u WHERE u.user_id IN
(SELECT f.user_id_2 AS frend  FROM friend AS f 
WHERE f.user_id_1 = 2
UNION ALL
SELECT f.user_id_1 AS frend FROM friend AS f
WHERE f.user_id_2 = 2 AND confirm = TRUE))
```

 ��� ������� ��������� �� �������� ����.