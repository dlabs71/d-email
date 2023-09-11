package ru.dlabs.library.email.dto.message;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.dlabs.library.email.dto.message.api.OutgoingMessage;
import ru.dlabs.library.email.dto.message.common.BaseMessage;
import ru.dlabs.library.email.dto.message.common.EmailAttachment;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;

/**
 * Class describe a text email message
 *
 * @author Ivanov Danila
 * @version 1.0
 */
@Getter
@ToString
public class TextOutgoingMessage extends BaseMessage implements OutgoingMessage {

    public TextOutgoingMessage(
        String subject,
        String content,
        Set<EmailParticipant> recipientEmail,
        List<EmailAttachment> attachments
    ) {
        this.setSubject(subject);
        this.setContent(content);
        this.setRecipientEmail(recipientEmail);
        this.setAttachments(attachments);
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
        private List<EmailAttachment> attachments = new ArrayList<>();

        public TextOutgoingMessage build() {
            return new TextOutgoingMessage(
                subject,
                content,
                recipientEmail,
                attachments
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

        public TextMessageBuilder attachments(List<EmailAttachment> attachments) {
            this.attachments = attachments;
            return this;
        }

        public TextMessageBuilder addAttachment(EmailAttachment attachment) {
            if (this.attachments == null) {
                this.attachments = new ArrayList<>();
            }
            this.attachments.add(attachment);
            return this;
        }
    }
}
