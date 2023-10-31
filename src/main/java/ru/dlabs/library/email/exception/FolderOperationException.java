package ru.dlabs.library.email.exception;

/**
 * The exception is caused by troubles with connection to a server or other operations with email folders.
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-09-01</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
public class FolderOperationException extends RuntimeException {

    /**
     * The constructor of this class.
     *
     * @param message a user message for a stacktrace
     */
    public FolderOperationException(String message) {
        super(message);
    }

    /**
     * The constructor of this class.
     *
     * @param message a user message for a stacktrace
     * @param cause   a base exception
     */
    public FolderOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
