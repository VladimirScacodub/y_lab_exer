package org.coworking.Utils.exceptions;

/**
 * Иключение, возникающие при попытке получения доступа без авторизации
 */
public class RequiredAuthorisationException extends Exception{
    public RequiredAuthorisationException(String message) {
        super(message);
    }
}
