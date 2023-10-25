package ru.dlabs.library.email.tests.client.sender;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ru.dlabs.library.email.DEmailSender;
import ru.dlabs.library.email.support.AbstractTestsClass;
import ru.dlabs.library.email.type.SendingStatus;
import ru.dlabs.library.email.property.SmtpProperties;
import ru.dlabs.library.email.support.PropUtils;
import ru.dlabs.library.email.util.AttachmentUtils;

@Order(31)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SMTPAttachmentTests extends AbstractTestsClass {

    private SmtpProperties sslSmtpProperties;
    private String recipientEmail;

    @BeforeEach
    public void loadConfig() {
        Properties props = PropUtils.loadPropertiesFromFile(SenderTestUtils.PROP_FILE_NAME);
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
