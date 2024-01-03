package ru.dlabs71.library.email.tests.converter.outgoing;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import ru.dlabs71.library.email.converter.outgoing.JakartaMessageConverter;
import ru.dlabs71.library.email.dto.message.outgoing.DefaultOutgoingMessage;
import ru.dlabs71.library.email.dto.message.outgoing.OutgoingMessage;
import ru.dlabs71.library.email.tests.converter.outgoing.utils.MessageAsserts;
import ru.dlabs71.library.email.tests.converter.outgoing.utils.TestConverterUtils;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-26</div>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Order(311)
public class JakartaMessageEnvelopConverterTest {

    /**
     * The test for:
     * <ul>
     *     <li>{@link JakartaMessageConverter#createEnvelop(OutgoingMessage, Session, String, String)} </li>
     * </ul>
     */
    @Test
    public void convertTest_0() {
        MimeMessage message = JakartaMessageConverter.createEnvelop(null, null, null, null);
        assertNull(message);

        assertThrows(IllegalArgumentException.class, () -> {
            JakartaMessageConverter.createEnvelop(new DefaultOutgoingMessage(null, null, null, null), null, null, null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            JakartaMessageConverter.createEnvelop(
                new DefaultOutgoingMessage(null, null, null, null),
                null,
                "email@email.com",
                null
            );
        });

        assertThrows(IllegalArgumentException.class, () -> {
            JakartaMessageConverter.createEnvelop(
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
     *     <li>{@link JakartaMessageConverter#createEnvelop(OutgoingMessage, Session, String, String)} </li>
     * </ul>
     */
    @Test
    public void convertTest_1() {
        String fromEmail = "email_from@email.com";
        String fromName = "Sender name";

        DefaultOutgoingMessage outgoingMessage = TestConverterUtils.createEmptyMessage();
        MimeMessage message = JakartaMessageConverter.createEnvelop(
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
     *     <li>{@link JakartaMessageConverter#createEnvelop(OutgoingMessage, Session, String, String)} </li>
     * </ul>
     */
    @Test
    public void convertTest_2() {
        String subject = "The subject of a message";
        String content = "The content of a message";

        String fromEmail = "email_from@email.com";
        String fromName = "Sender name";

        DefaultOutgoingMessage outgoingMessage = TestConverterUtils.createMessageWithoutRecipients(subject, content);
        MimeMessage message = JakartaMessageConverter.createEnvelop(
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
     *     <li>{@link JakartaMessageConverter#createEnvelop(OutgoingMessage, Session, String, String)} </li>
     * </ul>
     */
    @Test
    public void convertTest_3() {
        String subject = "The subject of a message";
        String content = "The content of a message";

        String fromEmail = "email_from@email.com";
        String fromName = "Sender name";

        DefaultOutgoingMessage outgoingMessage = TestConverterUtils.createSimpleMessage(subject, content);
        MimeMessage message = JakartaMessageConverter.createEnvelop(
            outgoingMessage,
            Session.getDefaultInstance(new Properties()),
            fromEmail,
            fromName
        );

        MessageAsserts.assertEnvelop(outgoingMessage, fromEmail, fromName, message);
        MessageAsserts.assertEmptyContent(message);
    }
}
