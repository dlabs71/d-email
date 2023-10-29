package ru.dlabs.library.email.dto.message.common;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import ru.dlabs.library.email.type.TransferEncoder;

/**
 * Email message
 *
 * @author Ivanov Danila
 * @version 1.0
 */
public interface Message {

    Integer getId();

    String getSubject();

    List<ContentMessage> getContents();

    Set<EmailParticipant> getRecipients();

    EmailParticipant getSender();

    TransferEncoder getTransferEncoder();

    List<EmailAttachment> getAttachments();

    Long getSize();

    LocalDateTime getSentDate();

    LocalDateTime getReceivedDate();

    boolean isSeen();
}
