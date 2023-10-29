package ru.dlabs.library.email.tests.client.sender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ru.dlabs.library.email.client.sender.SMTPDClient;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;
import ru.dlabs.library.email.dto.message.outgoing.DefaultOutgoingMessage;
import ru.dlabs.library.email.exception.ValidationMessageException;
import ru.dlabs.library.email.property.SmtpProperties;
import ru.dlabs.library.email.support.AbstractTestsClass;
import ru.dlabs.library.email.support.PropUtils;
import ru.dlabs.library.email.tests.client.sender.utils.SenderTestUtils;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-29</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SMTPDClientTest extends AbstractTestsClass {

    private SmtpProperties sslSmtpProperties;
    private String recipientEmail;

    @BeforeEach
    public void loadConfig() {
        Properties props = PropUtils.loadPropertiesFromFile(SenderTestUtils.PROP_FILE_NAME);
        this.recipientEmail = props.getProperty("recipientEmail");

        this.sslSmtpProperties = SenderTestUtils.loadSslProperties();
    }

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
