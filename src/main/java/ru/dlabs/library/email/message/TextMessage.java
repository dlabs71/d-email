package ru.dlabs.library.email.message;

import static ru.dlabs.library.email.utils.EmailMessageUtils.DEFAULT_ENCODING;

import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import ru.dlabs.library.email.utils.MessageValidator;

/**
 * Class describe a text email message
 *
 * @author Ivanov Danila
 * @version 1.0
 */
@Getter
@Builder
public class TextMessage implements Message {

    private final Set<EmailParticipant> recipientEmail;
    private final String subject;
    private final String message;
    private final String contentType;
    private final EmailParticipant sender;

    public TextMessage(
        Set<EmailParticipant> recipientEmail,
        String subject,
        String message,
        String contentType,
        EmailParticipant sender
    ) {
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.message = message;
        this.contentType = contentType;
        this.sender = sender;
        MessageValidator.validate(this);
    }

    public TextMessage(
        Set<EmailParticipant> recipientEmail,
        String subject,
        String message
    ) {
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.message = message;
        this.contentType = DEFAULT_ENCODING;
        this.sender = null;
        MessageValidator.validate(this);
    }

    @Override
    public String getContent() {
        return this.message;
    }
}
