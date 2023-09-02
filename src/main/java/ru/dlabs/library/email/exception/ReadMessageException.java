package ru.dlabs.library.email.exception;

/**
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-09-02
 */
public class ReadMessageException extends RuntimeException {

    public ReadMessageException(String message) {
        super(message);
    }

    public ReadMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
