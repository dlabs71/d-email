package ru.dlabs.library.email.message;

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

    private final Set<EmailRecipient> recipientEmail;
    private final String subject;
    private final String message;

    public TextMessage(Set<EmailRecipient> recipientEmail, String subject, String message) {
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.message = message;
        MessageValidator.validate(this);
    }

    @Override
    public String getContent() {
        return this.message;
    }
}
