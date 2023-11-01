package ru.dlabs.library.email.dto.message.outgoing;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
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
import ru.dlabs.library.email.type.ContentMessageType;
import ru.dlabs.library.email.type.TransferEncoder;

/**
 * This class describes a default outgoing email message.
 * It extends the {@link BaseMessage} and also implements the {@link OutgoingMessage} interface.
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-09-18</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Getter
@ToString
public class DefaultOutgoingMessage extends BaseMessage implements OutgoingMessage {

    /**
     * The constructor of this class. Creates an instance with a single text content.
     * Transfer encoding is used by default ({@link TransferEncoder#byDefault()}).
     *
     * @param subject        a subject of a message
     * @param content        a text content of a message
     * @param recipientEmail message recipients
     * @param attachments    message attached files
     */
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
            ContentMessageType.TEXT,
            recipientEmail == null ? Collections.emptySet() : recipientEmail,
            attachments == null ? Collections.emptyList() : attachments,
            TransferEncoder.byDefault()
        );
    }

    /**
     * The constructor of this class. Creates an instance with a single content (text or html).
     *
     * @param subject         a subject of a message
     * @param content         a text content of a message (plain text or html)
     * @param charsetContent  a charset of the content
     * @param contentType     a value of a Content-Type header for content
     * @param recipientEmail  message recipients
     * @param attachments     message attached files
     * @param transferEncoder a value for a Content-Transfer-Encoding header
     */
    public DefaultOutgoingMessage(
        String subject,
        String content,
        Charset charsetContent,
        ContentMessageType contentType,
        Set<EmailParticipant> recipientEmail,
        List<EmailAttachment> attachments,
        TransferEncoder transferEncoder
    ) {
        this.setSubject(subject);
        this.setTransferEncoder(transferEncoder == null ? TransferEncoder.byDefault() : transferEncoder);
        this.setRecipients(recipientEmail);
        this.setAttachments(attachments);

        if (content != null) {
            ContentMessage contentMessage = new ContentMessage(content, contentType.getMimeType(), charsetContent);
            this.addContent(contentMessage);
        } else {
            this.setContents(new ArrayList<>());
        }
    }

    /**
     * The constructor of this class. Creates an instance with a multiple contents.
     *
     * @param subject         a subject of a message
     * @param contents        a text content of a message (plain text or html)
     * @param recipientEmail  message recipients
     * @param attachments     message attached files
     * @param transferEncoder a value for a Content-Transfer-Encoding header
     */
    public DefaultOutgoingMessage(
        String subject,
        List<ContentMessage> contents,
        Set<EmailParticipant> recipientEmail,
        List<EmailAttachment> attachments,
        TransferEncoder transferEncoder
    ) {
        this.setSubject(subject);
        this.setTransferEncoder(transferEncoder == null ? TransferEncoder.byDefault() : transferEncoder);
        this.setRecipients(recipientEmail);
        this.setAttachments(attachments);

        if (contents != null) {
            this.addAllContent(contents);
        } else {
            this.setContents(new ArrayList<>());
        }
    }

    /**
     * Returns an instance of the builder for this class.
     */
    public static Builder outgoingMessageBuilder() {
        return new Builder();
    }

    /**
     * Builder of this class.
     */
    @ToString
    @NoArgsConstructor
    public static class Builder {

        private String subject;
        private String content;
        private Charset charsetContent = Charset.defaultCharset();
        private ContentMessageType contentType;
        private TransferEncoder transferEncoder = TransferEncoder.byDefault();
        private Set<EmailParticipant> recipientEmail = new HashSet<>();
        private List<EmailAttachment> attachments = new ArrayList<>();
        private List<ContentMessage> contents = new ArrayList<>();

        /**
         * Builds and returns a new instance of {@link DefaultOutgoingMessage}.
         */
        public DefaultOutgoingMessage build() {
            if (content != null) {
                contents.add(new ContentMessage(content, contentType.getMimeType(), charsetContent));
            }

            return new DefaultOutgoingMessage(
                subject,
                contents,
                recipientEmail,
                attachments,
                transferEncoder
            );
        }

        /**
         * Sets a subject of this message.
         */
        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        /**
         * Sets a content of this message.
         */
        public Builder content(String content) {
            this.content = content;
            return this;
        }

        /**
         * Sets a charset content of this message.
         */
        public Builder charsetContent(Charset charsetContent) {
            this.charsetContent = charsetContent;
            return this;
        }

        /**
         * Sets a value for a Content-Transfer-Encoding header.
         */
        public Builder transferEncoder(TransferEncoder encoding) {
            this.transferEncoder = encoding;
            return this;
        }

        /**
         * Sets a value of a Content-Type header for content.
         */
        public Builder contentType(ContentMessageType contentType) {
            this.contentType = contentType;
            return this;
        }

        /**
         * Sets email recipients.
         */
        public Builder recipientEmail(Set<EmailParticipant> recipientEmail) {
            this.recipientEmail = recipientEmail;
            return this;
        }

        /**
         * Sets email attachments.
         */
        public Builder attachments(List<EmailAttachment> attachments) {
            this.attachments = attachments;
            return this;
        }

        /**
         * Adds email attachments.
         */
        public Builder addAttachment(EmailAttachment attachment) {
            if (this.attachments == null) {
                this.attachments = new ArrayList<>();
            }
            this.attachments.add(attachment);
            return this;
        }

        /**
         * Adds content to the message.
         */
        public Builder addContent(ContentMessage contentMessage) {
            if (this.contents == null) {
                this.contents = new ArrayList<>();
            }
            this.contents.add(contentMessage);
            return this;
        }
    }
}
