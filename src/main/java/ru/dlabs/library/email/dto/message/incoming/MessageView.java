package ru.dlabs.library.email.dto.message.incoming;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.dlabs.library.email.dto.message.common.ContentMessage;
import ru.dlabs.library.email.dto.message.common.EmailAttachment;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;
import ru.dlabs.library.email.dto.message.common.Message;
import ru.dlabs.library.email.type.TransferEncoder;

/**
 * This class describes only common data about an email message. Without a message body and message attachments.
 *
 * <p>Uses in the check email operations.
 *
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-09-07</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Getter
@Builder
@ToString
public class MessageView implements IncomingMessage {

    private final EmailParticipant sender;
    private final Set<EmailParticipant> recipients;
    private final String subject;

    private Integer id;
    private Long size;
    private TransferEncoder transferEncoder;
    private boolean seen;

    private LocalDateTime sentDate;
    private LocalDateTime receivedDate;

    /**
     * Returns empty list.
     */
    @Override
    public List<ContentMessage> getContents() {
        return Collections.emptyList();
    }

    /**
     * Returns empty list.
     */
    @Override
    public List<EmailAttachment> getAttachments() {
        return Collections.emptyList();
    }

    @Override
    public List<ContentMessage> getHtmlContents() {
        return Collections.emptyList();
    }

    @Override
    public List<ContentMessage> getTextContents() {
        return Collections.emptyList();
    }

    @Override
    public String getTextContentsAsString() {
        return null;
    }

    @Override
    public String getTextContentsAsString(String delimiter) {
        return null;
    }

    @Override
    public String getHtmlContentsAsString() {
        return null;
    }

    @Override
    public String getHtmlContentsAsString(String delimiter) {
        return null;
    }
}
