package ru.dlabs.library.email.util;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Properties;
import lombok.experimental.UtilityClass;
import org.eclipse.angus.mail.util.MailSSLSocketFactory;
import ru.dlabs.library.email.property.CommonProperties;
import ru.dlabs.library.email.type.EncryptionType;
import ru.dlabs.library.email.type.Protocol;

/**
 * Utility class for settings and workings with the email session connection
 *
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-08-30
 */
@UtilityClass
public class SessionUtils {

    public Properties createCommonProperties(CommonProperties properties, Protocol protocol)
        throws GeneralSecurityException {
        Properties props = new Properties();

        props.put(createProperty(protocol, "host"), properties.getHost());
        props.put(createProperty(protocol, "port"), properties.getPort());
        props.put(createProperty(protocol, "timeout"), properties.getReadTimeout());
        props.put(createProperty(protocol, "connectiontimeout"), properties.getConnectionTimeout());
        props.put("mail.mime.allowutf8", true);
        props.put("mail.mime.charset", StandardCharsets.UTF_8.name());
        props.put("mail.debug", properties.isDebug());

        if (EncryptionType.SSL.equals(properties.getEncryptionType())) {
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            props.put(createProperty(protocol, "ssl.enable"), true);
            props.put(createProperty(protocol, "socketFactory.port"), properties.getPort());
            props.put(createProperty(protocol, "ssl.socketFactory"), sf);
            props.put(createProperty(protocol, "ssl.checkserveridentity"), true);
        } else if (EncryptionType.TLS.equals(properties.getEncryptionType())) {
            props.put(createProperty(protocol, "starttls.enable"), true);
            props.put(createProperty(protocol, "starttls.required"), true);
        }
        return props;
    }

    public String createProperty(Protocol protocol, String property) {
        return "mail." + protocol.getPropName() + "." + property;
    }

}
