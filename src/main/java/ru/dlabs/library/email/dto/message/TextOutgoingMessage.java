package ru.dlabs.library.email.dto.message;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.dlabs.library.email.dto.message.common.BaseMessage;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;

/**
 * Class describe a text email message
 *
 * @author Ivanov Danila
 * @version 1.0
 */
@Getter
@ToString
public class TextOutgoingMessage extends BaseMessage {

    public TextOutgoingMessage(
        String subject,
        String content,
        Set<EmailParticipant> recipientEmail,
        EmailParticipant sender
    ) {
        this.setSubject(subject);
        this.setContent(content);
        this.setRecipientEmail(recipientEmail);
        this.setSender(sender);
    }

    public static TextMessageBuilder builder() {
        return new TextMessageBuilder();
    }

    @ToString
    @NoArgsConstructor
    public static class TextMessageBuilder {

        private String subject;
        private String content;
        private Set<EmailParticipant> recipientEmail = new HashSet<>();
        private EmailParticipant sender = null;

        public TextOutgoingMessage build() {
            return new TextOutgoingMessage(
                subject,
                content,
                recipientEmail,
                sender
            );
        }

        public TextMessageBuilder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public TextMessageBuilder content(String content) {
            this.content = content;
            return this;
        }

        public TextMessageBuilder recipientEmail(Set<EmailParticipant> recipientEmail) {
            this.recipientEmail = recipientEmail;
            return this;
        }

        public TextMessageBuilder sender(EmailParticipant sender) {
            this.sender = sender;
            return this;
        }
    }
}
