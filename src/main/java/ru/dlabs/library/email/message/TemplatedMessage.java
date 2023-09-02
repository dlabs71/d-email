package ru.dlabs.library.email.message;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.dlabs.library.email.exception.TemplateCreationException;
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
@ToString
public class TemplatedMessage extends BaseMessage {

    /**
     * Path to velocity template
     */
    private final String pathToTemplate;

    /**
     * It's values for aliases in the velocity template
     */
    private final Map<String, Object> params;

    public TemplatedMessage(
        String subject,
        String pathToTemplate,
        Map<String, Object> params,
        Set<EmailParticipant> recipientEmail
    ) throws TemplateCreationException {
        this.setSubject(subject);
        this.setRecipientEmail(recipientEmail);
        this.pathToTemplate = pathToTemplate;
        this.params = params;
        this.setContent(this.constructContent());
    }

    public TemplatedMessage(
        String subject,
        Set<EmailParticipant> recipientEmail,
        EmailParticipant sender,
        String pathToTemplate,
        Map<String, Object> params
    ) throws TemplateCreationException {
        this.pathToTemplate = pathToTemplate;
        this.params = params;
        this.setContent(this.constructContent());
        this.setSubject(subject);
        this.setRecipientEmail(recipientEmail);
        this.setSender(sender);
    }

    public static TemplatedMessageBuilder builder() { return new TemplatedMessageBuilder(); }

    public String constructContent() throws TemplateCreationException {
        if (pathToTemplate == null || params == null) {
            return null;
        }
        return TemplateUtils.construct(pathToTemplate, params);
    }

    @ToString
    @NoArgsConstructor
    public static class TemplatedMessageBuilder {

        private String pathToTemplate;
        private Map<String, Object> params = new HashMap<>();
        private String subject;
        private Set<EmailParticipant> recipientEmail = new HashSet<>();
        private EmailParticipant sender = null;


        public TemplatedMessage build() throws TemplateCreationException {
            return new TemplatedMessage(
                subject,
                recipientEmail,
                sender,
                pathToTemplate,
                params
            );
        }

        public TemplatedMessageBuilder template(String pathToTemplate, Map<String, Object> params) {
            this.pathToTemplate = pathToTemplate;
            this.params = params;
            return this;
        }

        public TemplatedMessageBuilder template(String pathToTemplate) {
            this.pathToTemplate = pathToTemplate;
            return this;
        }

        public TemplatedMessageBuilder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public TemplatedMessageBuilder recipientEmail(Set<EmailParticipant> recipientEmail) {
            this.recipientEmail = recipientEmail;
            return this;
        }

        public TemplatedMessageBuilder sender(EmailParticipant sender) {
            this.sender = sender;
            return this;
        }
    }
}
