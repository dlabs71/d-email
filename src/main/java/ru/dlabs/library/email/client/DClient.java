package ru.dlabs.library.email.client;

import jakarta.mail.Session;

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
     */
    Session connect();
}
