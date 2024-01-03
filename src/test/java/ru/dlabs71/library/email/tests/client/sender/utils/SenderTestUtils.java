package ru.dlabs71.library.email.tests.client.sender.utils;

import java.io.IOException;
import java.util.Properties;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import ru.dlabs71.library.email.DEmailSender;
import ru.dlabs71.library.email.property.SmtpProperties;
import ru.dlabs71.library.email.support.PropUtils;
import ru.dlabs71.library.email.type.EncryptionType;

/**
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-09-11
 */
@UtilityClass
public class SenderTestUtils {

    public final static String PROP_FILE_NAME = "smtp.properties";

    public DEmailSender createSender() {
        SmtpProperties smtpProperties = loadSslProperties();
        return DEmailSender.of(smtpProperties);
    }

    public SmtpProperties.SmtpPropertiesBuilder loadCommonProperties(Properties properties) throws IOException {
        return SmtpProperties.builder()
            .host(properties.getProperty("host"))
            .email(properties.getProperty("email"))
            .password(properties.getProperty("password"))
            .name(properties.getProperty("name"))
            .readTimeout(Integer.parseInt(properties.getProperty("readTimeout")))
            .connectionTimeout(Integer.parseInt(properties.getProperty("connectionTimeout")))
            .writeTimeout(Integer.parseInt(properties.getProperty("writeTimeout")))
            .debug("true".equals(properties.getProperty("debug", "false")));
    }

    @SneakyThrows
    public SmtpProperties[] loadProperties() {
        SmtpProperties[] result = new SmtpProperties[3];

        Properties properties = PropUtils.loadPropertiesFromFile(PROP_FILE_NAME);
        SmtpProperties.SmtpPropertiesBuilder builder = loadCommonProperties(properties);

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
    public SmtpProperties loadSslProperties() {
        Properties properties = PropUtils.loadPropertiesFromFile(PROP_FILE_NAME);
        SmtpProperties.SmtpPropertiesBuilder builder = loadCommonProperties(properties);

        return builder
            .encryptionType(EncryptionType.SSL)
            .port(Integer.parseInt(properties.getProperty("encryptionClient.port")))
            .build();
    }
}
