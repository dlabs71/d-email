package ru.dlabs.library.email.message;

import static ru.dlabs.library.email.utils.EmailMessageUtils.DEFAULT_CONTENT_TYPE;
import static ru.dlabs.library.email.utils.EmailMessageUtils.DEFAULT_ENCODING;

import java.util.Set;

/**
 * Email message
 *
 * @author Ivanov Danila
 * @version 1.0
 */
public interface Message {

    String getSubject();

    Set<EmailParticipant> getRecipientEmail();

    default String getContent() {
        return null;
    }

    default EmailParticipant getSender() {
        return null;
    }

    default String getEncoding() {
        return DEFAULT_ENCODING;
    }

    default String getContentType() {
        return DEFAULT_CONTENT_TYPE;
    }
}
