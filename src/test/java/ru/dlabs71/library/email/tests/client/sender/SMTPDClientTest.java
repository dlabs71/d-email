package ru.dlabs71.library.email.tests.client.sender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ru.dlabs71.library.email.client.sender.SMTPDClient;
import ru.dlabs71.library.email.dto.message.common.EmailParticipant;
import ru.dlabs71.library.email.dto.message.outgoing.DefaultOutgoingMessage;
import ru.dlabs71.library.email.dto.message.outgoing.OutgoingMessage;
import ru.dlabs71.library.email.exception.ValidationMessageException;
import ru.dlabs71.library.email.property.SmtpProperties;
import ru.dlabs71.library.email.support.AbstractTestsClass;
import ru.dlabs71.library.email.support.PropUtils;
import ru.dlabs71.library.email.tests.client.sender.utils.SenderTestUtils;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-29</div>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Order(411)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SMTPDClientTest extends AbstractTestsClass {

    private SmtpProperties sslSmtpProperties;
    private String recipientEmail;

    @BeforeAll
    public void loadConfig() {
        Properties props = PropUtils.loadPropertiesFromFile(SenderTestUtils.PROP_FILE_NAME);
        this.recipientEmail = props.getProperty("recipientEmail");
        this.sslSmtpProperties = SenderTestUtils.loadSslProperties();
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link SMTPDClient#SMTPDClient(SmtpProperties)}</li>
     * </ul>
     */
    @Test
    public void clientTest() {
        assertThrows(IllegalArgumentException.class, () -> new SMTPDClient(null));

        SMTPDClient smtpdClient = new SMTPDClient(sslSmtpProperties);
        assertNotNull(smtpdClient);
        assertNotNull(smtpdClient.getPrincipal());
        assertNotNull(smtpdClient.getProtocolName());
        assertEquals(smtpdClient.getPrincipal().getEmail(), sslSmtpProperties.getEmail());
        assertEquals(smtpdClient.getPrincipal().getName(), sslSmtpProperties.getName());
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link SMTPDClient#send(OutgoingMessage)}</li>
     * </ul>
     * <p>
     * Validate message
     */
    @Test
    public void validateMessageTest() {
        SMTPDClient sender = new SMTPDClient(sslSmtpProperties);
        Set<EmailParticipant> recipients = new HashSet<>();
        recipients.add(new EmailParticipant(this.recipientEmail));

        Exception exception = assertThrows(
            RuntimeException.class,
            () -> sender.send(new DefaultOutgoingMessage("Test subject", null, recipients, null))
        );
        assertInstanceOf(ValidationMessageException.class, exception);
        assertEquals(exception.getMessage(), "Content cannot be null in the email message");

        exception = assertThrows(
            RuntimeException.class,
            () -> sender.send(new DefaultOutgoingMessage(null, null, recipients, null))
        );

        assertInstanceOf(ValidationMessageException.class, exception);
        assertEquals(exception.getMessage(), "Subject cannot be null in the email message");

        exception = assertThrows(
            RuntimeException.class,
            () -> sender.send(new DefaultOutgoingMessage("Test subject", "Content", new HashSet<>(), null))
        );

        assertInstanceOf(ValidationMessageException.class, exception);
        assertEquals("List recipients cannot be null or empty in the email message", exception.getMessage());
    }
}
