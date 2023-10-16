package ru.dlabs.library.email.dto.message.common;

import static ru.dlabs.library.email.util.HttpUtils.DEFAULT_ENCODING;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    private List<ContentMessage> contents;

    private Set<EmailParticipant> recipients = new HashSet<>();
    private EmailParticipant sender = null;

    private List<EmailAttachment> attachments = new ArrayList<>();

    private String encoding = DEFAULT_ENCODING;
    private Integer size;

    private LocalDateTime sentDate;
    private LocalDateTime receivedDate;

    public BaseMessage(
        Integer id,
        String subject,
        List<ContentMessage> contents,
        Set<EmailParticipant> recipients,
        EmailParticipant sender,
        List<EmailAttachment> attachments,
        Integer size,
        LocalDateTime sentDate,
        LocalDateTime receivedDate
    ) {
        this.id = id;
        this.subject = subject;
        this.contents = contents;
        this.recipients = recipients;
        this.sender = sender;
        this.attachments = attachments;
        this.size = size;
        this.sentDate = sentDate;
        this.receivedDate = receivedDate;
    }

    public void addContent(ContentMessage content) {
        if (this.contents == null) {
            this.contents = new ArrayList<>();
        }
        this.contents.add(content);
    }

    public String getTextContentsAsString() {
        return this.getTextContentsAsString("\n");
    }

    public String getTextContentsAsString(String delimiter) {
        return this.contents.stream()
            .map(ContentMessage::getData)
            .collect(Collectors.joining(delimiter));
    }
}
