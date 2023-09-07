package ru.dlabs.library.email.client.sender;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import ru.dlabs.library.email.client.SendingStatus;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;
import ru.dlabs.library.email.dto.message.common.Message;
import ru.dlabs.library.email.dto.message.TextOutgoingMessage;
import ru.dlabs.library.email.property.SmtpProperties;
import ru.dlabs.library.email.util.MessageValidator;

/**
 * This class implements the Facade pattern for sending email messages.
 *
 * @author Ivanov Danila
 * @version 1.0
 */
public final class DEmailSender {

    private final SenderDClient senderClient;

    private DEmailSender(SmtpProperties smtpProperties) {
        this.senderClient = new SMTPDClient(smtpProperties);
    }

    public static DEmailSender of(SmtpProperties properties) {
        return new DEmailSender(properties);
    }

    public SenderDClient getClient() {
        return this.senderClient;
    }

    public SendingStatus send(Message message) {
        return this.senderClient.send(message);
    }

    public SendingStatus sendText(String email, String subject, String content) {
        Set<EmailParticipant> recipients = new HashSet<>();
        recipients.add(new EmailParticipant(email));
        return this.sendText(recipients, subject, content);
    }

    public SendingStatus sendText(List<String> emails, String subject, String content) {
        Set<EmailParticipant> recipients = emails.stream()
            .map(EmailParticipant::new)
            .collect(Collectors.toSet());
        return this.sendText(recipients, subject, content);
    }

    public SendingStatus sendText(Set<EmailParticipant> recipients, String subject, String content) {
        Message message = TextOutgoingMessage.builder()
            .recipientEmail(recipients)
            .subject(subject)
            .content(content)
            .build();
        return this.senderClient.send(message);
    }
}
