package ru.dlabs.library.email.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import jakarta.mail.Message;
import jakarta.mail.internet.MimeMessage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import ru.dlabs.library.email.dto.message.common.BaseMessage;
import ru.dlabs.library.email.dto.message.common.ContentMessage;
import ru.dlabs.library.email.dto.message.common.EmailAttachment;
import ru.dlabs.library.email.type.AttachmentType;
import ru.dlabs.library.email.util.IOUtils;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-17</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
public class BaseMessageConverterTest {

    /**
     * The test for:
     * <ul>
     *     <li>{@link BaseMessageConverter#convert(Message)}</li>
     * </ul>
     */
    @Test
    public void convertEnvelopDataTest_1() {
        String subject = "Captain Flint's Map";
        String content = "Treasure Island";

        MimeMessage message = TestConverterUtils.createSimpleMessage(subject, content);
        BaseMessage baseMessage = BaseMessageConverter.convertToIncomingMessage(message);
        MessageAsserts.assertMessagesAsEnvelop(baseMessage, message, subject);

        assertContentMessage(baseMessage, content);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link BaseMessageConverter#convert(Message)}</li>
     * </ul>
     */
    @Test
    public void convertEnvelopDataTest_2() {
        String subject = "Captain Flint's Map";
        String content = "Treasure Island";
        String contentHtml = "<div>Treasure Island</div>";

        MimeMessage message = TestConverterUtils.createMessageWithHtml(subject, content, contentHtml);
        BaseMessage baseMessage = BaseMessageConverter.convert(message);
        MessageAsserts.assertMessagesAsEnvelop(baseMessage, message, subject);

        assertContentMessage(baseMessage, content);
    }

    @Test
    public void convertEnvelopDataTest_3() {
        String subject = "Captain Flint's Map";
        String content = "Treasure Island";
        String contentHtml = "<div>Treasure Island</div>";
        List<String> attachments = Arrays.asList(
            "classpath:attachments/file.jpg",
            "classpath:attachments/file.txt",
            "classpath:attachments/file.html",
            "classpath:attachments/file.docx",
            "classpath:attachments/file.zip"
        );

        MimeMessage message = TestConverterUtils.createMessageWithHtml(subject, content, contentHtml);
        BaseMessage baseMessage = BaseMessageConverter.convert(message);
        MessageAsserts.assertMessagesAsEnvelop(baseMessage, message, subject);

        assertContentMessage(baseMessage, content);
        assertAttachmentsMessage(baseMessage, attachments);
    }

    private void assertContentMessage(BaseMessage baseMessage, String content) {
        assertNotNull(baseMessage.getContents());
        assertEquals(baseMessage.getContents().size(), 1);

        ContentMessage contentMessage = baseMessage.getContents().get(0);
        assertEquals(contentMessage.getData(), content);
        assertEquals(contentMessage.getCharset(), StandardCharsets.UTF_8);
        assertEquals(contentMessage.getContentType(), "text/plain; charset=utf-8");
    }

    @SneakyThrows
    private void assertAttachmentsMessage(BaseMessage baseMessage, List<String> emailAttachments) {
        assertNotNull(baseMessage.getAttachments());
        assertEquals(baseMessage.getAttachments().size(), emailAttachments.size());

        for (String attachment : emailAttachments) {
            File sourceFile = new File(this.getClass().getClassLoader().getResource(attachment).toURI());
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
            byte[] contentOfFile = IOUtils.toByteArray(Files.newInputStream(sourceFile.toPath()));

            EmailAttachment messageAttachment = baseMessage.getAttachments().stream()
                .filter(item -> item.getName().equals(sourceFile.getName()))
                .findFirst()
                .orElse(null);

            assertNotNull(messageAttachment);
            assertNotNull(attachmentType);
            assertEquals(messageAttachment.getSize(), sourceFile.length());
            assertEquals(messageAttachment.getType(), attachmentType);
            assertEquals(messageAttachment.getData(), contentOfFile);
        }
    }

}
