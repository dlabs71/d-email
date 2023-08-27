package ru.dlabs.library.email;

import java.io.IOException;
import java.util.Properties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ru.dlabs.library.email.client.sender.DEmailSender;
import ru.dlabs.library.email.client.SendingStatus;
import ru.dlabs.library.email.properties.AuthenticationType;
import ru.dlabs.library.email.properties.SmtpProperties;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SMTPClientTests {

    private SmtpProperties smtpProperties;

    @BeforeEach
    public void loadConfig() throws IOException {
        Properties properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream("smtp.properties"));
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
    public void sendTextMessageTest1() {
        SendingStatus result = DEmailSender.of(this.smtpProperties)
            .sendText("danila.a.ivanov@gmail.com", "Test subject", "Test message");
        Assertions.assertEquals(result, SendingStatus.SUCCESS);
    }

    @Test
    public void sendTextMessageTest2() {
        SendingStatus result = DEmailSender.of(this.smtpProperties)
            .sendText("danila.a.ivanov@gmail.com", "Test subject", null);
        Assertions.assertEquals(result, SendingStatus.SUCCESS);
    }

}
