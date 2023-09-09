package ru.dlabs.library.email.client.sender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ru.dlabs.library.email.client.SendingStatus;
import ru.dlabs.library.email.exception.ValidationMessageException;
import ru.dlabs.library.email.property.EncryptionType;
import ru.dlabs.library.email.property.SmtpProperties;

@Order(31)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SMTPClientTests {

    private SmtpProperties sslSmtpProperties;
    private SmtpProperties tlsSmtpProperties;
    private SmtpProperties simpleSmtpProperties;
    private String recipientEmail;

    @BeforeEach
    public void loadConfig() throws IOException {
        Properties properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream("smtp.properties"));
        this.recipientEmail = properties.getProperty("recipientEmail");

        SmtpProperties.SmtpPropertiesBuilder builder = SmtpProperties.builder()
            .host(properties.getProperty("host"))
            .email(properties.getProperty("email"))
            .password(properties.getProperty("password"))
            .name(properties.getProperty("name"))
            .readTimeout(Integer.parseInt(properties.getProperty("readTimeout")))
            .connectionTimeout(Integer.parseInt(properties.getProperty("connectionTimeout")))
            .writeTimeout(Integer.parseInt(properties.getProperty("writeTimeout")))
            .debug("true".equals(properties.getProperty("debug", "false")));

        this.sslSmtpProperties = builder
            .encryptionType(EncryptionType.SSL)
            .port(Integer.parseInt(properties.getProperty("encryptionClient.port")))
            .build();

        this.tlsSmtpProperties = builder
            .encryptionType(EncryptionType.TLS)
            .port(Integer.parseInt(properties.getProperty("port")))
            .build();

        this.simpleSmtpProperties = builder
            .encryptionType(EncryptionType.NONE)
            .port(Integer.parseInt(properties.getProperty("port")))
            .build();
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
