package ru.dlabs.library.email.dto.message.incoming;

import java.util.List;
import ru.dlabs.library.email.dto.message.common.ContentMessage;
import ru.dlabs.library.email.dto.message.common.Message;

/**
 * Interface an incoming message
 *
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-09-07
 */
public interface IncomingMessage extends Message {

    List<ContentMessage> getHtmlContents();
}
