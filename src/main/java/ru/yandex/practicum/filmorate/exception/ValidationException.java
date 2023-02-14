package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Используется для ответа с кодом ошибки 500
 * (HttpStatus.INTERNAL_SERVER_ERROR), как приемлемый для всех условий ТЗ
 */
@Slf4j
@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
        log.warn(message);
    }

}