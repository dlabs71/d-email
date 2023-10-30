package ru.dlabs.library.email.property;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Properties;
import org.eclipse.angus.mail.util.MailSSLSocketFactory;
import ru.dlabs.library.email.type.EncryptionType;
import ru.dlabs.library.email.type.Protocol;

/**
 * Utility class for settings and workings with the email session connection.
 *
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-08-30</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
public final class SessionPropertyCollector {

    /**
     * Returns a Properties object for set up an email connection configuration.
     *
     * @param properties properties. {@link ImapProperties}, {@link SmtpProperties}
     * @param protocol   a using protocol
     *
     * @return object {@link Properties}
     *
     * @throws GeneralSecurityException if a {@link MailSSLSocketFactory} will is broken
     */
    public static Properties createCommonProperties(CommonProperties properties, Protocol protocol)
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
        props.putAll(properties.getExtraProperties());
        return props;
    }

    /**
     * Utility method for creating property names based on protocol and property postfix.
     *
     * @param protocol a protocol IMAP, SMTP, etc.
     * @param property a property postfix
     *
     * @return a property name
     */
    public static String createProperty(Protocol protocol, String property) {
        return "mail." + protocol.getPropName() + "." + property;
    }

}
