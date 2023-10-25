package ru.dlabs.library.email.converter;

import jakarta.mail.Message;
import jakarta.mail.internet.MimeMessage;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.dlabs.library.email.converter.incoming.BaseMessageConverter;
import ru.dlabs.library.email.converter.utils.MessageAsserts;
import ru.dlabs.library.email.converter.utils.TestConverterUtils;
import ru.dlabs.library.email.dto.message.incoming.DefaultIncomingMessage;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-19</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
public class BaseMessageConverterIncomeMessageTest {

    /**
     * The test for:
     * <ul>
     *     <li>{@link BaseMessageConverter#convertToIncomingMessage(Message)}</li>
     * </ul>
     */
    @Test
    public void convertTest_0() {
        DefaultIncomingMessage incomingMessage = BaseMessageConverter.convertToIncomingMessage(null);
        Assertions.assertNull(incomingMessage);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link BaseMessageConverter#convertToIncomingMessage(Message)}</li>
     * </ul>
     */
    @Test
    public void convertTest_1() {
        MimeMessage message = TestConverterUtils.createEmptyMessage();
        DefaultIncomingMessage incomingMessage = BaseMessageConverter.convertToIncomingMessage(message);

        MessageAsserts.assertMessageEnvelop(incomingMessage, message, null);
        MessageAsserts.assertEmptyContentMessage(incomingMessage);
        MessageAsserts.assertEmptyTextContentMessage(incomingMessage);
        MessageAsserts.assertEmptyHtmlContentMessage(incomingMessage);
        MessageAsserts.assertEmptyAttachmentsMessage(incomingMessage);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link BaseMessageConverter#convertToIncomingMessage(Message)}</li>
     * </ul>
     */
    @Test
    public void convertTest_2() {
        String subject = "Captain Flint's Map";
        String content = "Treasure Island";

        MimeMessage message = TestConverterUtils.createSimpleMessage(subject, content);
        DefaultIncomingMessage incomingMessage = BaseMessageConverter.convertToIncomingMessage(message);

        MessageAsserts.assertMessageEnvelop(incomingMessage, message, subject);
        MessageAsserts.assertContentMessage(incomingMessage, content);
        MessageAsserts.assertTextContentMessage(incomingMessage, content);
        MessageAsserts.assertEmptyHtmlContentMessage(incomingMessage);
        MessageAsserts.assertEmptyAttachmentsMessage(incomingMessage);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link BaseMessageConverter#convertToIncomingMessage(Message)}</li>
     * </ul>
     */
    @Test
    public void convertTest_3() {
        String subject = "Captain Flint's Map";
        String contentText = "Treasure Island";
        String contentHtml = "<div>Treasure Island</div>";

        MimeMessage message = TestConverterUtils.createMessageWithHtml(subject, contentText, contentHtml);
        DefaultIncomingMessage incomingMessage = BaseMessageConverter.convertToIncomingMessage(message);

        MessageAsserts.assertMessageEnvelop(incomingMessage, message, subject);
        MessageAsserts.assertTextContentMessage(incomingMessage, contentText);
        MessageAsserts.assertHtmlContentMessage(incomingMessage, contentHtml);
        MessageAsserts.assertEmptyAttachmentsMessage(incomingMessage);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link BaseMessageConverter#convertToIncomingMessage(Message)}</li>
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
        DefaultIncomingMessage incomingMessage = BaseMessageConverter.convertToIncomingMessage(message);

        MessageAsserts.assertMessageEnvelop(incomingMessage, message, subject);
        MessageAsserts.assertContentMessage(
            incomingMessage,
            Arrays.asList(contentText1, contentText2)
        );
        MessageAsserts.assertTextContentMessage(incomingMessage, Arrays.asList(contentText1, contentText2));
        MessageAsserts.assertEmptyHtmlContentMessage(incomingMessage);
        MessageAsserts.assertEmptyAttachmentsMessage(incomingMessage);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link BaseMessageConverter#convertToIncomingMessage(Message)}</li>
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
        DefaultIncomingMessage incomingMessage = BaseMessageConverter.convertToIncomingMessage(message);

        MessageAsserts.assertMessageEnvelop(incomingMessage, message, subject);
        MessageAsserts.assertContentMessage(incomingMessage, content);
        MessageAsserts.assertTextContentMessage(incomingMessage, content);
        MessageAsserts.assertEmptyHtmlContentMessage(incomingMessage);
        MessageAsserts.assertAttachmentsMessage(incomingMessage, attachments);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link BaseMessageConverter#convertToIncomingMessage(Message)}</li>
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
        DefaultIncomingMessage incomingMessage = BaseMessageConverter.convertToIncomingMessage(message);

        MessageAsserts.assertMessageEnvelop(incomingMessage, message, subject);
        MessageAsserts.assertEmptyContentMessage(incomingMessage);
        MessageAsserts.assertEmptyTextContentMessage(incomingMessage);
        MessageAsserts.assertEmptyHtmlContentMessage(incomingMessage);
        MessageAsserts.assertAttachmentsMessage(incomingMessage, attachments);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link BaseMessageConverter#convertToIncomingMessage(Message)}</li>
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
        DefaultIncomingMessage incomingMessage = BaseMessageConverter.convertToIncomingMessage(message);

        MessageAsserts.assertMessageEnvelop(incomingMessage, message, subject);
        MessageAsserts.assertContentMessage(
            incomingMessage,
            Arrays.asList(contentText1, contentText2, contentHtml1, contentHtml2)
        );
        MessageAsserts.assertTextContentMessage(incomingMessage, Arrays.asList(contentText1, contentText2));
        MessageAsserts.assertHtmlContentMessage(incomingMessage, Arrays.asList(contentHtml1, contentHtml2));
        MessageAsserts.assertEmptyAttachmentsMessage(incomingMessage);
    }
}
