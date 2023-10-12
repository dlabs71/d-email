package ru.dlabs.library.email.dto.message;

import static ru.dlabs.library.email.util.EmailMessageUtils.DEFAULT_CONTENT_TYPE;
import static ru.dlabs.library.email.util.EmailMessageUtils.DEFAULT_ENCODING;

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
 * Class describe a simple outgoing email message
 *
 * @author Ivanov Danila
 * @since 0.0.1
 * Project name: d-email
 * Creation date: 2023-09-18
 */
@Getter
@ToString
public class DefaultOutgoingMessage extends BaseMessage implements OutgoingMessage {

    public DefaultOutgoingMessage(
        String subject,
        String content,
        String contentType,
        String encoding,
        Set<EmailParticipant> recipientEmail,
        List<EmailAttachment> attachments
    ) {
        this.setSubject(subject);
        this.setContent(content);
        this.setContentType(contentType);
        this.setEncoding(encoding);
        this.setRecipients(recipientEmail);
        this.setAttachments(attachments);
    }

    public DefaultOutgoingMessage(
        String subject,
        String content,
        Set<EmailParticipant> recipientEmail,
        List<EmailAttachment> attachments
    ) {
        this(subject, content, null, null, recipientEmail, attachments);
    }

    public static Builder outgoingMessageBuilder() {
        return new Builder();
    }

    @ToString
    @NoArgsConstructor
    public static class Builder {

        private String subject;
        private String content;
        private String contentType = DEFAULT_CONTENT_TYPE;
        private String encoding = DEFAULT_ENCODING;
        private Set<EmailParticipant> recipientEmail = new HashSet<>();
        private List<EmailAttachment> attachments = new ArrayList<>();

        public DefaultOutgoingMessage build() {
            return new DefaultOutgoingMessage(
                subject,
                content,
                contentType,
                encoding,
                recipientEmail,
                attachments
            );
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public Builder encoding(String encoding) {
            this.encoding = encoding;
            return this;
        }

        public Builder recipientEmail(Set<EmailParticipant> recipientEmail) {
            this.recipientEmail = recipientEmail;
            return this;
        }

        public Builder attachments(List<EmailAttachment> attachments) {
            this.attachments = attachments;
            return this;
        }

        public Builder addAttachment(EmailAttachment attachment) {
            if (this.attachments == null) {
                this.attachments = new ArrayList<>();
            }
            this.attachments.add(attachment);
            return this;
        }
    }
}
