package ru.dlabs.library.email.client;

import jakarta.mail.NoSuchProviderException;
import jakarta.mail.Session;
import java.security.GeneralSecurityException;

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
    Session connect() throws NoSuchProviderException, GeneralSecurityException;
}
