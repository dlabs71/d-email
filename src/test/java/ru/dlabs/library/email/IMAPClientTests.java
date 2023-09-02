package ru.dlabs.library.email;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ru.dlabs.library.email.client.receiver.DEmailReceiver;
import ru.dlabs.library.email.message.MessageView;
import ru.dlabs.library.email.message.PageResponse;
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

        HashMap<String, ImapProperties.Credentials> credentialsMap = new HashMap<>();
        credentialsMap.put(
            CREDENTIAL_ID,
            new ImapProperties.Credentials(
                properties.getProperty("email"),
                properties.getProperty("password")
            )
        );
        ImapProperties.ImapPropertiesBuilder builder = ImapProperties.builder()
            .host(properties.getProperty("host"))
            .credentials(credentialsMap)
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
    public void checkSimpleEmailTest() {
        PageResponse<MessageView> response = DEmailReceiver.of(this.simpleImapProperties).nextCheckEmail();
    }

    @Test
    public void checkSSLEmailTest() {
        PageResponse<MessageView> response = DEmailReceiver.of(this.sslImapProperties).nextCheckEmail();
    }

    @Test
    public void checkTLSEmailTest() {
        PageResponse<MessageView> response = DEmailReceiver.of(this.tlsImapProperties).nextCheckEmail();
    }
}
