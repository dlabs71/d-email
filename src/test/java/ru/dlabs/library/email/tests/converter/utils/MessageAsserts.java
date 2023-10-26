package ru.dlabs.library.email.tests.converter.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.dlabs.library.email.util.HttpUtils.HTML_CONTENT_TYPE;
import static ru.dlabs.library.email.util.HttpUtils.TEXT_CONTENT_TYPE;

import jakarta.mail.Address;
import jakarta.mail.Flags;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import ru.dlabs.library.email.dto.message.common.BaseMessage;
import ru.dlabs.library.email.dto.message.common.ContentMessage;
import ru.dlabs.library.email.dto.message.common.EmailAttachment;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;
import ru.dlabs.library.email.dto.message.incoming.IncomingMessage;
import ru.dlabs.library.email.dto.message.incoming.MessageView;
import ru.dlabs.library.email.type.AttachmentType;
import ru.dlabs.library.email.type.ContentMessageType;
import ru.dlabs.library.email.util.JavaCoreUtils;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-17</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Builder
@UtilityClass
public class MessageAsserts {

    @SneakyThrows
    public void assertMessageEnvelop(
        @NonNull ru.dlabs.library.email.dto.message.common.Message messageDto,
        @NonNull MimeMessage message,
        String subject
    ) {
        assertEquals(message.getMessageNumber(), messageDto.getId());
        assertEquals(message.getSize(), messageDto.getSize());
        assertEquals(message.isSet(Flags.Flag.SEEN), messageDto.isSeen());

        if (messageDto.getSentDate() == null) {
            assertNull(message.getSentDate());
        } else {
            Long sendDateAfterConvert = messageDto.getSentDate()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
            Long sendDateBeforeConvert = message.getSentDate().getTime();
            assertEquals(sendDateBeforeConvert, sendDateAfterConvert);
        }

        if (messageDto.getReceivedDate() == null) {
            assertNull(message.getReceivedDate());
        } else {
            Long sendDateAfterConvert = messageDto.getReceivedDate().toEpochSecond(ZoneOffset.UTC);
            Long sendDateBeforeConvert = message.getReceivedDate().getTime();
            assertEquals(sendDateBeforeConvert, sendDateAfterConvert);
        }

        assertEquals(messageDto.getSubject(), subject);
        if (message.getEncoding() != null) {
            assertEquals(message.getEncoding(), messageDto.getTransferEncoder().getName());
        } else {
            assertNull(messageDto.getTransferEncoder());
        }

        if (message.getFrom() != null) {
            assertNotNull(messageDto.getSender());
            InternetAddress from = (InternetAddress) message.getFrom()[0];
            assertEquals(from.getAddress(), messageDto.getSender().getEmail());
            assertEquals(from.getPersonal(), messageDto.getSender().getName());
        } else {
            assertNull(messageDto.getSender());
        }

        if (message.getRecipients(Message.RecipientType.TO) != null) {
            for (Address address : message.getRecipients(Message.RecipientType.TO)) {
                InternetAddress internetAddress = (InternetAddress) address;
                EmailParticipant participant = messageDto.getRecipients().stream()
                    .filter(item -> item.getEmail().equals(internetAddress.getAddress()))
                    .findFirst()
                    .orElse(null);
                assertNotNull(participant);
                assertEquals(internetAddress.getAddress(), participant.getEmail());
                assertEquals(internetAddress.getPersonal(), participant.getName());
            }
        } else {
            assertNotNull(messageDto.getRecipients());
            assertEquals(0, messageDto.getRecipients().size());
        }
    }

    public void assertContentMessage(@NonNull BaseMessage baseMessage, @NonNull String content) {
        assertContentMessage(baseMessage, Arrays.asList(content));
    }

    public void assertContentMessage(@NonNull BaseMessage baseMessage, @NonNull List<String> contents) {
        assertNotNull(baseMessage.getContents());
        assertEquals(contents.size(), baseMessage.getContents().size());

        for (ContentMessage content : baseMessage.getContents()) {
            assertInstanceOf(ContentMessage.class, content);
            assertEquals(StandardCharsets.UTF_8, content.getCharset());
            assertNotNull(content.getData());
            assertTrue(contents.contains(content.getData()));
            assertNotNull(content.getContentType());
            assertNotNull(content.getType());
        }

        assertEquals(String.join(";", contents), baseMessage.getAllContentsAsString(";"));
        assertEquals(String.join("\n", contents), baseMessage.getAllContentsAsString());
    }

    public void assertEmptyContentMessage(@NonNull BaseMessage baseMessage) {
        assertNotNull(baseMessage.getContents());
        assertEquals(baseMessage.getContents().size(), 0);

        assertEquals("", baseMessage.getAllContentsAsString(";"));
        assertEquals("", baseMessage.getAllContentsAsString());
    }

    public void assertEmptyContentMessage(@NonNull MessageView messageView) {
        assertNotNull(messageView.getContents());
        assertEquals(0, messageView.getContents().size());
    }

    public void assertHtmlContentMessage(@NonNull IncomingMessage message, @NonNull String html) {
        assertHtmlContentMessage(message, Arrays.asList(html));
    }

    public void assertHtmlContentMessage(@NonNull IncomingMessage message, @NonNull List<String> contents) {
        assertNotNull(message.getHtmlContents());
        assertEquals(contents.size(), message.getHtmlContents().size());

        for (ContentMessage content : message.getHtmlContents()) {
            assertInstanceOf(ContentMessage.class, content);
            assertEquals(StandardCharsets.UTF_8, content.getCharset());
            assertNotNull(content.getData());
            assertTrue(contents.contains(content.getData()));
            assertEquals(
                HTML_CONTENT_TYPE + "; charset=" + StandardCharsets.UTF_8.name().toUpperCase(),
                content.getContentType()
            );
            assertEquals(ContentMessageType.HTML, content.getType());
        }

        assertEquals(String.join(";", contents), message.getHtmlContentsAsString(";"));
        assertEquals(String.join("\n", contents), message.getHtmlContentsAsString());
    }

    public void assertEmptyHtmlContentMessage(@NonNull IncomingMessage message) {
        assertNotNull(message.getHtmlContents());
        assertEquals(message.getHtmlContents().size(), 0);

        assertEquals("", message.getHtmlContentsAsString(";"));
        assertEquals("", message.getHtmlContentsAsString());
    }

    public void assertTextContentMessage(@NonNull IncomingMessage message, @NonNull String text) {
        assertTextContentMessage(message, Arrays.asList(text));
    }

    public void assertTextContentMessage(@NonNull IncomingMessage message, @NonNull List<String> contents) {
        assertNotNull(message.getTextContents());
        assertEquals(contents.size(), message.getTextContents().size());

        for (ContentMessage content : message.getTextContents()) {
            assertInstanceOf(ContentMessage.class, content);
            assertEquals(StandardCharsets.UTF_8, content.getCharset());
            assertNotNull(content.getData());
            assertTrue(contents.contains(content.getData()));
            assertEquals(
                TEXT_CONTENT_TYPE + "; charset=" + StandardCharsets.UTF_8.name().toUpperCase(),
                content.getContentType()
            );
            assertEquals(ContentMessageType.TEXT, content.getType());
        }

        assertEquals(String.join(";", contents), message.getTextContentsAsString(";"));
        assertEquals(String.join("\n", contents), message.getTextContentsAsString());
    }

    public void assertEmptyTextContentMessage(@NonNull IncomingMessage message) {
        assertNotNull(message.getTextContents());
        assertEquals(0, message.getTextContents().size());

        assertEquals("", message.getTextContentsAsString(";"));
        assertEquals("", message.getTextContentsAsString());
    }

    @SneakyThrows
    public void assertAttachmentsMessage(@NonNull BaseMessage baseMessage, @NonNull List<String> emailAttachments) {
        assertAttachmentsMessage(baseMessage.getAttachments(), emailAttachments);
    }

    @SneakyThrows
    public void assertAttachmentsMessage(
        @NonNull List<EmailAttachment> attachments,
        @NonNull List<String> emailAttachments
    ) {
        assertNotNull(attachments);
        assertEquals(attachments.size(), emailAttachments.size());

        for (String attachment : emailAttachments) {
            File sourceFile = new File(
                MessageAsserts.class
                    .getClassLoader()
                    .getResource(attachment.replace("classpath:", ""))
                    .toURI()
            );
            AttachmentType attachmentType = null;
            switch (sourceFile.getName()) {
                case "file.jpg":
                    attachmentType = AttachmentType.IMAGE;
                    break;
                case "file.txt":
                    attachmentType = AttachmentType.TEXT;
                    break;
                case "file.html":
                    attachmentType = AttachmentType.TEXT;
                    break;
                case "file.docx":
                    attachmentType = AttachmentType.APPLICATION;
                    break;
                case "file.zip":
                    attachmentType = AttachmentType.APPLICATION;
                    break;
            }
            byte[] contentOfFile = JavaCoreUtils.toByteArray(Files.newInputStream(sourceFile.toPath()));

            EmailAttachment messageAttachment = attachments.stream()
                .filter(item -> item.getName().equals(sourceFile.getName()))
                .findFirst()
                .orElse(null);

            assertNotNull(messageAttachment);
            assertNotNull(attachmentType);
            assertEquals(sourceFile.length(), messageAttachment.getSize());
            assertEquals(attachmentType, messageAttachment.getType());
            assertEquals(Arrays.toString(contentOfFile), Arrays.toString(messageAttachment.getData()));
        }
    }

    public void assertEmptyAttachmentsMessage(@NonNull BaseMessage baseMessage) {
        assertNotNull(baseMessage.getAttachments());
        assertEquals(0, baseMessage.getAttachments().size());
    }

    public void assertEmptyAttachmentsMessage(@NonNull MessageView messageView) {
        assertNotNull(messageView.getAttachments());
        assertEquals(0, messageView.getAttachments().size());
    }
}
