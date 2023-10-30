package ru.dlabs.library.email.tests.converter.outgoing;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.mail.Message;
import jakarta.mail.Session;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import ru.dlabs.library.email.converter.outgoing.JakartaMessageConverter;
import ru.dlabs.library.email.dto.message.outgoing.DefaultOutgoingMessage;
import ru.dlabs.library.email.dto.message.outgoing.OutgoingMessage;
import ru.dlabs.library.email.tests.converter.outgoing.utils.MessageAsserts;
import ru.dlabs.library.email.tests.converter.outgoing.utils.TestConverterUtils;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-26</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Order(312)
public class JakartaMessagePartConverterTest {

    /**
     * The test for:
     * <ul>
     *     <li>{@link JakartaMessageConverter#convert(OutgoingMessage, Session, String, String)} </li>
     * </ul>
     */
    @Test
    @SneakyThrows
    public void convertTest_0() {
        Message message = JakartaMessageConverter.convert(null, null, null, null);
        assertNull(message);

        assertThrows(IllegalArgumentException.class, () -> {
            JakartaMessageConverter.convert(new DefaultOutgoingMessage(null, null, null, null), null, null, null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            JakartaMessageConverter.convert(
                new DefaultOutgoingMessage(null, null, null, null),
                null,
                "email@email.com",
                null
            );
        });

        assertThrows(IllegalArgumentException.class, () -> {
            JakartaMessageConverter.convert(
                new DefaultOutgoingMessage(null, null, null, null),
                Session.getDefaultInstance(new Properties()),
                null,
                null
            );
        });
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link JakartaMessageConverter#convert(OutgoingMessage, Session, String, String)} </li>
     * </ul>
     */
    @Test
    @SneakyThrows
    public void convertTest_1() {
        String fromEmail = "email_from@email.com";
        String fromName = "Sender name";

        DefaultOutgoingMessage outgoingMessage = TestConverterUtils.createEmptyMessage();
        Message message = JakartaMessageConverter.convert(
            outgoingMessage,
            Session.getDefaultInstance(new Properties()),
            fromEmail,
            fromName
        );

        MessageAsserts.assertEnvelop(outgoingMessage, fromEmail, fromName, message);
        MessageAsserts.assertEmptyContent(message);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link JakartaMessageConverter#convert(OutgoingMessage, Session, String, String)} </li>
     * </ul>
     */
    @Test
    @SneakyThrows
    public void convertTest_2() {
        String subject = "The subject of a message";
        String content = "The content of a message";

        String fromEmail = "email_from@email.com";
        String fromName = "Sender name";

        DefaultOutgoingMessage outgoingMessage = TestConverterUtils.createMessageWithoutRecipients(subject, content);
        Message message = JakartaMessageConverter.convert(
            outgoingMessage,
            Session.getDefaultInstance(new Properties()),
            fromEmail,
            fromName
        );

        MessageAsserts.assertEnvelop(outgoingMessage, fromEmail, fromName, message);
        MessageAsserts.assertMessageContent(outgoingMessage, message);
        MessageAsserts.assertEmptyAttachments(message);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link JakartaMessageConverter#convert(OutgoingMessage, Session, String, String)} </li>
     * </ul>
     */
    @Test
    @SneakyThrows
    public void convertTest_3() {
        String subject = "The subject of a message";
        String content = "The content of a message";

        String fromEmail = "email_from@email.com";
        String fromName = "Sender name";

        DefaultOutgoingMessage outgoingMessage = TestConverterUtils.createSimpleMessage(subject, content);
        Message message = JakartaMessageConverter.convert(
            outgoingMessage,
            Session.getDefaultInstance(new Properties()),
            fromEmail,
            fromName
        );

        MessageAsserts.assertEnvelop(outgoingMessage, fromEmail, fromName, message);
        MessageAsserts.assertMessageContent(outgoingMessage, message);
        MessageAsserts.assertEmptyAttachments(message);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link JakartaMessageConverter#convert(OutgoingMessage, Session, String, String)} </li>
     * </ul>
     */
    @Test
    @SneakyThrows
    public void convertTest_4() {
        String subject = "The subject of a message";
        String content = "<div>The content of a message</div>";

        String fromEmail = "email_from@email.com";
        String fromName = "Sender name";

        DefaultOutgoingMessage outgoingMessage = TestConverterUtils.createHtmlMessage(subject, content);
        Message message = JakartaMessageConverter.convert(
            outgoingMessage,
            Session.getDefaultInstance(new Properties()),
            fromEmail,
            fromName
        );

        MessageAsserts.assertEnvelop(outgoingMessage, fromEmail, fromName, message);
        MessageAsserts.assertMessageContent(outgoingMessage, message);
        MessageAsserts.assertEmptyAttachments(message);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link JakartaMessageConverter#convert(OutgoingMessage, Session, String, String)} </li>
     * </ul>
     */
    @Test
    @SneakyThrows
    public void convertTest_5() {
        String subject = "The subject of a message";

        String fromEmail = "email_from@email.com";
        String fromName = "Sender name";

        DefaultOutgoingMessage outgoingMessage = TestConverterUtils.createMessageWithEmptyContent(subject);
        Message message = JakartaMessageConverter.convert(
            outgoingMessage,
            Session.getDefaultInstance(new Properties()),
            fromEmail,
            fromName
        );

        MessageAsserts.assertEnvelop(outgoingMessage, fromEmail, fromName, message);
        MessageAsserts.assertEmptyContent(message);
        MessageAsserts.assertEmptyAttachments(message);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link JakartaMessageConverter#convert(OutgoingMessage, Session, String, String)} </li>
     * </ul>
     */
    @Test
    @SneakyThrows
    public void convertTest_6() {
        String subject = "The subject of a message";
        String content = "<div>The content of a message</div>";

        List<String> attachments = Arrays.asList(
            "classpath:attachments/file.jpg",
            "classpath:attachments/file.txt",
            "classpath:attachments/file.html",
            "classpath:attachments/file.docx",
            "classpath:attachments/file.zip"
        );

        String fromEmail = "email_from@email.com";
        String fromName = "Sender name";

        DefaultOutgoingMessage outgoingMessage = TestConverterUtils.createMessageWithAttachments(
            subject,
            content,
            attachments
        );
        Message message = JakartaMessageConverter.convert(
            outgoingMessage,
            Session.getDefaultInstance(new Properties()),
            fromEmail,
            fromName
        );

        MessageAsserts.assertEnvelop(outgoingMessage, fromEmail, fromName, message);
        MessageAsserts.assertMessageContent(outgoingMessage, message);
        MessageAsserts.assertMessageAttachments(outgoingMessage, message);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link JakartaMessageConverter#convert(OutgoingMessage, Session, String, String)} </li>
     * </ul>
     */
    @Test
    @SneakyThrows
    public void convertTest_7() {
        String subject = "The subject of a message";

        List<String> attachments = Arrays.asList(
            "classpath:attachments/file.jpg",
            "classpath:attachments/file.txt",
            "classpath:attachments/file.html",
            "classpath:attachments/file.docx",
            "classpath:attachments/file.zip"
        );

        String fromEmail = "email_from@email.com";
        String fromName = "Sender name";

        DefaultOutgoingMessage outgoingMessage = TestConverterUtils.createMessageWithEmptyContentAndAttachments(
            subject,
            attachments
        );
        Message message = JakartaMessageConverter.convert(
            outgoingMessage,
            Session.getDefaultInstance(new Properties()),
            fromEmail,
            fromName
        );

        MessageAsserts.assertEnvelop(outgoingMessage, fromEmail, fromName, message);
        MessageAsserts.assertEmptyContent(message);
        MessageAsserts.assertMessageAttachments(outgoingMessage, message);
    }
}
