package ru.dlabs.library.email;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ru.dlabs.library.email.client.SendingStatus;
import ru.dlabs.library.email.client.sender.DEmailSender;
import ru.dlabs.library.email.exception.ValidationMessageException;
import ru.dlabs.library.email.properties.AuthenticationType;
import ru.dlabs.library.email.properties.SmtpProperties;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SMTPClientTests {

    private SmtpProperties smtpProperties;
    private String recipientEmail;

    @BeforeEach
    public void loadConfig() throws IOException {
        Properties properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream("smtp.properties"));
        this.recipientEmail = properties.getProperty("recipientEmail");
        this.smtpProperties = SmtpProperties.builder()
            .host(properties.getProperty("host"))
            .port(Integer.parseInt(properties.getProperty("port")))
            .email(properties.getProperty("email"))
            .password(properties.getProperty("password"))
            .name(properties.getProperty("name"))
            .authenticationType(AuthenticationType.valueOf(properties.getProperty("authenticationType")))
            .readTimeout(Integer.parseInt(properties.getProperty("readTimeout")))
            .connectionTimeout(Integer.parseInt(properties.getProperty("connectionTimeout")))
            .writeTimeout(Integer.parseInt(properties.getProperty("writeTimeout")))
            .build();
    }

    @Test
    public void sendTextMessageTest() {
        SendingStatus result = DEmailSender.of(this.smtpProperties)
            .sendText(this.recipientEmail, "Test subject", "Test message");
        assertEquals(result, SendingStatus.SUCCESS);
    }

    @Test
    public void validateMessageTest() {
        DEmailSender sender = DEmailSender.of(this.smtpProperties);
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
            () -> sender.sendText(List.of(), null, null)
        );

        assertInstanceOf(ValidationMessageException.class, exception);
        assertEquals(exception.getMessage(), "List recipients cannot be null or empty in the email message");
    }

}
