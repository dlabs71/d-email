package ru.dlabs.library.email.exception;

/**
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-09-02
 */
public class SessionException extends RuntimeException {

    public SessionException(String message) {
        super(message);
    }

    public SessionException(String message, Throwable cause) {
        super(message, cause);
    }
}
