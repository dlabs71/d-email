package ru.dlabs.library.email.exception;

/**
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-09-01
 */
public class CheckEmailException extends RuntimeException {

    public CheckEmailException(String message) {
        super(message);
    }

    public CheckEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
