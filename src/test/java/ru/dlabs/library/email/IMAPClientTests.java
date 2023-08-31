package ru.dlabs.library.email;

import jakarta.mail.MessagingException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ru.dlabs.library.email.client.receiver.IMAPDClient;
import ru.dlabs.library.email.message.TextMessage;
import ru.dlabs.library.email.properties.EncryptionType;
import ru.dlabs.library.email.properties.ImapProperties;

/**
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-08-31
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class IMAPClientTests {

    private final static String CREDENTIAL_ID = "credentialId";

    private ImapProperties sslImapProperties;
    private ImapProperties tlsImapProperties;
    private ImapProperties simpleImapProperties;

    @BeforeEach
    public void loadConfig() throws IOException {
        Properties properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream("imap.properties"));

        ImapProperties.ImapPropertiesBuilder builder = ImapProperties.builder()
            .host(properties.getProperty("host"))
            .credentials(
                Map.of(
                    CREDENTIAL_ID,
                    new ImapProperties.Credentials(
                        properties.getProperty("email"),
                        properties.getProperty("password")
                    )
                )
            )
            .debug("true".equals(properties.getProperty("debug", "false")));

        this.sslImapProperties = builder
            .encryptionType(EncryptionType.SSL)
            .port(Integer.parseInt(properties.getProperty("encryptionClient.port")))
            .build();

        this.tlsImapProperties = builder
            .encryptionType(EncryptionType.TLS)
            .port(Integer.parseInt(properties.getProperty("port")))
            .build();

        this.simpleImapProperties = builder
            .encryptionType(EncryptionType.NONE)
            .port(Integer.parseInt(properties.getProperty("port")))
            .build();
    }

    @Test
    public void checkEmailTest() throws GeneralSecurityException, MessagingException {
        IMAPDClient client = new IMAPDClient(this.sslImapProperties);
        client.setStore(CREDENTIAL_ID);
        List<TextMessage> messages = client.checkEmailMessages();
        System.out.println(messages);
    }
}
