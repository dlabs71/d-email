package ru.dlabs.library.email.exception;

/**
 * The exception is caused by troubles in the process of connecting to a server.
 * It usually occurs while creating a connection during initializing clients
 * ({@link ru.dlabs.library.email.client.DClient}).
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-09-02</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
public final class SessionException extends RuntimeException {

    /**
     * The constructor of this class.
     *
     * @param message a user message for a stacktrace
     */
    public SessionException(String message) {
        super(message);
    }

    /**
     * The constructor of this class.
     *
     * @param message a user message for a stacktrace
     * @param cause   a base exception
     */
    public SessionException(String message, Throwable cause) {
        super(message, cause);
    }
}
