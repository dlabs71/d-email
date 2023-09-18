package ru.dlabs.library.email.client.sender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ru.dlabs.library.email.DEmailSender;
import ru.dlabs.library.email.client.SendingStatus;
import ru.dlabs.library.email.exception.ValidationMessageException;
import ru.dlabs.library.email.property.SmtpProperties;

@Order(31)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SMTPClientTests {

    private SmtpProperties sslSmtpProperties;
    private SmtpProperties tlsSmtpProperties;
    private SmtpProperties simpleSmtpProperties;
    private String recipientEmail;

    @BeforeEach
    public void loadConfig() {
        Properties props = SenderTestUtils.loadPropertiesFromFile();
        this.recipientEmail = props.getProperty("recipientEmail");

        SmtpProperties[] properties = SenderTestUtils.loadProperties();
        this.sslSmtpProperties = properties[0];
        this.tlsSmtpProperties = properties[1];
        this.simpleSmtpProperties = properties[2];
    }

    @Test
    public void sendSslTextMessageTest() {
        SendingStatus result = DEmailSender.of(this.sslSmtpProperties)
            .sendText(this.recipientEmail, "Test subject", "Test message");
        assertEquals(result, SendingStatus.SUCCESS);
    }

    @Test
    public void sendTlsTextMessageTest() {
        SendingStatus result = DEmailSender.of(this.tlsSmtpProperties)
            .sendText(this.recipientEmail, "Test subject", "Test message");
        assertEquals(result, SendingStatus.SUCCESS);
    }

    @Test
    public void sendSimpleTextMessageTest() {
        SendingStatus result = DEmailSender.of(this.simpleSmtpProperties)
            .sendText(this.recipientEmail, "Test subject", "Test message");
        assertEquals(result, SendingStatus.SUCCESS);
    }

    @Test
    public void validateMessageTest() {
        DEmailSender sender = DEmailSender.of(this.sslSmtpProperties);
        Exception exception = assertThrows(
            RuntimeException.class,
            () -> sender.sendText(this.recipientEmail, "Test subject", null)
        );
        assertInstanceOf(ValidationMessageException.class, exception);
        assertEquals(exception.getMessage(), "Content cannot be null in the email message");

        exception = assertThrows(
            RuntimeException.class,
            () -> sender.sendText(this.recipientEmail, null, null)
        );

        assertInstanceOf(ValidationMessageException.class, exception);
        assertEquals(exception.getMessage(), "Subject cannot be null in the email message");

        exception = assertThrows(
            RuntimeException.class,
            () -> sender.sendText(new ArrayList<>(), null, null)
        );

        assertInstanceOf(ValidationMessageException.class, exception);
        assertEquals(exception.getMessage(), "List recipients cannot be null or empty in the email message");
    }

}
