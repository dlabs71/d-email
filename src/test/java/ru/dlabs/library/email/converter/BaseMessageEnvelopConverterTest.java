package ru.dlabs.library.email.converter;

import jakarta.mail.Message;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import ru.dlabs.library.email.dto.message.common.BaseMessage;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-12</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
public class BaseMessageEnvelopConverterTest {

    /**
     * The test for:
     * <ul>
     *     <li>{@link BaseMessageConverter#convertEnvelopData(Message)}</li>
     * </ul>
     */
    @Test
    public void convertEnvelopDataTest_1() {
        String subject = "Captain Flint's Map";

        MimeMessage message = TestConverterUtils.createSimpleMessage(subject, null);
        BaseMessage baseMessage = BaseMessageConverter.convertEnvelopData(message);
        MessageAsserts.assertMessagesAsEnvelop(baseMessage, message, subject);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link BaseMessageConverter#convertEnvelopData(Message)}</li>
     * </ul>
     */
    @Test
    public void convertEnvelopDataTest_2() {
        MimeMessage message = TestConverterUtils.createEmptyMessage();
        BaseMessage baseMessage = BaseMessageConverter.convertEnvelopData(message);
        MessageAsserts.assertMessagesAsEnvelop(baseMessage, message, null);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link BaseMessageConverter#convertEnvelopData(Message)}</li>
     * </ul>
     */
    @Test
    public void convertEnvelopDataTest_3() {
        String subject = "Captain Flint's Map";

        MimeMessage message = TestConverterUtils.createMessageWithoutSenders(subject, null);
        BaseMessage baseMessage = BaseMessageConverter.convertEnvelopData(message);
        MessageAsserts.assertMessagesAsEnvelop(baseMessage, message, subject);
    }

}
