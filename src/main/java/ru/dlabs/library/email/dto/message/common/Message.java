package ru.dlabs.library.email.dto.message.common;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import ru.dlabs.library.email.type.TransferEncoder;

/**
 * This interface defines the base of any email message.
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-08-27</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
public interface Message {

    /**
     * Returns the unique identifier of a message.
     */
    Integer getId();

    /**
     * Returns the subject of a message.
     */
    String getSubject();

    /**
     * Returns all the contents of a message.
     *
     * <p>A message can contain several contents at once. They may have different content types.
     * But not any content type, only plain/text or plain/html.
     * Allowed content types were enumerated in {@link ru.dlabs.library.email.type.ContentMessageType}.
     */
    List<ContentMessage> getContents();

    /**
     * Returns all the recipients of a message.
     */
    Set<EmailParticipant> getRecipients();

    /**
     * Returns the sender of a message.
     */
    EmailParticipant getSender();

    /**
     * Returns the value of Content-Transfer-Encoding header.
     */
    TransferEncoder getTransferEncoder();

    /**
     * Returns all the attached files to a message.
     */
    List<EmailAttachment> getAttachments();

    /**
     * Returns size of a message.
     */
    Long getSize();

    /**
     * Returns the date of sending this message.
     */
    LocalDateTime getSentDate();

    /**
     * Returns the date of receiving this message.
     */
    LocalDateTime getReceivedDate();

    /**
     * Returns true if this message has already seen.
     */
    boolean isSeen();
}
