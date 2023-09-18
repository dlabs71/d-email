package ru.dlabs.library.email.client.receiver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import ru.dlabs.library.email.DEmailSender;
import ru.dlabs.library.email.type.EncryptionType;
import ru.dlabs.library.email.property.ImapProperties;
import ru.dlabs.library.email.property.SmtpProperties;

/**
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-09-09
 */
@UtilityClass
public class ReceiveTestUtils {

    public final static String CREDENTIAL_ID = "credentialId";

    public DEmailSender createSender() throws IOException {
        Properties properties = new Properties();
        properties.load(ReceiveTestUtils.class.getClassLoader().getResourceAsStream("smtp.properties"));

        SmtpProperties.SmtpPropertiesBuilder builder = SmtpProperties.builder()
            .host(properties.getProperty("host"))
            .email(properties.getProperty("email"))
            .password(properties.getProperty("password"))
            .name(properties.getProperty("name"))
            .readTimeout(Integer.parseInt(properties.getProperty("readTimeout")))
            .connectionTimeout(Integer.parseInt(properties.getProperty("connectionTimeout")))
            .writeTimeout(Integer.parseInt(properties.getProperty("writeTimeout")))
            .debug("true".equals(properties.getProperty("debug", "false")));

        SmtpProperties smtpProperties = builder
            .encryptionType(EncryptionType.SSL)
            .port(Integer.parseInt(properties.getProperty("encryptionClient.port")))
            .build();
        return DEmailSender.of(smtpProperties);
    }

    @SneakyThrows
    public ImapProperties[] loadProperties() {
        ImapProperties[] result = new ImapProperties[3];

        Properties properties = new Properties();
        properties.load(ReceiveTestUtils.class.getClassLoader().getResourceAsStream("imap.properties"));

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

        result[0] = builder
            .encryptionType(EncryptionType.SSL)
            .port(Integer.parseInt(properties.getProperty("encryptionClient.port")))
            .build();

        result[1] = builder
            .encryptionType(EncryptionType.TLS)
            .port(Integer.parseInt(properties.getProperty("port")))
            .build();

        result[2] = builder
            .encryptionType(EncryptionType.NONE)
            .port(Integer.parseInt(properties.getProperty("port")))
            .build();

        return result;
    }

    public String getDefaultEmail(ImapProperties properties) {
        return properties.getCredentials().get(ReceiveTestUtils.CREDENTIAL_ID).getEmail();
    }
}
