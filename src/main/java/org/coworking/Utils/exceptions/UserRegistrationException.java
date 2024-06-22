package org.coworking.Utils.exceptions;

/**
 * Исключение которое возникает во время ошибок регистрации
 */
public class UserRegistrationException extends Exception{
    public UserRegistrationException(String message) {
        super(message);
    }
}
