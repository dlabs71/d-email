package ru.dlabs.library.email.message;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Class describe a text email message
 *
 * @author Ivanov Danila
 * @version 1.0
 */
@Getter
@ToString
public class TextMessage extends BaseMessage {

    public TextMessage(
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

        public TextMessage build() {
            return new TextMessage(
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
