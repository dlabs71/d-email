package ru.dlabs71.library.email.exception;

/**
 * The exception is caused by a failure to convert a Jakarta Message class ({@link jakarta.mail.Message})
 * to DTO classes that implement the {@link ru.dlabs71.library.email.dto.message.common.Message} interface.
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-09-01</div>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
public final class CheckEmailException extends RuntimeException {

    /**
     * The constructor of this class.
     *
     * @param message a user message for a stacktrace
     */
    public CheckEmailException(String message) {
        super(message);
    }

    /**
     * The constructor of this class.
     *
     * @param message a user message for a stacktrace
     * @param cause   a base exception
     */
    public CheckEmailException(String message, Throwable cause) {
        super(message, cause);
    }
}
