package ru.dlabs71.library.email.exception;

/**
 * The exception is caused by a failure to create an email attachment.
 * ({@link ru.dlabs71.library.email.dto.message.common.EmailAttachment}).
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-09-01</div>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
public final class AttachmentException extends RuntimeException {

    /**
     * The constructor of this class.
     *
     * @param message a user message for a stacktrace
     */
    public AttachmentException(String message) {
        super(message);
    }

    /**
     * The constructor of this class.
     *
     * @param message a user message for a stacktrace
     * @param cause   a base exception
     */
    public AttachmentException(String message, Throwable cause) {
        super(message, cause);
    }
}
