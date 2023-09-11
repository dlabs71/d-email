package ru.dlabs.library.email.client.sender;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ru.dlabs.library.email.client.SendingStatus;
import ru.dlabs.library.email.property.SmtpProperties;
import ru.dlabs.library.email.util.AttachmentUtils;

@Order(31)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SMTPAttachmentTests {

    private SmtpProperties sslSmtpProperties;
    private String recipientEmail;

    @BeforeEach
    public void loadConfig() {
        Properties props = SenderTestUtils.loadPropertiesFromFile();
        this.recipientEmail = props.getProperty("recipientEmail");
        this.sslSmtpProperties = SenderTestUtils.loadSslProperties();
    }

    @Test
    public void sendWithAttachments() {
        SendingStatus result = DEmailSender.of(this.sslSmtpProperties)
            .sendText(
                this.recipientEmail,
                "Test subject",
                "Test message",
                AttachmentUtils.create("classpath:attachments/file.txt"),
                AttachmentUtils.create("classpath:attachments/file.jpg")
            );
        assertEquals(result, SendingStatus.SUCCESS);
    }
}
