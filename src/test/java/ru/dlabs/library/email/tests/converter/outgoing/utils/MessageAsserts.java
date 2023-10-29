package ru.dlabs.library.email.tests.converter.outgoing.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import jakarta.mail.Address;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Part;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMultipart;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import ru.dlabs.library.email.dto.message.common.ContentMessage;
import ru.dlabs.library.email.dto.message.common.EmailAttachment;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;
import ru.dlabs.library.email.dto.message.outgoing.DefaultOutgoingMessage;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-28</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@UtilityClass
public class MessageAsserts {

    @SneakyThrows
    public void assertEnvelop(
        DefaultOutgoingMessage expected,
        String expectedEmail,
        String expectedName,
        Message actual
    ) {
        assertNull(expected.getSender());
        assertNull(expected.getSentDate());

        assertFrom(expectedEmail, expectedName, actual.getFrom());
        assertEquals(expected.getSubject(), actual.getSubject());
        assertNotNull(actual.getSentDate());
        assertRecipients(expected.getRecipients(), actual.getRecipients(Message.RecipientType.TO));
        assertNull(actual.getRecipients(Message.RecipientType.BCC));
        assertNull(actual.getRecipients(Message.RecipientType.CC));
    }

    public void assertEmptyContent(Message actual) {
        try {
            actual.getContent();
            assertInstanceOf(MimeMultipart.class, actual.getContent());

            MimeMultipart contents = (MimeMultipart) actual.getContent();
            boolean bodyExists = false;
            for (int i = 0; i < contents.getCount(); i++) {
                BodyPart part = contents.getBodyPart(i);
                if (!Part.ATTACHMENT.equals(part.getDisposition())) {
                    bodyExists = true;
                }
            }

            assertFalse(bodyExists);
        } catch (IOException | MessagingException ex) {
        }
    }

    public void assertEmptyAttachments(Message actual) {
        try {
            actual.getContent();
            assertInstanceOf(MimeMultipart.class, actual.getContent());

            MimeMultipart contents = (MimeMultipart) actual.getContent();
            boolean attachmentsExist = false;
            for (int i = 0; i < contents.getCount(); i++) {
                BodyPart part = contents.getBodyPart(i);
                if (Part.ATTACHMENT.equals(part.getDisposition())) {
                    attachmentsExist = true;
                }
            }

            assertFalse(attachmentsExist);
        } catch (IOException | MessagingException ex) {
        }
    }

    @SneakyThrows
    public void assertMessageContent(DefaultOutgoingMessage expected, Message actual) {
        assertInstanceOf(MimeMultipart.class, actual.getContent());
        assertMessageContent(expected.getContents(), (MimeMultipart) actual.getContent());
    }

    @SneakyThrows
    public void assertMessageContent(List<ContentMessage> expected, MimeMultipart actual) {
        for (ContentMessage expectedMessage : expected) {
            BodyPart bodyPart = null;
            for (int i = 0; i < actual.getCount(); i++) {
                BodyPart part = actual.getBodyPart(i);
                if (!Part.ATTACHMENT.equals(part.getDisposition())) {
                    if (expectedMessage.getData().equals(part.getContent().toString())) {
                        bodyPart = part;
                        break;
                    }
                }
            }
            assertContent(expectedMessage, bodyPart);
        }
    }

    @SneakyThrows
    public void assertMessageContent(List<ContentMessage> expected, List<BodyPart> actual) {
        for (ContentMessage expectedMessage : expected) {
            BodyPart bodyPart = null;
            for (BodyPart part : actual) {
                if (!Part.ATTACHMENT.equals(part.getDisposition())) {
                    if (expectedMessage.getData().equals(part.getContent().toString())) {
                        bodyPart = part;
                        break;
                    }
                }
            }
            assertContent(expectedMessage, bodyPart);
        }
    }

    @SneakyThrows
    public void assertMessageAttachments(DefaultOutgoingMessage expected, Message actual) {
        assertInstanceOf(MimeMultipart.class, actual.getContent());
        assertMessageAttachments(expected.getAttachments(), (MimeMultipart) actual.getContent());
    }

    @SneakyThrows
    public void assertMessageAttachments(List<EmailAttachment> expected, MimeMultipart actual) {
        for (EmailAttachment expectedAttachment : expected) {
            BodyPart bodyPart = null;
            for (int i = 0; i < actual.getCount(); i++) {
                BodyPart part = actual.getBodyPart(i);
                if (Part.ATTACHMENT.equals(part.getDisposition())) {
                    if (expectedAttachment.getName().equals(part.getFileName())) {
                        bodyPart = part;
                        break;
                    }
                }
            }
            assertAttachment(expectedAttachment, bodyPart);
        }
    }

    @SneakyThrows
    public void assertMessageAttachments(List<EmailAttachment> expected, List<BodyPart> actual) {
        for (EmailAttachment expectedAttachment : expected) {
            BodyPart bodyPart = null;
            for (int i = 0; i < actual.size(); i++) {
                BodyPart part = actual.get(i);
                if (Part.ATTACHMENT.equals(part.getDisposition())) {
                    if (expectedAttachment.getName().equals(part.getFileName())) {
                        bodyPart = part;
                        break;
                    }
                }
            }
            assertAttachment(expectedAttachment, bodyPart);
        }
    }


    public void assertRecipients(Collection<EmailParticipant> expected, Address[] actual) {
        if (expected != null && !expected.isEmpty()) {
            assertNotNull(expected);
            assertNotNull(actual);
            assertEquals(expected.size(), actual.length);

            assertEmailParticipant(expected, actual);
        } else {
            assertNotNull(expected);
            assertEquals(0, expected.size());
            assertNull(actual);
        }
    }

    public void assertFrom(EmailParticipant expected, Address[] actual) {
        assertNotNull(expected);
        assertNotNull(actual);
        assertEquals(1, actual.length);

        assertEmailParticipant(expected, actual[0]);
    }

    public void assertFrom(String expectedEmail, String expectedName, Address[] actual) {
        assertNotNull(expectedEmail);
        assertNotNull(actual);
        assertEquals(1, actual.length);

        assertInstanceOf(InternetAddress.class, actual[0]);
        assertEquals(expectedEmail, ((InternetAddress) actual[0]).getAddress());
        assertEquals(expectedName, ((InternetAddress) actual[0]).getPersonal());
    }

    public void assertEmailParticipant(Collection<EmailParticipant> expected, Address[] actual) {
        for (Address actualAddress : actual) {
            EmailParticipant expectedAddress = expected.stream()
                .filter(item -> item.toString().equals(actualAddress.toString()))
                .findFirst()
                .orElse(null);

            assertNotNull(expectedAddress);
            assertEmailParticipant(expectedAddress, actualAddress);
        }
    }

    public void assertEmailParticipant(EmailParticipant expected, Address actual) {
        assertInstanceOf(InternetAddress.class, actual);
        assertEquals(expected.getEmail(), ((InternetAddress) actual).getAddress());
        assertEquals(expected.getName(), ((InternetAddress) actual).getPersonal());
    }

    @SneakyThrows
    public void assertContent(ContentMessage expected, BodyPart actual) {
        assertNotNull(actual);
        assertEquals(expected.getContentType(), actual.getContentType());
        assertEquals(expected.getData(), actual.getContent());
    }

    @SneakyThrows
    public void assertAttachment(EmailAttachment expected, BodyPart actual) {
        assertNotNull(actual);
        assertNotNull(actual.getDataHandler());
        assertEquals(expected.getContentType(), actual.getContentType());
        assertEquals(expected.getSize(), actual.getDataHandler().getInputStream().available());

        byte[] actualBytes = new byte[Math.toIntExact(expected.getSize())];
        actual.getDataHandler().getInputStream().read(actualBytes);

        assertEquals(Arrays.toString(expected.getData()), Arrays.toString(actualBytes));
    }
}
