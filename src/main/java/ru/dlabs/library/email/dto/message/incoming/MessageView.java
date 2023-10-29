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
 * This class described common data about an email message. Without a message body and message attachments.
 *
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-08-31
 */
@Getter
@Builder
@ToString
public class MessageView implements Message {

    private final EmailParticipant sender;
    private final Set<EmailParticipant> recipients;
    private final String subject;

    private Integer id;
    private Long size;
    private TransferEncoder transferEncoder;
    private boolean seen;

    private LocalDateTime sentDate;
    private LocalDateTime receivedDate;

    @Override
    public List<ContentMessage> getContents() {
        return Collections.emptyList();
    }

    @Override
    public List<EmailAttachment> getAttachments() {
        return Collections.emptyList();
    }
}
