package ru.dlabs.library.email.tests.client.sender.utils;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import ru.dlabs.library.email.dto.message.common.EmailAttachment;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;
import ru.dlabs.library.email.dto.message.incoming.DefaultIncomingMessage;
import ru.dlabs.library.email.dto.message.incoming.IncomingMessage;
import ru.dlabs.library.email.type.TransferEncoder;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-29</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@UtilityClass
public class Assertions {

    public void assertTextIncomingMessage(
        EmailParticipant sender,
        String subject,
        String textBody,
        IncomingMessage message
    ) {
        assertCommonIncomingMessage(sender, subject, message);
        assertTextContentMessage(textBody, message);
    }

    public void assertHtmlIncomingMessage(
        EmailParticipant sender,
        String subject,
        String htmlBody,
        IncomingMessage message
    ) {
        assertCommonIncomingMessage(sender, subject, message);
        assertHtmlContentMessage(htmlBody, message);
    }

    public void assertCommonIncomingMessage(
        EmailParticipant sender,
        String subject,
        IncomingMessage message
    ) {
        assertInstanceOf(DefaultIncomingMessage.class, message);
        DefaultIncomingMessage incomingMessage = (DefaultIncomingMessage) message;
        assertEquals(sender, incomingMessage.getSender());
        assertEquals(sender.getName(), incomingMessage.getSender().getName());
        assertEquals(subject, incomingMessage.getSubject());
        assertEquals(TransferEncoder.byDefault(), incomingMessage.getTransferEncoder());
    }

    public void assertTextContentMessage(String textBody, IncomingMessage incomingMessage) {
        assertEquals(textBody, incomingMessage.getTextContentsAsString());
    }

    public void assertHtmlContentMessage(String htmlBody, IncomingMessage incomingMessage) {
        assertEquals(htmlBody, incomingMessage.getHtmlContentsAsString());
    }

    public void assertIncomingMessageRecipients(List<String> expected, Set<EmailParticipant> actual) {
        assertEquals(expected.size(), actual.size());
        Set<String> emails = actual.stream()
            .map(EmailParticipant::getEmail)
            .collect(Collectors.toSet());
        Set<String> names = actual.stream()
            .map(EmailParticipant::getName)
            .collect(Collectors.toSet());

        assertTrue(emails.containsAll(expected));
        assertTrue(names.containsAll(expected));
    }

    public void assertIncomingMessageEmptyAttachments(List<EmailAttachment> actual) {
        assertNotNull(actual);
        assertEquals(0, actual.size());
    }

    public void assertIncomingMessageAttachments(List<EmailAttachment> expected, List<EmailAttachment> actual) {
        assertEquals(expected.size(), actual.size());
        actual.forEach(attachment -> {
            assertNotNull(attachment.getName());
            EmailAttachment expectedAttachment = expected.stream()
                .filter(item -> item.getName().equals(attachment.getName()))
                .findFirst()
                .orElse(null);

            assertNotNull(expectedAttachment);
            assertEquals(expectedAttachment.getSize(), attachment.getSize());
            assertEquals(expectedAttachment.getType(), attachment.getType());
            assertEquals(expectedAttachment.getContentType(), attachment.getContentType());
            assertArrayEquals(expectedAttachment.getData(), attachment.getData());
        });
    }
}
