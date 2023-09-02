package ru.dlabs.library.email.exception;

/**
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-09-01
 */
public class FolderOperationException extends RuntimeException {

    public FolderOperationException(String message) {
        super(message);
    }

    public FolderOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
