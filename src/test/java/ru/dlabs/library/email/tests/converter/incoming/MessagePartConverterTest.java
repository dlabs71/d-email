package ru.dlabs.library.email.tests.converter.incoming;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.dlabs.library.email.util.HttpUtils.CONTENT_TYPE_HDR;
import static ru.dlabs.library.email.util.HttpUtils.HTML_CONTENT_TYPE;
import static ru.dlabs.library.email.util.HttpUtils.TEXT_CONTENT_TYPE;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.Part;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.util.ByteArrayDataSource;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import ru.dlabs.library.email.converter.incoming.MessagePartConverter;
import ru.dlabs.library.email.dto.message.common.ContentMessage;
import ru.dlabs.library.email.dto.message.common.EmailAttachment;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;
import ru.dlabs.library.email.tests.converter.incoming.utils.MessageAsserts;
import ru.dlabs.library.email.tests.converter.incoming.utils.TestConverterUtils;
import ru.dlabs.library.email.type.AttachmentType;
import ru.dlabs.library.email.type.ContentMessageType;
import ru.dlabs.library.email.util.JavaCoreUtils;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-23</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Order(322)
public class MessagePartConverterTest {

    /**
     * The test for:
     * <ul>
     *     <li>{@link MessagePartConverter#getRecipients(Message)}</li>
     * </ul>
     */
    @Test
    @SneakyThrows
    public void getParticipantsTest_1() {
        Message message = TestConverterUtils.createEmptyMessage();
        Set<EmailParticipant> participants1 = MessagePartConverter.getRecipients(message);

        assertNotNull(participants1);
        assertNull(message.getRecipients(Message.RecipientType.TO));
        assertEquals(0, participants1.size());

        Set<EmailParticipant> participants2 = MessagePartConverter.getRecipients(null);
        assertNull(participants2);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link MessagePartConverter#getRecipients(Message)}</li>
     * </ul>
     */
    @Test
    @SneakyThrows
    public void getParticipantsTest_2() {
        Message message1 = TestConverterUtils.createSimpleMessage("subject", "content");
        Message message2 = TestConverterUtils.createMessageWithoutSenders("subject", "content");

        Set<EmailParticipant> participants1 = MessagePartConverter.getRecipients(message1);
        Set<EmailParticipant> participants2 = MessagePartConverter.getRecipients(message2);

        assertRecipients(message1.getRecipients(Message.RecipientType.TO), participants1);
        assertRecipients(message2.getRecipients(Message.RecipientType.TO), participants2);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link MessagePartConverter#getContentDefaultAsBytes(Part)}</li>
     * </ul>
     */
    @Test
    @SneakyThrows
    public void getContentDefaultAsBytesTest() {
        String attachmentPath = "classpath:attachments/file.docx";
        String content = "content message";
        File sourceFile = new File(
            MessageAsserts.class
                .getClassLoader()
                .getResource(attachmentPath.replace("classpath:", ""))
                .toURI()
        );
        byte[] contentOfFile = JavaCoreUtils.toByteArray(Files.newInputStream(sourceFile.toPath()));

        MimeBodyPart emptyAttachment = new MimeBodyPart();
        DataSource dataSource = new ByteArrayDataSource(
            (byte[]) null,
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        );
        emptyAttachment.setDataHandler(new DataHandler(dataSource));
        emptyAttachment.setFileName("file.docx");
        emptyAttachment.addHeader(
            CONTENT_TYPE_HDR,
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        );


        Part emailPartAttachment = TestConverterUtils.createAttachment(attachmentPath);
        Part emailPartContent = TestConverterUtils.createContent(content, "text");

        byte[] result1 = MessagePartConverter.getContentDefaultAsBytes(null);
        assertNotNull(result1);
        assertEquals(0, result1.length);

        byte[] result2 = MessagePartConverter.getContentDefaultAsBytes(new MimeBodyPart());
        assertNotNull(result2);
        assertEquals(0, result2.length);

        byte[] result3 = MessagePartConverter.getContentDefaultAsBytes(emptyAttachment);
        assertNotNull(result3);
        assertEquals(0, result3.length);

        byte[] result4 = MessagePartConverter.getContentDefaultAsBytes(emailPartAttachment);
        assertNotNull(result4);
        assertNotNull(contentOfFile);
        assertEquals(contentOfFile.length, result4.length);
        assertEquals(Arrays.toString(contentOfFile), Arrays.toString(result4));

        byte[] result5 = MessagePartConverter.getContentDefaultAsBytes(emailPartContent);
        assertNotNull(result5);
        assertNotNull(contentOfFile);
        assertEquals(content.getBytes(StandardCharsets.UTF_8).length, result5.length);
        assertEquals(content, new String(result5));
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link MessagePartConverter#getContentDefault(Part)}</li>
     * </ul>
     */
    @Test
    @SneakyThrows
    public void getContentDefaultTest() {
        String attachmentPath = "classpath:attachments/file.docx";
        String content = "content message";
        File sourceFile = new File(
            MessageAsserts.class
                .getClassLoader()
                .getResource(attachmentPath.replace("classpath:", ""))
                .toURI()
        );
        String contentOfFile = new BufferedReader(
            new InputStreamReader(Files.newInputStream(sourceFile.toPath()))
        ).lines().collect(Collectors.joining("\n"));

        MimeBodyPart emptyAttachment = new MimeBodyPart();
        DataSource dataSource = new ByteArrayDataSource(
            (byte[]) null,
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        );
        emptyAttachment.setDataHandler(new DataHandler(dataSource));
        emptyAttachment.setFileName("file.docx");
        emptyAttachment.addHeader(
            CONTENT_TYPE_HDR,
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        );


        Part emailPartAttachment = TestConverterUtils.createAttachment(attachmentPath);
        Part emailPartContent = TestConverterUtils.createContent(content, "text");

        String result1 = MessagePartConverter.getContentDefault(null);
        assertNull(result1);

        String result2 = MessagePartConverter.getContentDefault(new MimeBodyPart());
        assertNull(result2);

        String result3 = MessagePartConverter.getContentDefault(emptyAttachment);
        assertNull(result3);

        String result4 = MessagePartConverter.getContentDefault(emailPartAttachment);
        assertNotNull(result4);
        assertNotNull(contentOfFile);
        assertEquals(contentOfFile, result4);

        String result5 = MessagePartConverter.getContentDefault(emailPartContent);
        assertNotNull(result5);
        assertNotNull(contentOfFile);
        assertEquals(content, result5);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link MessagePartConverter#getAttachment(Part)}</li>
     * </ul>
     */
    @Test
    @SneakyThrows
    public void getAttachmentTest() {
        Part emailPart = TestConverterUtils.createAttachment("classpath:attachments/file.docx");
        MimeBodyPart emptyAttachment = new MimeBodyPart();
        DataSource dataSource = new ByteArrayDataSource(
            (byte[]) null,
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        );
        emptyAttachment.setDataHandler(new DataHandler(dataSource));
        emptyAttachment.setFileName("file.docx");
        emptyAttachment.addHeader(
            CONTENT_TYPE_HDR,
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document"
        );

        EmailAttachment attachment1 = MessagePartConverter.getAttachment(null);
        assertNull(attachment1);

        EmailAttachment attachment2 = MessagePartConverter.getAttachment(emptyAttachment);
        assertNotNull(attachment2);
        assertNotNull(emptyAttachment);
        assertEquals(0, attachment2.getSize());
        assertEquals(emptyAttachment.getFileName(), attachment2.getName());
        assertEquals(emptyAttachment.getContentType(), attachment2.getContentType());
        assertEquals(AttachmentType.APPLICATION, attachment2.getType());
        assertNotNull(attachment2.getData());
        assertEquals(0, attachment2.getData().length);

        EmailAttachment attachment3 = MessagePartConverter.getAttachment(emailPart);
        assertNotNull(attachment3);
        assertNotNull(emailPart);

        Field field = ByteArrayInputStream.class.getDeclaredField("count");
        field.setAccessible(true);
        Integer sizeContent = (Integer) field.get(emailPart.getContent());

        assertEquals(sizeContent, attachment3.getSize());
        assertEquals(emailPart.getFileName(), attachment3.getName());
        assertEquals(emailPart.getContentType(), attachment3.getContentType());
        assertEquals(AttachmentType.APPLICATION, attachment3.getType());

        Field fieldBuf = ByteArrayInputStream.class.getDeclaredField("buf");
        fieldBuf.setAccessible(true);
        byte[] contentBuf = (byte[]) fieldBuf.get(emailPart.getContent());

        assertEquals(Arrays.toString(contentBuf), Arrays.toString(attachment3.getData()));
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link MessagePartConverter#getContent(Part)} </li>
     * </ul>
     */
    @Test
    public void getContentTest() {
        List<String> attachmentPaths = Arrays.asList(
            "classpath:attachments/file.jpg",
            "classpath:attachments/file.txt",
            "classpath:attachments/file.html",
            "classpath:attachments/file.docx",
            "classpath:attachments/file.zip"
        );
        List<String> textContents = Arrays.asList("Text message 1", "Text message 2");
        List<String> htmlContents = Arrays.asList(
            "<div>Html message 1</div>",
            "<div>Html message 2</div>",
            "<div>Html message 3</div>"
        );

        Message message = TestConverterUtils.constructMessage(
            "Subject of the message",
            textContents,
            htmlContents,
            new InternetAddress[] {
                TestConverterUtils.createAddress("john007@island.com", "John Silver"),
                },
            new InternetAddress[] {
                TestConverterUtils.createAddress("billy@island.com", "Billy Bones"),
                TestConverterUtils.createAddress("livesey@island.com", "Dr. Livesey"),
                TestConverterUtils.createAddress("pew@island.com", "Blind Pew")
            },
            attachmentPaths
        );

        MessagePartConverter.ContentAndAttachments result1 = MessagePartConverter.getContent(null);
        assertNotNull(result1);
        assertTrue(result1.isEmpty());

        MessagePartConverter.ContentAndAttachments result2 = MessagePartConverter.getContent(message);
        assertNotNull(result2);
        assertFalse(result2.isEmpty());
        assertEquals(5, result2.getContents().size());
        assertEquals(5, result2.getAttachments().size());
        assertEquals(2, result2.getContentByType(TEXT_CONTENT_TYPE).size());
        assertEquals(3, result2.getContentByType(HTML_CONTENT_TYPE).size());

        assertContents(
            result2.getContentByType("text/plain"),
            textContents,
            "text/plain; charset=UTF-8",
            ContentMessageType.TEXT
        );
        assertContents(
            result2.getContentByType("text/html"),
            htmlContents,
            "text/html; charset=UTF-8",
            ContentMessageType.HTML
        );

        MessageAsserts.assertAttachmentsMessage(result2.getAttachments(), attachmentPaths);
    }

    public void assertContents(
        List<ContentMessage> resultContent,
        List<String> sourceContents,
        String contentType,
        ContentMessageType contentMessageType
    ) {
        assertNotNull(resultContent);
        assertEquals(sourceContents.size(), resultContent.size());
        for (String sourceContent : sourceContents) {
            ContentMessage content = resultContent.stream()
                .filter(item -> item.getData().equals(sourceContent))
                .findFirst()
                .orElse(null);
            assertNotNull(content);
            assertEquals(contentType, content.getContentType());
            assertEquals(StandardCharsets.UTF_8, content.getCharset());
            assertEquals(contentMessageType, content.getType());
        }
    }

    public void assertRecipients(Address[] addresses, Set<EmailParticipant> participants) {
        assertNotNull(participants);
        assertNotNull(addresses);
        assertEquals(participants.size(), addresses.length);
        for (Address address : addresses) {
            InternetAddress internetAddress = (InternetAddress) address;
            EmailParticipant participant = participants.stream()
                .filter(item -> item.getEmail().equals(internetAddress.getAddress()))
                .findFirst()
                .orElse(null);
            assertNotNull(participant);
            assertEquals(internetAddress.getAddress(), participant.getEmail());
            assertEquals(internetAddress.getPersonal(), participant.getName());
        }
    }
}
