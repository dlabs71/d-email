package ru.dlabs.library.email.exception;

/**
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-09-10
 */
public class CreateMessageException extends RuntimeException {

    public CreateMessageException(String message) {
        super(message);
    }

    public CreateMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
