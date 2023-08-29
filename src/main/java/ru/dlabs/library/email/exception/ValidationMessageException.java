package ru.dlabs.library.email.exception;

/**
 * It's the exception to validating a message object
 */
public class ValidationMessageException extends RuntimeException {

    public ValidationMessageException(String message) {
        super(message);
    }
}
