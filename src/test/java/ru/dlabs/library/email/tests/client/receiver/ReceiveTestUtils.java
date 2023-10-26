package ru.dlabs.library.email.tests.client.receiver;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import ru.dlabs.library.email.DEmailReceiver;
import ru.dlabs.library.email.property.ImapProperties;
import ru.dlabs.library.email.support.PropUtils;
import ru.dlabs.library.email.type.EncryptionType;

/**
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-09-09
 */
@UtilityClass
public class ReceiveTestUtils {

    public final static String PROP_FILE_NAME = "imap.properties";
    public final static String CREDENTIAL_ID_1 = "credentialId_1";
    public final static String CREDENTIAL_ID_2 = "credentialId_2";

    public DEmailReceiver createReceiver() {
        ImapProperties properties = loadSslProperties();
        return DEmailReceiver.of(properties);
    }

    public DEmailReceiver createReceiver1() {
        ImapProperties properties = loadSslProperties(CREDENTIAL_ID_1);
        return DEmailReceiver.of(properties);
    }

    public DEmailReceiver createReceiver2() {
        ImapProperties properties = loadSslProperties(CREDENTIAL_ID_2);
        return DEmailReceiver.of(properties);
    }

    public ImapProperties.ImapPropertiesBuilder loadCommonProperties(Properties properties, String credentialId)
        throws IOException {
        HashMap<String, ImapProperties.Credentials> credentialsMap = new HashMap<>();
        credentialsMap.put(
            CREDENTIAL_ID_1,
            new ImapProperties.Credentials(
                properties.getProperty("email1"),
                properties.getProperty("password1")
            )
        );
        credentialsMap.put(
            CREDENTIAL_ID_2,
            new ImapProperties.Credentials(
                properties.getProperty("email2"),
                properties.getProperty("password2")
            )
        );
        return ImapProperties.builder()
            .host(properties.getProperty("host"))
            .credentials(credentialsMap.get(credentialId))
            .debug("true".equals(properties.getProperty("debug", "false")));
    }

    @SneakyThrows
    public ImapProperties[] loadProperties() {
        return loadProperties(CREDENTIAL_ID_1);
    }

    @SneakyThrows
    public ImapProperties[] loadProperties(String credentialId) {
        ImapProperties[] result = new ImapProperties[3];

        Properties properties = PropUtils.loadPropertiesFromFile(PROP_FILE_NAME);
        ImapProperties.ImapPropertiesBuilder builder = loadCommonProperties(properties, credentialId);

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

    @SneakyThrows
    public ImapProperties loadSslProperties() {
        return loadSslProperties(CREDENTIAL_ID_1);
    }

    @SneakyThrows
    public ImapProperties loadSslProperties(String credentialId) {
        Properties properties = PropUtils.loadPropertiesFromFile(PROP_FILE_NAME);
        ImapProperties.ImapPropertiesBuilder builder = loadCommonProperties(properties, credentialId);
        return builder
            .encryptionType(EncryptionType.SSL)
            .port(Integer.parseInt(properties.getProperty("encryptionClient.port")))
            .build();
    }

    public String getDefaultEmail(ImapProperties properties) {
        return properties.getCredentials().getEmail();
    }
}
