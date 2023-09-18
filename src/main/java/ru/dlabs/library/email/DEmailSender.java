package ru.dlabs.library.email;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import ru.dlabs.library.email.client.SendingStatus;
import ru.dlabs.library.email.client.sender.SMTPDClient;
import ru.dlabs.library.email.client.sender.SenderDClient;
import ru.dlabs.library.email.dto.message.TextOutgoingMessage;
import ru.dlabs.library.email.dto.message.api.OutgoingMessage;
import ru.dlabs.library.email.dto.message.common.EmailAttachment;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;
import ru.dlabs.library.email.property.SmtpProperties;
import ru.dlabs.library.email.util.AttachmentUtils;

/**
 * This class implements the Facade pattern for sending email messages.
 * This class use the SMTP protocol.
 *
 * @author Ivanov Danila
 * @version 1.0
 */
public final class DEmailSender {

    private final SenderDClient senderClient;
    private final SmtpProperties properties;

    /**
     * Constructor of the class
     *
     * @param smtpProperties properties for connecting to an email server by the SMTP protocol ({@link SmtpProperties})
     */
    private DEmailSender(SmtpProperties smtpProperties) {
        this.properties = smtpProperties;
        this.senderClient = new SMTPDClient(smtpProperties);
    }

    /**
     * Creates instance of the {@link DEmailSender} class
     *
     * @param properties properties for connecting to an email server by the SMTP protocol ({@link SmtpProperties})
     *
     * @return object of the class {@link DEmailSender}
     */
    public static DEmailSender of(SmtpProperties properties) {
        return new DEmailSender(properties);
    }

    /**
     * Returns information about sender as object of the class {@link EmailParticipant}
     *
     * @return the object of the class {@link EmailParticipant}
     */
    public EmailParticipant sender() {
        return new EmailParticipant(this.properties.getEmail(), this.properties.getName());
    }

    /**
     * The common method for sending messages
     *
     * @param message the outgoing message
     *
     * @return the sending status {@link SendingStatus}
     */
    public SendingStatus send(OutgoingMessage message) {
        return this.senderClient.send(message);
    }

    /**
     * Sends message like a text message. Content type is "text/plain".
     *
     * @param email   the recipient email address. For example: example@mail.com
     * @param subject the message subject
     * @param content the message body
     *
     * @return the sending status {@link SendingStatus}
     */
    public SendingStatus sendText(String email, String subject, String content) {
        Set<EmailParticipant> recipients = new HashSet<>();
        recipients.add(new EmailParticipant(email));
        return this.sendText(recipients, subject, content);
    }

    /**
     * Email distribution messages to a group of recipients
     *
     * @param emails  the list of a recipient email addresses as simple strings. Use only simple format email address.
     *                For example: example@mail.com
     * @param subject the message subject
     * @param content the message body
     *
     * @return the sending status {@link SendingStatus}
     */
    public SendingStatus sendText(List<String> emails, String subject, String content) {
        Set<EmailParticipant> recipients = emails.stream()
            .map(EmailParticipant::new)
            .collect(Collectors.toSet());
        return this.sendText(recipients, subject, content);
    }

    /**
     * Email distribution messages to a group of recipients
     *
     * @param recipients the list of a recipient email addresses as an object of class {@link EmailParticipant}
     * @param subject    the message subject
     * @param content    the message body
     *
     * @return the sending status {@link SendingStatus}
     */
    public SendingStatus sendText(Set<EmailParticipant> recipients, String subject, String content) {
        return this.sendText(recipients, subject, content, null);
    }

    /**
     * Sends message like a text message with attachments. Content type message body is "text/plain".
     * For simplifying attachment creation, use the {@link AttachmentUtils} utility class
     *
     * @param email       the recipient email address. For example: example@mail.com
     * @param subject     the message subject
     * @param content     the message body
     * @param attachments the one or several objects of the class {@link EmailAttachment}
     *
     * @return the sending status {@link SendingStatus}
     */
    public SendingStatus sendText(String email, String subject, String content, EmailAttachment... attachments) {
        Set<EmailParticipant> recipients = new HashSet<>();
        recipients.add(new EmailParticipant(email));
        return this.sendText(recipients, subject, content, Arrays.asList(attachments));
    }

    /**
     * Sends message like a text message with attachments. Content type message body is "text/plain".
     * For simplifying attachment creation, use the {@link AttachmentUtils} utility class
     *
     * @param email       the recipient email address. For example: example@mail.com
     * @param subject     the message subject
     * @param content     the message body
     * @param attachments the list objects of the class {@link EmailAttachment}
     *
     * @return the sending status {@link SendingStatus}
     */
    public SendingStatus sendText(String email, String subject, String content, List<EmailAttachment> attachments) {
        Set<EmailParticipant> recipients = new HashSet<>();
        recipients.add(new EmailParticipant(email));
        return this.sendText(recipients, subject, content, attachments);
    }

    /**
     * Email distribution messages to a group of recipients with attachments. Content type message body is "text/plain".
     * For simplifying attachment creation, use the {@link AttachmentUtils} utility class
     *
     * @param recipients  the set of recipients
     * @param subject     the message subject
     * @param content     the message body
     * @param attachments the list of attachments
     *
     * @return the sending status {@link SendingStatus}
     */
    public SendingStatus sendText(
        Set<EmailParticipant> recipients,
        String subject,
        String content,
        List<EmailAttachment> attachments
    ) {
        OutgoingMessage message = TextOutgoingMessage.builder()
            .recipientEmail(recipients)
            .subject(subject)
            .content(content)
            .attachments(attachments)
            .build();
        return this.send(message);
    }
}
