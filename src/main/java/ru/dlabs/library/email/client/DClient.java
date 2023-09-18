package ru.dlabs.library.email.client;

import jakarta.mail.Session;
import ru.dlabs.library.email.exception.SessionException;

/**
 * Interface is any email client
 *
 * @author Ivanov Danila
 * @version 1.0
 */
public interface DClient {

    /**
     * It connects to email server
     *
     * @return {@link Session} object
     * @throws SessionException The connection to the server has failed. The properties are broken.
     */
    Session connect() throws SessionException;
}
