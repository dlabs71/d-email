package ru.dlabs.library.email.type;

/**
 * Statuses of results sending email messages
 *
 * @author Ivanov Danila
 * @version 1.0
 */
public enum SendingStatus {

    /**
     * Message sent successfully
     */
    SUCCESS,

    /**
     * Message is incorrect
     */
    ERROR_IN_MESSAGE,

    /**
     * Message didn't send since transport couldn't do it
     */
    ERROR_IN_TRANSPORT
}
