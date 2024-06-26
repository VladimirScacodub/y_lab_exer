package org.coworking.Utils.exceptions;

/**
 * Исключение которое возникает при работе с Place
 */
public class PlaceNamingException extends Exception{
    public PlaceNamingException(String message) {
        super(message);
    }
}
