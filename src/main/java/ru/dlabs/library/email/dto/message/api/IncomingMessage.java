package ru.dlabs.library.email.dto.message.api;

import ru.dlabs.library.email.dto.message.common.Message;

/**
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-09-07
 */
public interface IncomingMessage extends Message {

    String getHtmlContent();
}
