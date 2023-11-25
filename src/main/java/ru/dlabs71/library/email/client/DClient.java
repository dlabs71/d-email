package ru.dlabs71.library.email.client;

import jakarta.mail.Session;
import ru.dlabs71.library.email.dto.message.common.EmailParticipant;
import ru.dlabs71.library.email.exception.SessionException;

/**
 * Interface for any email client.
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-25</div>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
public interface DClient {

    /**
     * Connects to the email server.
     *
     * @return {@link Session} object
     *
     * @throws SessionException The connection to the server has failed. The properties are broken.
     */
    Session connect() throws SessionException;

    /**
     * Returns a using protocol name.
     */
    String getProtocolName();

    /**
     * Returns name and email address used for connection.
     */
    EmailParticipant getPrincipal();
}
