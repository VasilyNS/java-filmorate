package ru.yandex.practicum.filmorate.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserValidatorsTest {

    private User user;

    @BeforeEach
    void InitEach() {
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
                Validators.userValidation(user);
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
                        Validators.userValidation(user);
                    }
                });

        assertEquals("Email cannot be blank and must contain the '@' symbol", e.getMessage());

        final ValidationException e2 = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        user.setEmail("mail#mail.ru");
                        Validators.userValidation(user);
                    }
                });

        assertEquals("Email cannot be blank and must contain the '@' symbol", e2.getMessage());
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
                        Validators.userValidation(user);
                    }
                });

        assertEquals("Login cannot be empty or contain spaces", e.getMessage());

        final ValidationException e2 = assertThrows(ValidationException.class,
                new Executable() {
                    @Override
                    public void execute() {
                        user.setLogin("aaaa bbbb");
                        Validators.userValidation(user);
                    }
                });

        assertEquals("Login cannot be empty or contain spaces", e2.getMessage());
    }

    /**
     * Некорректно заполненный объект пользователя, пункт ТЗ:
     * имя для отображения может быть пустым — в таком случае будет использован логин;
     */
    @Test
    void userValidationTestForBlankName() {
        user.setName("");
        Validators.userValidation(user);
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
                        Validators.userValidation(user);
                    }
                });

        assertEquals("Date of birth cannot be in the future", e.getMessage());
    }

}