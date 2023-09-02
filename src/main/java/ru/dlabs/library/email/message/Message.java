package ru.dlabs.library.email.message;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

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

    Set<EmailParticipant> getRecipientEmail();

    EmailParticipant getSender();

    String getEncoding();

    String getContentType();

    List<EmailAttachment> getAttachments();

    Integer getSize();

    LocalDateTime getSentDate();

    LocalDateTime getReceivedDate();
}
