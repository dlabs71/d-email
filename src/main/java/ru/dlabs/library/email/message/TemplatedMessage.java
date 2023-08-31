package ru.dlabs.library.email.message;

import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.ToString;
import ru.dlabs.library.email.exception.TemplateCreationException;
import ru.dlabs.library.email.utils.MessageValidator;
import ru.dlabs.library.email.utils.TemplateUtils;

/**
 * Class describe a templated email message.
 * If you want to send an HTML message, you can use this class to do it. Library use velocity template
 *
 * @author Ivanov Danila
 * @version 1.0
 * @see <a href="https://velocity.apache.org/engine/1.7/user-guide.html#what-is-velocity">Apache Velocity Project</a>
 */
@Getter
public class TemplatedMessage implements Message {

    private final Set<EmailParticipant> recipientEmail;
    private final String subject;

    /**
     * Path to velocity template
     */
    private final String pathToTemplate;

    /**
     * It's values for aliases in the velocity template
     */
    private final Map<String, Object> params;
    private final String content;

    public TemplatedMessage(
        Set<EmailParticipant> recipientEmail,
        String subject,
        String pathToTemplate,
        Map<String, Object> params
    ) throws TemplateCreationException {
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.pathToTemplate = pathToTemplate;
        this.params = params;
        this.content = this.constructContent();
        MessageValidator.validate(this);
    }

    public static TemplatedMessageBuilder builder() { return new TemplatedMessageBuilder(); }

    private String constructContent() throws TemplateCreationException {
        if (pathToTemplate == null || params == null) {
            return null;
        }
        return TemplateUtils.construct(pathToTemplate, params);
    }

    @ToString
    public static class TemplatedMessageBuilder {

        private Set<EmailParticipant> recipientEmail;
        private String subject;
        private String pathToTemplate;
        private Map<String, Object> params;

        TemplatedMessageBuilder() { }

        public TemplatedMessageBuilder recipientEmail(Set<EmailParticipant> recipientEmail) {
            this.recipientEmail = recipientEmail;
            return this;
        }

        public TemplatedMessageBuilder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public TemplatedMessageBuilder pathToTemplate(String pathToTemplate) {
            this.pathToTemplate = pathToTemplate;
            return this;
        }

        public TemplatedMessageBuilder params(Map<String, Object> params) {
            this.params = params;
            return this;
        }

        public TemplatedMessage build() throws TemplateCreationException {
            return new TemplatedMessage(recipientEmail, subject, pathToTemplate, params);
        }
    }
}
