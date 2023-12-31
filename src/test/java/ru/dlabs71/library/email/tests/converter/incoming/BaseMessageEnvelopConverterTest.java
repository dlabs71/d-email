package ru.dlabs71.library.email.tests.converter.incoming;

import jakarta.mail.Message;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import ru.dlabs71.library.email.converter.incoming.BaseMessageConverter;
import ru.dlabs71.library.email.tests.converter.incoming.utils.MessageAsserts;
import ru.dlabs71.library.email.tests.converter.incoming.utils.TestConverterUtils;
import ru.dlabs71.library.email.dto.message.common.BaseMessage;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-12</div>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Order(321)
public class BaseMessageEnvelopConverterTest {

    /**
     * The test for:
     * <ul>
     *     <li>{@link BaseMessageConverter#convertEnvelopData(Message)}</li>
     * </ul>
     */
    @Test
    public void convertEnvelopDataTest_0() {
        BaseMessage baseMessage = BaseMessageConverter.convertEnvelopData(null);
        Assertions.assertNull(baseMessage);
    }

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
        MessageAsserts.assertMessageEnvelop(baseMessage, message, subject);
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
        MessageAsserts.assertMessageEnvelop(baseMessage, message, null);
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
        MessageAsserts.assertMessageEnvelop(baseMessage, message, subject);
    }
}
