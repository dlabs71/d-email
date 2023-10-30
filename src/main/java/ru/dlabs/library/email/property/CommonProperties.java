package ru.dlabs.library.email.property;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.dlabs.library.email.type.EncryptionType;

/**
 * The common class of properties for connection.
 *
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-08-30</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class CommonProperties {

    /**
     * A hostname of an email server.
     *
     * <p>Corresponds the next jakarta mail properties: mail.smtp.host, mail.imap.host, etc.
     */
    private String host;

    /**
     * A port connection to an email server.
     *
     * <p>Corresponds the next jakarta mail properties: mail.smtp.port, mail.imap.port, etc.
     */
    private Integer port;

    /**
     * An encryption type of connection to email server.
     *
     * <p>Settings TLS, SSL and PLAIN encodings of the connection.
     */
    private EncryptionType encryptionType = EncryptionType.NONE;

    /**
     * Socket read timeout value in milliseconds. This timeout is implemented by java.net.Socket.
     * Default is infinite timeout.
     *
     * <p>Corresponds the next jakarta mail properties: mail.smtp.timeout
     */
    private Integer readTimeout = 30000;

    /**
     * Socket connection timeout value in milliseconds. This timeout is implemented by java.net.Socket.
     * Default is infinite timeout.
     *
     * <p>Corresponds the next jakarta mail properties: mail.smtp.connectiontimeout
     */
    private Integer connectionTimeout = 30000;

    /**
     * Socket write timeout value in milliseconds. This timeout is implemented by using a
     * java.util.concurrent.ScheduledExecutorService per connection that schedules a thread to close the socket
     * if the timeout expires. Thus, the overhead of using this timeout is one thread per connection.
     * Default is infinite timeout.
     *
     * <p>Corresponds the next jakarta mail properties: mail.smtp.writetimeout
     */
    private Integer writeTimeout = 30000;

    /**
     * Enables debug mode.
     *
     * <p>Corresponds the next jakarta mail properties: mail.debug
     */
    private boolean debug = false;

    /**
     * Default value for content and attachment encoding.
     */
    private Charset charset = Charset.defaultCharset();

    /**
     * You can use this field to set any other properties for setting connection.
     * See the
     * <a href="https://jakarta.ee/specifications/mail/1.6/apidocs/?com/sun/mail/">Jakarta Mail Documentation</a>
     * to look up needed properties.
     */
    private Map<String, Object> extraProperties;

    public Map<String, Object> getExtraProperties() {
        if (extraProperties == null) {
            return Collections.emptyMap();
        }
        return extraProperties;
    }
}
