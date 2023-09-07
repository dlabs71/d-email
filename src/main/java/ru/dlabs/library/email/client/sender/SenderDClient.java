package ru.dlabs.library.email.client.sender;

import ru.dlabs.library.email.client.DClient;
import ru.dlabs.library.email.client.SendingStatus;
import ru.dlabs.library.email.dto.message.common.Message;

/**
 * The email client for sending messages
 *
 * @author Ivanov Danila
 * @version 1.0
 */
public interface SenderDClient extends DClient {

    /**
     * It sends to message
     *
     * @param message the message object
     *
     * @return the result status {@link SendingStatus}
     */
    SendingStatus send(Message message);
}
