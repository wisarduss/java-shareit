package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("Ошибка валидации данных из запроса.")
                .build();
        log.debug("{}: {}", MethodArgumentNotValidException.class.getSimpleName(),
                e.getFieldError().getDefaultMessage());

        return errorResponse;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ErrorResponse handleValidationExceptions(MethodArgumentTypeMismatchException ex) {
        log.debug("Получен статус 400 bad request {}", ex.getMessage());
        return new ErrorResponse("Unknown state: " + ex.getValue().toString());
    }


    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResponse handleValidationExceptions(IllegalArgumentException ex) {
        log.debug("Получен статус 400 bad request {}", ex.getMessage());
        return new ErrorResponse(ex.getMessage());
    }

}
