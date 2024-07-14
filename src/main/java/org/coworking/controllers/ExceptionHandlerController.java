package org.coworking.controllers;

import org.coworking.Utils.exceptions.BookedPlaceConflictsException;
import org.coworking.Utils.exceptions.ForbiddenAccessException;
import org.coworking.Utils.exceptions.PlaceNamingException;
import org.coworking.Utils.exceptions.RequiredAuthorisationException;
import org.coworking.Utils.exceptions.UserAuthorisationException;
import org.coworking.Utils.exceptions.UserRegistrationException;
import org.coworking.annotations.Loggable;
import org.coworking.dtos.MessageDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.format.DateTimeParseException;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

/**
 * Контроллер, который перехватывает и обрабатывает исключения
 */
@Loggable
@ControllerAdvice
public class ExceptionHandlerController {

    /**
     * ExceptionHandler метод, обрабатывающий разные иключения (связанные с пользователями, местами и бронированием)
     *
     * @param e исключение
     * @return ResponseEntity, содержащий ответ
     */
    @ExceptionHandler({UserAuthorisationException.class,
            PlaceNamingException.class,
            BookedPlaceConflictsException.class,
            UserRegistrationException.class})
    public ResponseEntity<MessageDTO> exceptionHandel(Exception e) {
        MessageDTO message = new MessageDTO(e.getMessage());
        return ResponseEntity.badRequest().body(message);
    }

    /**
     * ExceptionHandler обрабатывающий исключения при попытке получить доступ не имея соответствующих прав
     *
     * @param e исключение
     * @return ResponseEntity, содержащий ответ
     */
    @ExceptionHandler({ForbiddenAccessException.class})
    public ResponseEntity<MessageDTO> forbiddenAccessExceptionHandler(Exception e) {
        MessageDTO message = new MessageDTO(e.getMessage());
        return ResponseEntity.status(FORBIDDEN).body(message);
    }

    /**
     * ExceptionHandler обрабатывающий попытки получить доступ без авторизации
     *
     * @param e исключение
     * @return ResponseEntity, содержащий ответ
     */
    @ExceptionHandler({RequiredAuthorisationException.class})
    public ResponseEntity<MessageDTO> authorisationExceptionHandler(Exception e) {
        MessageDTO message = new MessageDTO(e.getMessage());
        return ResponseEntity.status(UNAUTHORIZED).body(message);
    }

    /**
     * ExceptionHandler метод, обрабатывающий иключения парсинга дат
     *
     * @return ResponseEntity, содержащий ответ
     */
    @ExceptionHandler({DateTimeParseException.class})
    public ResponseEntity<MessageDTO> dateTimeFormatException() {
        return ResponseEntity.badRequest().body(new MessageDTO("Нужно вести параметр date в формате: yyyy-MM-dd"));
    }
}
