package ru.dlabs.library.email.dto.message.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import ru.dlabs.library.email.dto.message.common.EmailAttachment;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;

/**
 * Email message
 *
 * @author Ivanov Danila
 * @version 1.0
 */
public interface Message {

    Integer getId();

    String getSubject();

    String getContent();

    Set<EmailParticipant> getRecipients();

    EmailParticipant getSender();

    String getEncoding();

    String getContentType();

    List<EmailAttachment> getAttachments();

    Integer getSize();

    LocalDateTime getSentDate();

    LocalDateTime getReceivedDate();
}
