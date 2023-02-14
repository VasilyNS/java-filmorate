package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Аннотация для класса @SpringBootTest нужна для запуска SpringBoot
 * Тут тестируется только методы валидации. Сообщения, например:
 * "23:42:52.491 [main] WARN r.y.p.filmorate.exception.ValidationException
 * - Электронная почта не может быть пустой и должна содержать символ '@'"
 * При тестировании всё равно будут попадать в лог при срабатывании исключений
 */
class UserControllerTest {

    User user;
    final UserController userController = new UserController();

    @BeforeEach
    public void InitEach() {
        user = new User();
        user.setLogin("dolore");
        user.setName("Nick Name");
        user.setEmail("mail@mail.ru");
        user.setBirthday(LocalDate.of(2000, 1, 1));
    }

    /**
     * Корректно заполненный объект пользователя не должен вызывать исключений
     */
    @Test
    void userValidationTestForCorrectData() {
        assertDoesNotThrow(new Executable() {
            @Override
            public void execute() {
                userController.userValidation(user);
            }
        });
    }

    /**
     * Некорректно заполненный объект пользователя, пункт ТЗ:
     * электронная почта не может быть пустой и должна содержать символ @;
     */
    @Test
    void userValidationTestForIncorrectEmail() {
        final ValidationException e = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        user.setEmail("");
                        userController.userValidation(user);
                    }
                });

        assertEquals("Электронная почта не может быть пустой и должна содержать символ '@'", e.getMessage());

        final ValidationException e2 = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        user.setEmail("mail#mail.ru");
                        userController.userValidation(user);
                    }
                });

        assertEquals("Электронная почта не может быть пустой и должна содержать символ '@'", e2.getMessage());
    }

    /**
     * Некорректно заполненный объект пользователя, пункт ТЗ:
     * логин не может быть пустым и содержать пробелы;
     */
    @Test
    void userValidationTestForIncorrectLogin() {
        final ValidationException e = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        user.setLogin("");
                        userController.userValidation(user);
                    }
                });

        assertEquals("Логин не может быть пустым и содержать пробелы", e.getMessage());

        final ValidationException e2 = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        user.setLogin("aaaa bbbb");
                        userController.userValidation(user);
                    }
                });

        assertEquals("Логин не может быть пустым и содержать пробелы", e2.getMessage());
    }

    /**
     * Некорректно заполненный объект пользователя, пункт ТЗ:
     * имя для отображения может быть пустым — в таком случае будет использован логин;
     */
    @Test
    void userValidationTestForBlankName() {
        user.setName("");
        userController.userValidation(user);
        assertEquals("dolore", user.getName());
    }

    /**
     * Некорректно заполненный объект пользователя, пункт ТЗ:
     * дата рождения не может быть в будущем.
     */
    @Test
    void userValidationTestForIncorrectBirthday() {
        final ValidationException e = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        LocalDate nowPlusOneDay = LocalDate.now().plusDays(1);
                        user.setBirthday(nowPlusOneDay);
                        userController.userValidation(user);
                    }
                });

        assertEquals("Дата рождения не может быть в будущем", e.getMessage());
    }

}
