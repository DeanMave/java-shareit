package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidation(final ValidationException e) {
        log.warn("Ошибка при валидации данных: {}", e.getMessage());
        return Map.of("error", "Ошибка при валидации данных.", "message", e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(final NotFoundException e) {
        log.warn("Ошибка: искомый объект не найден: {}", e.getMessage());
        return Map.of("error", "Искомый объект не найден.", "message", e.getMessage());
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleConflict(final ConflictException e) {
        log.warn("Ошибка: конфликт данных: {}", e.getMessage());
        return Map.of("error", "Конфликт данных.", "message", e.getMessage());
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(final org.springframework.web.bind.MethodArgumentNotValidException e) {
        String defaultMessage = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
        log.warn("Ошибка аргументов метода: {}", defaultMessage);
        return Map.of("error", "Ошибка валидации данных.", "message", defaultMessage);
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleThrowable(final Throwable e) {
        log.error("Внутренняя ошибка сервера: {}", e.getMessage(), e);
        return Map.of("error", "Возникло исключение.", "message", e.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> handleAccessDenied(final AccessDeniedException e) {
        log.warn("Ошибка доступа: {}", e.getMessage());
        return Map.of("error", "Доступ запрещен.", "message", e.getMessage());
    }
}

