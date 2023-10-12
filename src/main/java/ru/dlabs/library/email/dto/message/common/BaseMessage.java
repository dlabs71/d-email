package ru.dlabs.library.email.dto.message.common;

import static ru.dlabs.library.email.util.EmailMessageUtils.DEFAULT_CONTENT_TYPE;
import static ru.dlabs.library.email.util.EmailMessageUtils.DEFAULT_ENCODING;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.dlabs.library.email.dto.message.api.Message;
import ru.dlabs.library.email.util.EmailMessageUtils;

/**
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-09-01
 */
@Setter
@Getter
@NoArgsConstructor
public class BaseMessage implements Message {

    private Integer id;

    private String subject;
    private String content;

    private Set<EmailParticipant> recipients = new HashSet<>();
    private EmailParticipant sender = null;

    private List<EmailAttachment> attachments = new ArrayList<>();

    private String encoding = DEFAULT_ENCODING;
    private String contentType = DEFAULT_CONTENT_TYPE;
    private Integer size;

    private LocalDateTime sentDate;
    private LocalDateTime receivedDate;

    public BaseMessage(
        Integer id,
        String subject,
        String content,
        Set<EmailParticipant> recipients,
        EmailParticipant sender,
        List<EmailAttachment> attachments,
        String encoding,
        String contentType,
        Integer size,
        LocalDateTime sentDate,
        LocalDateTime receivedDate
    ) {
        this.id = id;
        this.subject = subject;
        this.content = content;
        this.recipients = recipients;
        this.sender = sender;
        this.attachments = attachments;
        this.size = size;
        this.sentDate = sentDate;
        this.receivedDate = receivedDate;

        if (encoding != null) {
            this.encoding = encoding;
        }
        if (contentType != null) {
            this.contentType = EmailMessageUtils.contentTypeWithEncoding(contentType, this.encoding);
        }
    }

    public void setEncoding(String encoding) {
        if (encoding == null) {
            return;
        }
        this.encoding = encoding;
    }

    public void setContentType(String contentType) {
        if (contentType == null) {
            return;
        }
        this.contentType = EmailMessageUtils.contentTypeWithEncoding(contentType, this.encoding);
    }
}
