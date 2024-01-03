package ru.dlabs71.library.email.tests.converter.incoming;

import jakarta.mail.Message;
import jakarta.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import ru.dlabs71.library.email.converter.incoming.MessageViewConverter;
import ru.dlabs71.library.email.tests.converter.incoming.utils.MessageAsserts;
import ru.dlabs71.library.email.tests.converter.incoming.utils.TestConverterUtils;
import ru.dlabs71.library.email.dto.message.incoming.MessageView;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-19</div>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Order(325)
public class MessageViewConverterTest {

    /**
     * The test for:
     * <ul>
     *     <li>{@link MessageViewConverter#convert(Message)}</li>
     * </ul>
     */
    @Test
    public void convertTest_0() {
        MessageView messageView = MessageViewConverter.convert(null);
        Assertions.assertNull(messageView);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link MessageViewConverter#convert(Message)}</li>
     * </ul>
     */
    @Test
    public void convertTest_1() {
        MimeMessage message = TestConverterUtils.createEmptyMessage();
        MessageView messageView = MessageViewConverter.convert(message);

        MessageAsserts.assertMessageEnvelop(messageView, message, null);
        MessageAsserts.assertEmptyContentMessage(messageView);
        MessageAsserts.assertEmptyAttachmentsMessage(messageView);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link MessageViewConverter#convert(Message)}</li>
     * </ul>
     */
    @Test
    public void convertTest_2() {
        String subject = "Captain Flint's Map";
        String content = "Treasure Island";

        MimeMessage message = TestConverterUtils.createSimpleMessage(subject, content);
        MessageView messageView = MessageViewConverter.convert(message);

        MessageAsserts.assertMessageEnvelop(messageView, message, subject);
        MessageAsserts.assertEmptyContentMessage(messageView);
        MessageAsserts.assertEmptyAttachmentsMessage(messageView);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link MessageViewConverter#convert(Message)}</li>
     * </ul>
     */
    @Test
    public void convertTest_3() {
        String subject = "Captain Flint's Map";
        String contentText = "Treasure Island";
        String contentHtml = "<div>Treasure Island</div>";

        MimeMessage message = TestConverterUtils.createMessageWithHtml(subject, contentText, contentHtml);
        MessageView messageView = MessageViewConverter.convert(message);

        MessageAsserts.assertMessageEnvelop(messageView, message, subject);
        MessageAsserts.assertEmptyContentMessage(messageView);
        MessageAsserts.assertEmptyAttachmentsMessage(messageView);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link MessageViewConverter#convert(Message)}</li>
     * </ul>
     */
    @Test
    public void convertTest_4() {
        String subject = "Captain Flint's Map";

        String contentText1 = "Treasure Island. Part 1";
        String contentText2 = "Treasure Island. Part 2";

        MimeMessage message = TestConverterUtils.createMessageWithMultipleTextContent(
            subject,
            Arrays.asList(contentText1, contentText2)
        );
        MessageView messageView = MessageViewConverter.convert(message);

        MessageAsserts.assertMessageEnvelop(messageView, message, subject);
        MessageAsserts.assertEmptyContentMessage(messageView);
        MessageAsserts.assertEmptyAttachmentsMessage(messageView);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link MessageViewConverter#convert(Message)}</li>
     * </ul>
     */
    @Test
    public void convertTest_5() {
        String subject = "Captain Flint's Map";
        String content = "Treasure Island";
        List<String> attachments = Arrays.asList(
            "classpath:attachments/file.jpg",
            "classpath:attachments/file.txt",
            "classpath:attachments/file.html",
            "classpath:attachments/file.docx",
            "classpath:attachments/file.zip"
        );

        MimeMessage message = TestConverterUtils.createMessageWithAttachments(subject, content, attachments);
        MessageView messageView = MessageViewConverter.convert(message);

        MessageAsserts.assertMessageEnvelop(messageView, message, subject);
        MessageAsserts.assertEmptyContentMessage(messageView);
        MessageAsserts.assertEmptyAttachmentsMessage(messageView);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link MessageViewConverter#convert(Message)}</li>
     * </ul>
     */
    @Test
    public void convertTest_6() {
        String subject = "Captain Flint's Map";
        List<String> attachments = Arrays.asList(
            "classpath:attachments/file.jpg",
            "classpath:attachments/file.txt",
            "classpath:attachments/file.html",
            "classpath:attachments/file.docx",
            "classpath:attachments/file.zip"
        );

        MimeMessage message = TestConverterUtils.createMessageWithAttachments(subject, null, attachments);
        MessageView messageView = MessageViewConverter.convert(message);

        MessageAsserts.assertMessageEnvelop(messageView, message, subject);
        MessageAsserts.assertEmptyContentMessage(messageView);
        MessageAsserts.assertEmptyAttachmentsMessage(messageView);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link MessageViewConverter#convert(Message)}</li>
     * </ul>
     */
    @Test
    public void convertTest_7() {
        String subject = "Captain Flint's Map";

        String contentText1 = "Treasure Island. Part 1";
        String contentText2 = "Treasure Island. Part 2";

        String contentHtml1 = "<div>Treasure Island. <strong>Part 1</strong></div>";
        String contentHtml2 = "<div>Treasure Island. <strong>Part 2</strong></div>";

        MimeMessage message = TestConverterUtils.createMessageWithMultipleContent(
            subject,
            Arrays.asList(contentText1, contentText2),
            Arrays.asList(contentHtml1, contentHtml2)
        );
        MessageView messageView = MessageViewConverter.convert(message);

        MessageAsserts.assertMessageEnvelop(messageView, message, subject);
        MessageAsserts.assertEmptyContentMessage(messageView);
        MessageAsserts.assertEmptyAttachmentsMessage(messageView);
    }
}
