package ru.dlabs.library.email.dto.message.incoming;

import static ru.dlabs.library.email.util.HttpUtils.DEFAULT_ENCODING;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.dlabs.library.email.dto.message.common.ContentMessage;
import ru.dlabs.library.email.dto.message.common.EmailAttachment;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;
import ru.dlabs.library.email.dto.message.common.Message;

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
    private Integer size;
    private String encoding = DEFAULT_ENCODING;
    private boolean seen = false;

    private LocalDateTime sentDate;
    private LocalDateTime receivedDate;

    @Override
    public List<ContentMessage> getContents() {
        return null;
    }

    @Override
    public List<EmailAttachment> getAttachments() {
        return null;
    }
}
