package ru.dlabs.library.email.dto.message.outgoing;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.dlabs.library.email.dto.message.common.BaseMessage;
import ru.dlabs.library.email.dto.message.common.ContentMessage;
import ru.dlabs.library.email.dto.message.common.EmailAttachment;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;
import ru.dlabs.library.email.dto.message.common.TransferEncoder;

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

    private final OutgoingContentType contentType;

    public DefaultOutgoingMessage(
        String subject,
        String content,
        Set<EmailParticipant> recipientEmail,
        List<EmailAttachment> attachments
    ) {
        this(
            subject,
            content,
            Charset.defaultCharset(),
            OutgoingContentType.TEXT,
            recipientEmail,
            attachments,
            TransferEncoder.byDefault()
        );
    }

    public DefaultOutgoingMessage(
        String subject,
        String content,
        Charset charsetContent,
        OutgoingContentType contentType,
        Set<EmailParticipant> recipientEmail,
        List<EmailAttachment> attachments,
        TransferEncoder transferEncoder
    ) {
        this.setSubject(subject);
        this.setTransferEncoder(transferEncoder == null ? TransferEncoder.byDefault() : transferEncoder);
        this.setRecipients(recipientEmail);
        this.setAttachments(attachments);
        this.contentType = contentType;


        if (content != null) {
            ContentMessage contentMessage = new ContentMessage(
                content,
                contentType.getContentType(),
                charsetContent
            );
            this.addContent(contentMessage);
        } else {
            this.setContents(new ArrayList<>());
        }
    }

    public static Builder outgoingMessageBuilder() {
        return new Builder();
    }

    @ToString
    @NoArgsConstructor
    public static class Builder {

        private String subject;
        private String content;
        private Charset charsetContent = Charset.defaultCharset();
        private OutgoingContentType contentType;
        private TransferEncoder transferEncoder = TransferEncoder.byDefault();
        private Set<EmailParticipant> recipientEmail = new HashSet<>();
        private List<EmailAttachment> attachments = new ArrayList<>();

        public DefaultOutgoingMessage build() {
            return new DefaultOutgoingMessage(
                subject,
                content,
                charsetContent,
                contentType,
                recipientEmail,
                attachments,
                transferEncoder
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

        public Builder charsetContent(Charset charsetContent) {
            this.charsetContent = charsetContent;
            return this;
        }

        public Builder transferEncoder(TransferEncoder encoding) {
            this.transferEncoder = encoding;
            return this;
        }

        public Builder contentType(OutgoingContentType contentType) {
            this.contentType = contentType;
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
