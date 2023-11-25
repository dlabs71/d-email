package ru.dlabs71.library.email.client.sender;

import ru.dlabs71.library.email.client.DClient;
import ru.dlabs71.library.email.dto.message.outgoing.OutgoingMessage;
import ru.dlabs71.library.email.type.SendingStatus;

/**
 * The email client for sending messages.
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-08-27</div>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
public interface SenderDClient extends DClient {

    /**
     * It sends to message.
     *
     * @param message the message object
     *
     * @return the result status {@link SendingStatus}
     */
    SendingStatus send(OutgoingMessage message);
}
