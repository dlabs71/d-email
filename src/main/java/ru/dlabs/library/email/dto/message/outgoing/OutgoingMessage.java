package ru.dlabs.library.email.dto.message.outgoing;

import ru.dlabs.library.email.dto.message.common.Message;
import ru.dlabs.library.email.type.ContentMessageType;

/**
 * Interface an outgoing message
 *
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-09-07
 */
public interface OutgoingMessage extends Message {

    ContentMessageType getContentType();
}
