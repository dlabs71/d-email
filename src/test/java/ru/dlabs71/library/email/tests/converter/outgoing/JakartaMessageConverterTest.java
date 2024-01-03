package ru.dlabs71.library.email.tests.converter.outgoing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static ru.dlabs71.library.email.util.ProtocolUtils.TEXT_CONTENT_TYPE;

import jakarta.mail.BodyPart;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import ru.dlabs71.library.email.converter.outgoing.JakartaMessagePartConverter;
import ru.dlabs71.library.email.dto.message.common.ContentMessage;
import ru.dlabs71.library.email.dto.message.common.EmailAttachment;
import ru.dlabs71.library.email.dto.message.outgoing.DefaultOutgoingMessage;
import ru.dlabs71.library.email.dto.message.outgoing.OutgoingMessage;
import ru.dlabs71.library.email.tests.converter.outgoing.utils.MessageAsserts;
import ru.dlabs71.library.email.tests.converter.outgoing.utils.TestConverterUtils;
import ru.dlabs71.library.email.util.AttachmentUtils;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-26</div>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Order(313)
public class JakartaMessageConverterTest {

    /**
     * The test for:
     * <ul>
     *     <li>{@link JakartaMessagePartConverter#convertBodyPart(ContentMessage)}</li>
     *      <li>{@link JakartaMessagePartConverter#convertBodyPart(OutgoingMessage)}</li>
     * </ul>
     */
    @Test
    public void convertBodyTest_0() {
        BodyPart bodyPart1 = JakartaMessagePartConverter.convertBodyPart((ContentMessage) null);
        assertNull(bodyPart1);

        List<BodyPart> bodyPart2 = JakartaMessagePartConverter.convertBodyPart((OutgoingMessage) null);
        assertNotNull(bodyPart2);
        assertEquals(0, bodyPart2.size());

        List<BodyPart> bodyPart3 = JakartaMessagePartConverter.convertBodyPart(TestConverterUtils.createEmptyMessage());
        assertNotNull(bodyPart3);
        assertEquals(0, bodyPart3.size());
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link JakartaMessagePartConverter#convertBodyPart(ContentMessage)}</li>
     *      <li>{@link JakartaMessagePartConverter#convertBodyPart(OutgoingMessage)}</li>
     * </ul>
     */
    @Test
    public void convertBodyTest_1() {
        ContentMessage contentMessage = new ContentMessage("content os the message", TEXT_CONTENT_TYPE);
        BodyPart result1 = JakartaMessagePartConverter.convertBodyPart(contentMessage);
        MessageAsserts.assertContent(contentMessage, result1);

        DefaultOutgoingMessage outgoingMessage = TestConverterUtils.createSimpleMessage(
            "Subject",
            "content os the message"
        );
        List<BodyPart> result2 = JakartaMessagePartConverter.convertBodyPart(outgoingMessage);
        assertNotNull(result2);
        assertEquals(1, result2.size());
        MessageAsserts.assertMessageContent(outgoingMessage.getContents(), result2);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link JakartaMessagePartConverter#convertAttachmentPart(EmailAttachment)}</li>
     *     <li>{@link JakartaMessagePartConverter#convertAttachmentParts(OutgoingMessage)}</li>
     * </ul>
     */
    @Test
    public void convertAttachmentTest_0() {
        BodyPart bodyPart1 = JakartaMessagePartConverter.convertAttachmentPart(null);
        assertNull(bodyPart1);

        List<BodyPart> bodyPart2 = JakartaMessagePartConverter.convertAttachmentParts(null);
        assertNotNull(bodyPart2);
        assertEquals(0, bodyPart2.size());

        List<BodyPart> bodyPart3 = JakartaMessagePartConverter.convertBodyPart(TestConverterUtils.createEmptyMessage());
        assertNotNull(bodyPart3);
        assertEquals(0, bodyPart3.size());
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link JakartaMessagePartConverter#convertAttachmentPart(EmailAttachment)}</li>
     *     <li>{@link JakartaMessagePartConverter#convertAttachmentParts(OutgoingMessage)}</li>
     * </ul>
     */
    @Test
    public void convertAttachmentTest_1() {
        EmailAttachment attachment = AttachmentUtils.create("classpath:attachments/file.jpg");
        BodyPart result1 = JakartaMessagePartConverter.convertAttachmentPart(attachment);
        MessageAsserts.assertAttachment(attachment, result1);

        DefaultOutgoingMessage outgoingMessage = TestConverterUtils.createMessageWithAttachments(
            "Subject",
            "content os the message",
            Arrays.asList(
                "classpath:attachments/file.jpg",
                "classpath:attachments/file.txt",
                "classpath:attachments/file.html",
                "classpath:attachments/file.docx",
                "classpath:attachments/file.zip"
            )
        );
        List<BodyPart> result2 = JakartaMessagePartConverter.convertAttachmentParts(outgoingMessage);
        assertNotNull(result2);
        assertEquals(5, result2.size());
        MessageAsserts.assertMessageAttachments(outgoingMessage.getAttachments(), result2);
    }
}
