package ru.dlabs.library.email.message;

import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.dlabs.library.email.utils.MessageValidator;

/**
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-08-31
 */
@Getter
@Builder
@ToString
public class MessageView implements Message {

    private final EmailParticipant sender;
    private final Set<EmailParticipant> recipientEmail;
    private final String subject;
    private final String contentType;

    public MessageView(
        EmailParticipant sender,
        Set<EmailParticipant> recipientEmail,
        String subject,
        String contentType
    ) {
        this.sender = sender;
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.contentType = contentType;
        MessageValidator.validateView(this);
    }
}
