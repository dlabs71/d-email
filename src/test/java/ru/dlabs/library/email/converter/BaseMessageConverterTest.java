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
import ru.dlabs.library.email.dto.message.common.BaseMessage;

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
    public void convertTest_0() {
        BaseMessage baseMessage = BaseMessageConverter.convert(null);
        Assertions.assertNull(baseMessage);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link BaseMessageConverter#convert(Message)}</li>
     * </ul>
     */
    @Test
    public void convertTest_1() {
        MimeMessage message = TestConverterUtils.createEmptyMessage();
        BaseMessage baseMessage = BaseMessageConverter.convert(message);

        MessageAsserts.assertMessageEnvelop(baseMessage, message, null);
        MessageAsserts.assertEmptyContentMessage(baseMessage);
        MessageAsserts.assertEmptyAttachmentsMessage(baseMessage);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link BaseMessageConverter#convert(Message)}</li>
     * </ul>
     */
    @Test
    public void convertTest_2() {
        String subject = "Captain Flint's Map";
        String content = "Treasure Island";

        MimeMessage message = TestConverterUtils.createSimpleMessage(subject, content);
        BaseMessage baseMessage = BaseMessageConverter.convert(message);

        MessageAsserts.assertMessageEnvelop(baseMessage, message, subject);
        MessageAsserts.assertContentMessage(baseMessage, content);
        MessageAsserts.assertEmptyAttachmentsMessage(baseMessage);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link BaseMessageConverter#convert(Message)}</li>
     * </ul>
     */
    @Test
    public void convertTest_3() {
        String subject = "Captain Flint's Map";
        String content = "Treasure Island";
        String contentHtml = "<div>Treasure Island</div>";

        MimeMessage message = TestConverterUtils.createMessageWithHtml(subject, content, contentHtml);
        BaseMessage baseMessage = BaseMessageConverter.convert(message);

        MessageAsserts.assertMessageEnvelop(baseMessage, message, subject);
        MessageAsserts.assertContentMessage(baseMessage, Arrays.asList(content, contentHtml));
        MessageAsserts.assertEmptyAttachmentsMessage(baseMessage);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link BaseMessageConverter#convert(Message)}</li>
     * </ul>
     */
    @Test
    public void convertTest_4() {
        String subject = "Captain Flint's Map";
        String content1 = "Treasure Island. Part 1";
        String content2 = "Treasure Island. Part 2";

        MimeMessage message = TestConverterUtils.createMessageWithMultipleTextContent(
            subject,
            Arrays.asList(content1, content2)
        );
        BaseMessage baseMessage = BaseMessageConverter.convert(message);

        MessageAsserts.assertMessageEnvelop(baseMessage, message, subject);
        MessageAsserts.assertContentMessage(baseMessage, Arrays.asList(content1, content2));
        MessageAsserts.assertEmptyAttachmentsMessage(baseMessage);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link BaseMessageConverter#convert(Message)}</li>
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
        BaseMessage baseMessage = BaseMessageConverter.convert(message);

        MessageAsserts.assertMessageEnvelop(baseMessage, message, subject);
        MessageAsserts.assertContentMessage(baseMessage, content);
        MessageAsserts.assertAttachmentsMessage(baseMessage, attachments);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link BaseMessageConverter#convert(Message)}</li>
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
        BaseMessage baseMessage = BaseMessageConverter.convert(message);

        MessageAsserts.assertMessageEnvelop(baseMessage, message, subject);
        MessageAsserts.assertEmptyContentMessage(baseMessage);
        MessageAsserts.assertAttachmentsMessage(baseMessage, attachments);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link BaseMessageConverter#convert(Message)}</li>
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
        BaseMessage baseMessage = BaseMessageConverter.convert(message);

        MessageAsserts.assertMessageEnvelop(baseMessage, message, subject);
        MessageAsserts.assertContentMessage(
            baseMessage,
            Arrays.asList(contentText1, contentText2, contentHtml1, contentHtml2)
        );
        MessageAsserts.assertEmptyAttachmentsMessage(baseMessage);
    }
}
