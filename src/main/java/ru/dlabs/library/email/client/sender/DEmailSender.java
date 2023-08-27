package ru.dlabs.library.email.client.sender;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import ru.dlabs.library.email.client.SendingStatus;
import ru.dlabs.library.email.message.EmailRecipient;
import ru.dlabs.library.email.message.Message;
import ru.dlabs.library.email.message.TextMessage;
import ru.dlabs.library.email.properties.SmtpProperties;

/**
 * This class implements the Facade pattern for sending email messages.
 *
 * @author Ivanov Danila
 * @version 1.0
 */
public final class DEmailSender {

    private final SenderDClient senderClient;

    private DEmailSender(SmtpProperties smtpProperties, boolean debug) {
        this.senderClient = new SMTPDClient(smtpProperties, debug);
    }

    public static DEmailSender of(SmtpProperties properties, boolean debug) {
        return new DEmailSender(properties, debug);
    }

    public static DEmailSender of(SmtpProperties properties) {
        return new DEmailSender(properties, false);
    }

    public SenderDClient getClient() {
        return this.senderClient;
    }

    public SendingStatus send(Message message) {
        return this.senderClient.send(message);
    }

    public SendingStatus sendText(String email, String subject, String content) {
        Set<EmailRecipient> recipients = Set.of(new EmailRecipient(email));
        return this.sendText(recipients, subject, content);
    }

    public SendingStatus sendText(List<String> emails, String subject, String content) {
        Set<EmailRecipient> recipients = emails.stream()
            .map(EmailRecipient::new)
            .collect(Collectors.toSet());
        return this.sendText(recipients, subject, content);
    }

    public SendingStatus sendText(Set<EmailRecipient> recipients, String subject, String content) {
        Message message = TextMessage.builder()
            .recipientEmail(recipients)
            .subject(subject)
            .message(content)
            .build();
        return this.senderClient.send(message);
    }
}
