package ru.dlabs.library.email.tests.client.sender;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Properties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ru.dlabs.library.email.DEmailSender;
import ru.dlabs.library.email.dto.message.outgoing.OutgoingMessage;
import ru.dlabs.library.email.property.SmtpProperties;
import ru.dlabs.library.email.support.AbstractTestsClass;
import ru.dlabs.library.email.support.PropUtils;
import ru.dlabs.library.email.tests.client.sender.utils.SenderTestUtils;
import ru.dlabs.library.email.type.SendingStatus;

@Order(412)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SMTPClientConnectionTests extends AbstractTestsClass {

    private SmtpProperties sslSmtpProperties;
    private SmtpProperties tlsSmtpProperties;
    private SmtpProperties simpleSmtpProperties;
    private String recipientEmail;

    @BeforeAll
    public void loadConfig() {
        Properties props = PropUtils.loadPropertiesFromFile(SenderTestUtils.PROP_FILE_NAME);
        this.recipientEmail = props.getProperty("recipientEmail");

        SmtpProperties[] properties = SenderTestUtils.loadProperties();
        this.sslSmtpProperties = properties[0];
        this.tlsSmtpProperties = properties[1];
        this.simpleSmtpProperties = properties[2];
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link DEmailSender#send(OutgoingMessage)}</li>
     * </ul>
     * <p>
     * Using of the SSL connection
     */
    @Test
    public void sendSslTextMessageTest() {
        DEmailSender sender = DEmailSender.of(this.sslSmtpProperties);
        assertEquals(sender.sender().getEmail(), this.sslSmtpProperties.getEmail());
        assertEquals(sender.sender().getName(), this.sslSmtpProperties.getName());

        SendingStatus result = sender.sendText(this.recipientEmail, "Test subject", "Test message");
        assertEquals(SendingStatus.SUCCESS, result);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link DEmailSender#send(OutgoingMessage)}</li>
     * </ul>
     * <p>
     * Using of the TSL connection
     */
    @Test
    public void sendTlsTextMessageTest() {
        DEmailSender sender = DEmailSender.of(this.tlsSmtpProperties);
        assertEquals(sender.sender().getEmail(), this.tlsSmtpProperties.getEmail());
        assertEquals(sender.sender().getName(), this.tlsSmtpProperties.getName());

        SendingStatus result = sender.sendText(this.recipientEmail, "Test subject", "Test message");
        assertEquals(SendingStatus.SUCCESS, result);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link DEmailSender#send(OutgoingMessage)}</li>
     * </ul>
     * <p>
     * Using of the simple type connection (NO SSL and NO TLS)
     */
    @Test
    public void sendSimpleTextMessageTest() {
        DEmailSender sender = DEmailSender.of(this.simpleSmtpProperties);
        assertEquals(sender.sender().getEmail(), this.simpleSmtpProperties.getEmail());
        assertEquals(sender.sender().getName(), this.simpleSmtpProperties.getName());

        SendingStatus result = sender.sendText(this.recipientEmail, "Test subject", "Test message");
        assertEquals(result, SendingStatus.SUCCESS);
    }
}
