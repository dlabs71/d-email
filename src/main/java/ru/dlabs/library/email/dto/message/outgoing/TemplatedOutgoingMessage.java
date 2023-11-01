package ru.dlabs.library.email.dto.message.outgoing;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.dlabs.library.email.dto.message.common.ContentMessage;
import ru.dlabs.library.email.dto.message.common.EmailAttachment;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;
import ru.dlabs.library.email.exception.TemplateCreationException;
import ru.dlabs.library.email.type.ContentMessageType;
import ru.dlabs.library.email.type.TransferEncoder;
import ru.dlabs.library.email.util.TemplateUtils;

/**
 * This class describes a templated outgoing email message.
 * It extends the {@link DefaultOutgoingMessage}.
 *
 * <p>The template will be created using the
 * <a href="https://velocity.apache.org/engine/1.7/user-guide.html#what-is-velocity">Apache Velocity Project</a>.
 *
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-08-27</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Getter
@ToString
public class TemplatedOutgoingMessage extends DefaultOutgoingMessage {

    /**
     * Path to velocity template.
     */
    private final String pathToTemplate;

    /**
     * It's values for aliases in the velocity template.
     */
    private final Map<String, Object> params;

    private final ContentMessageType contentType;

    /**
     * The constructor if this message. Creates an instance with a html content.
     * Html content will be created using Apache Velocity Template.
     *
     * @param subject        a subject of a message
     * @param pathToTemplate a path to template. See {@link TemplateUtils#createTemplate(String)}.
     * @param params         parameters of a template
     * @param recipientEmail message recipients
     *
     * @throws TemplateCreationException if content from the template wasn't can construct.
     */
    public TemplatedOutgoingMessage(
        String subject,
        String pathToTemplate,
        Map<String, Object> params,
        Set<EmailParticipant> recipientEmail
    ) throws TemplateCreationException {
        this(subject, pathToTemplate, params, null, ContentMessageType.HTML, recipientEmail, null, null);
    }

    /**
     * The constructor if this message. Creates an instance with a html content.
     * Html content will be created using Apache Velocity Template.
     *
     * @param subject        a subject of a message
     * @param pathToTemplate a path to template. See {@link TemplateUtils#createTemplate(String)}.
     * @param params         parameters of a template
     * @param recipientEmail message recipients
     * @param attachments    message attached files
     *
     * @throws TemplateCreationException if content from the template wasn't can construct.
     */
    public TemplatedOutgoingMessage(
        String subject,
        String pathToTemplate,
        Map<String, Object> params,
        Set<EmailParticipant> recipientEmail,
        List<EmailAttachment> attachments
    ) throws TemplateCreationException {
        this(subject, pathToTemplate, params, null, ContentMessageType.HTML, recipientEmail, attachments, null);
    }

    /**
     * The constructor if this message. Creates an instance with a html content.
     * Html content will be created using Apache Velocity Template.
     *
     * @param subject        a subject of a message
     * @param pathToTemplate a path to template. See {@link TemplateUtils#createTemplate(String)}.
     * @param params         parameters of a template
     * @param charsetContent a charset of the content
     * @param contentType    a value of a Content-Type header for content
     * @param recipientEmail message recipients
     *
     * @throws TemplateCreationException if content from the template wasn't can construct.
     */
    public TemplatedOutgoingMessage(
        String subject,
        String pathToTemplate,
        Map<String, Object> params,
        Charset charsetContent,
        ContentMessageType contentType,
        Set<EmailParticipant> recipientEmail
    ) throws TemplateCreationException {
        this(subject, pathToTemplate, params, charsetContent, contentType, recipientEmail, null, null);
    }

    /**
     * The constructor if this message. Creates an instance with a html content.
     * Html content will be created using Apache Velocity Template.
     *
     * @param subject         a subject of a message
     * @param pathToTemplate  a path to template. See {@link TemplateUtils#createTemplate(String)}.
     * @param params          parameters of a template
     * @param charsetContent  a charset of the content
     * @param contentType     a value of a Content-Type header for content
     * @param recipientEmail  message recipients
     * @param attachments     message attached files
     * @param transferEncoder a value for a Content-Transfer-Encoding header
     *
     * @throws TemplateCreationException if content from the template wasn't can construct.
     */
    public TemplatedOutgoingMessage(
        String subject,
        String pathToTemplate,
        Map<String, Object> params,
        Charset charsetContent,
        ContentMessageType contentType,
        Set<EmailParticipant> recipientEmail,
        List<EmailAttachment> attachments,
        TransferEncoder transferEncoder
    ) throws TemplateCreationException {
        super(subject, null, charsetContent, contentType, recipientEmail, attachments, transferEncoder);
        this.pathToTemplate = pathToTemplate;
        this.params = params;
        this.contentType = contentType;
        String content = this.constructContent(this.pathToTemplate, this.params);
        if (content != null) {
            this.addContent(new ContentMessage(content, contentType.getMimeType(), charsetContent));
        }
    }

    /**
     * Returns an instance of the builder for this class.
     */
    public static TemplatedMessageBuilder builder() {
        return new TemplatedMessageBuilder();
    }

    /**
     * Creates text content from a path to a templated file and parameters for this one.
     */
    protected String constructContent(String path, Map<String, Object> params) throws TemplateCreationException {
        if (path == null || params == null) {
            return null;
        }
        return TemplateUtils.construct(path, params);
    }

    /**
     * Builder of this class.
     */
    @ToString
    @NoArgsConstructor
    public static class TemplatedMessageBuilder {

        private String pathToTemplate;
        private Map<String, Object> params = new HashMap<>();
        private String subject;
        private ContentMessageType contentType = ContentMessageType.HTML;
        private Set<EmailParticipant> recipientEmail = new HashSet<>();
        private List<EmailAttachment> attachments = new ArrayList<>();
        private Charset charsetContent = Charset.defaultCharset();
        private TransferEncoder transferEncoder = TransferEncoder.byDefault();

        /**
         * Builds and returns a new instance of {@link TemplatedOutgoingMessage}.
         */
        public TemplatedOutgoingMessage build() throws TemplateCreationException {
            return new TemplatedOutgoingMessage(
                subject,
                pathToTemplate,
                params,
                charsetContent,
                contentType,
                recipientEmail,
                attachments,
                transferEncoder
            );
        }

        /**
         * Sets a path to template and parameters for this one.
         */
        public TemplatedMessageBuilder template(String pathToTemplate, Map<String, Object> params) {
            this.pathToTemplate = pathToTemplate;
            this.params = params;
            return this;
        }

        /**
         * Sets a path to template.
         */
        public TemplatedMessageBuilder template(String pathToTemplate) {
            this.pathToTemplate = pathToTemplate;
            return this;
        }

        /**
         * Sets a subject of this message.
         */
        public TemplatedMessageBuilder subject(String subject) {
            this.subject = subject;
            return this;
        }

        /**
         * Sets email recipients.
         */
        public TemplatedMessageBuilder recipientEmail(Set<EmailParticipant> recipientEmail) {
            this.recipientEmail = recipientEmail;
            return this;
        }

        /**
         * Sets email attachments.
         */
        public TemplatedMessageBuilder attachments(List<EmailAttachment> attachments) {
            this.attachments = attachments;
            return this;
        }

        /**
         * Sets a value of a Content-Type header for content.
         */
        public TemplatedMessageBuilder contentType(ContentMessageType contentType) {
            this.contentType = contentType;
            return this;
        }

        /**
         * Sets a charset content of this message.
         */
        public TemplatedMessageBuilder charsetContent(Charset charsetContent) {
            this.charsetContent = charsetContent;
            return this;
        }

        /**
         * Sets a value for a Content-Transfer-Encoding header.
         */
        public TemplatedMessageBuilder transferEncoder(TransferEncoder transferEncoder) {
            this.transferEncoder = transferEncoder;
            return this;
        }

        /**
         * Adds email attachments.
         */
        public TemplatedMessageBuilder addAttachment(EmailAttachment attachment) {
            if (this.attachments == null) {
                this.attachments = new ArrayList<>();
            }
            this.attachments.add(attachment);
            return this;
        }
    }
}
