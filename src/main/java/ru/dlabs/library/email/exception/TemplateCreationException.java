package ru.dlabs.library.email.exception;

/**
 * The exception is caused by a failure to create message based on a Velocity Template.
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-08-29</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
public class TemplateCreationException extends Exception {

    /**
     * The constructor of this class.
     *
     * @param message a user message for a stacktrace
     */
    public TemplateCreationException(String message) {
        super(message);
    }
}
