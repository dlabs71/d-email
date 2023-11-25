package ru.dlabs71.library.email.type;

/**
 * Statuses of results sending email messages.
 *
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-08-31</div>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
public enum SendingStatus {

    /**
     * Message sent successfully.
     */
    SUCCESS,

    /**
     * Message is incorrect.
     */
    ERROR_IN_MESSAGE,

    /**
     * Message didn't send since transport couldn't do it.
     */
    ERROR_IN_TRANSPORT
}
