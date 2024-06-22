package org.coworking.Utils.exceptions;

/**
 * Исключение которое возникает при работе с BookedPlace
 */
public class BookedPlaceConflictsException extends Exception{
    public BookedPlaceConflictsException(String message) {
        super(message);
    }
}
