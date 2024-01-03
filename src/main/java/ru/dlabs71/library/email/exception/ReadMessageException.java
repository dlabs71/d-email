package ru.dlabs71.library.email.exception;

/**
 * The exception is caused by troubles with connection to a server or errors while reading messages.
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-09-02</div>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
public final class ReadMessageException extends RuntimeException {

    /**
     * The constructor of this class.
     *
     * @param message a user message for a stacktrace
     */
    public ReadMessageException(String message) {
        super(message);
    }

    /**
     * The constructor of this class.
     *
     * @param message a user message for a stacktrace
     * @param cause   a base exception
     */
    public ReadMessageException(String message, Throwable cause) {
        super(message, cause);
    }
}
