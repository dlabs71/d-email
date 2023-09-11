package ru.dlabs.library.email.exception;

/**
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-09-01
 */
public class AttachmentException extends RuntimeException {

    public AttachmentException(String message) {
        super(message);
    }

    public AttachmentException(String message, Throwable cause) {
        super(message, cause);
    }
}
