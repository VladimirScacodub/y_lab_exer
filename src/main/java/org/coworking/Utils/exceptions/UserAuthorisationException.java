package org.coworking.Utils.exceptions;

/**
 * Исключение которое возникает во время ошибок авторизации
 */
public class UserAuthorisationException extends Exception{
    public UserAuthorisationException(String message) {
        super(message);
    }
}
