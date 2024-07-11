package org.coworking.Utils.exceptions;

/**
 * Иключение возникающие при попытке получить доступ, не имея соответсвтующей роли
 */
public class ForbiddenAccessException extends Exception{
    public ForbiddenAccessException(String message) {
        super(message);
    }
}
