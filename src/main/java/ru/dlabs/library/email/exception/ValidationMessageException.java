package ru.dlabs.library.email.exception;

/**
 * The exception is caused by a failure to validating a message object.
 * It is usually used while creating outgoing messages.
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-08-29</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
public final class ValidationMessageException extends RuntimeException {

    /**
     * The constructor of this class.
     *
     * @param message a user message for a stacktrace
     */
    public ValidationMessageException(String message) {
        super(message);
    }
}
