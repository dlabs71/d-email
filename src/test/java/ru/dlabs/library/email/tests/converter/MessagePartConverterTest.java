package ru.dlabs.library.email.tests.converter;

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
import org.junit.jupiter.api.Test;
import ru.dlabs.library.email.converter.incoming.MessagePartConverter;
import ru.dlabs.library.email.tests.converter.utils.MessageAsserts;
import ru.dlabs.library.email.tests.converter.utils.TestConverterUtils;
import ru.dlabs.library.email.dto.message.common.ContentMessage;
import ru.dlabs.library.email.dto.message.common.EmailAttachment;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;
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
        assertEquals(participants1.size(), 0);

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
        assertNull(result1);

        byte[] result2 = MessagePartConverter.getContentDefaultAsBytes(new MimeBodyPart());
        assertNull(result2);

        byte[] result3 = MessagePartConverter.getContentDefaultAsBytes(emptyAttachment);
        assertNull(result3);

        byte[] result4 = MessagePartConverter.getContentDefaultAsBytes(emailPartAttachment);
        assertNotNull(result4);
        assertNotNull(contentOfFile);
        assertEquals(result4.length, contentOfFile.length);
        assertEquals(Arrays.toString(result4), Arrays.toString(contentOfFile));

        byte[] result5 = MessagePartConverter.getContentDefaultAsBytes(emailPartContent);
        assertNotNull(result5);
        assertNotNull(contentOfFile);
        assertEquals(result5.length, content.getBytes(StandardCharsets.UTF_8).length);
        assertEquals(new String(result5), content);
    }

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
        assertEquals(result4, contentOfFile);

        String result5 = MessagePartConverter.getContentDefault(emailPartContent);
        assertNotNull(result5);
        assertNotNull(contentOfFile);
        assertEquals(result5, content);
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
        assertEquals(attachment2.getSize(), 0);
        assertEquals(attachment2.getName(), emptyAttachment.getFileName());
        assertEquals(attachment2.getContentType(), emptyAttachment.getContentType());
        assertEquals(attachment2.getType(), AttachmentType.APPLICATION);
        assertNull(attachment2.getData());

        EmailAttachment attachment3 = MessagePartConverter.getAttachment(emailPart);
        assertNotNull(attachment3);
        assertNotNull(emailPart);

        Field field = ByteArrayInputStream.class.getDeclaredField("count");
        field.setAccessible(true);
        Long sizeContent = ((Integer) field.get(emailPart.getContent())).longValue();

        assertEquals(attachment3.getSize(), sizeContent);
        assertEquals(attachment3.getName(), emailPart.getFileName());
        assertEquals(attachment3.getContentType(), emailPart.getContentType());
        assertEquals(attachment3.getType(), AttachmentType.APPLICATION);

        Field fieldBuf = ByteArrayInputStream.class.getDeclaredField("buf");
        fieldBuf.setAccessible(true);
        byte[] contentBuf = (byte[]) fieldBuf.get(emailPart.getContent());

        assertEquals(Arrays.toString(attachment3.getData()), Arrays.toString(contentBuf));
    }

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
        assertEquals(result2.getContents().size(), 5);
        assertEquals(result2.getAttachments().size(), 5);
        assertEquals(result2.getContentByType(TEXT_CONTENT_TYPE).size(), 2);
        assertEquals(result2.getContentByType(HTML_CONTENT_TYPE).size(), 3);

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
        assertEquals(resultContent.size(), sourceContents.size());
        for (String sourceContent : sourceContents) {
            ContentMessage content = resultContent.stream()
                .filter(item -> item.getData().equals(sourceContent))
                .findFirst()
                .orElse(null);
            assertNotNull(content);
            assertEquals(content.getContentType(), contentType);
            assertEquals(content.getCharset(), StandardCharsets.UTF_8);
            assertEquals(content.getType(), contentMessageType);
        }
    }

    public void assertRecipients(Address[] addresses, Set<EmailParticipant> participants) {
        assertNotNull(participants);
        assertNotNull(addresses);
        assertEquals(addresses.length, participants.size());
        for (Address address : addresses) {
            InternetAddress internetAddress = (InternetAddress) address;
            EmailParticipant participant = participants.stream()
                .filter(item -> item.getEmail().equals(internetAddress.getAddress()))
                .findFirst()
                .orElse(null);
            assertNotNull(participant);
            assertEquals(participant.getEmail(), internetAddress.getAddress());
            assertEquals(participant.getName(), internetAddress.getPersonal());
        }
    }
}
