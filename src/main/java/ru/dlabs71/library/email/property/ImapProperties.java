package ru.dlabs71.library.email.property;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.dlabs71.library.email.type.EncryptionType;

/**
 * The properties for connecting to an email server by the IMAP protocol.
 *
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-08-30</div>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
public class ImapProperties extends CommonProperties {

    /**
     * Email address of a mailbox to witch to connect.
     */
    private String email;

    /**
     * Password of a mailbox to witch to connect.
     */
    private String password;

    /**
     * Controls whether the IMAP partial-fetch capability should be used. Defaults to true.
     *
     * <p>Corresponds the next jakarta mail properties: mail.imap.partialfetch
     */
    private boolean partialFetch = true;

    /**
     * Partial fetch size in bytes. Defaults to 16K.
     *
     * <p>Corresponds the next jakarta mail properties: mail.imap.fetchsize
     */
    private int fetchSize = 1048576;

    /**
     * Timeout value in milliseconds for cache of STATUS command response. Default is 1000 (1 second).
     * Zero disables cache.
     *
     * <p>Corresponds the next jakarta mail properties: mail.imap.statuscachetimeout
     */
    private int statusCacheTimeout = 1000;

    /**
     * Maximum size of a message to buffer in memory when appending to an IMAP folder.
     * If not set, or set to -1, there is no maximum and all messages are buffered. If set to 0,
     * no messages are buffered. If set to (e.g.) 8192, messages of 8K bytes or less are buffered,
     * larger messages are not buffered. Buffering saves cpu time at the expense of short term memory usage.
     * If you commonly append very large messages to IMAP mailboxes you might want to set this to a moderate
     * value (1M or less).
     *
     * <p>Corresponds the next jakarta mail properties: mail.imap.appendbuffersize
     */
    private int appendBufferSize = 1000;

    /**
     * Maximum number of available connections in the connection pool. Default is 10.
     *
     * <p>Corresponds the next jakarta mail properties: mail.imap.connectionpoolsize
     */
    private int connectionPoolSize = 20;

    /**
     * Timeout value in milliseconds for connection pool connections. Default is 45000 (45 seconds).
     *
     * <p>Corresponds the next jakarta mail properties: mail.imap.connectionpooltimeout
     */
    private int connectionPoolTimeout = 45000;

    /**
     * The constructor of this class.
     */
    public ImapProperties(
        String email,
        String password,
        boolean partialFetch,
        int fetchSize,
        int statusCacheTimeout,
        int appendBufferSize,
        int connectionPoolSize,
        int connectionPoolTimeout,
        String host,
        int port,
        EncryptionType encryptionType,
        int readTimeout,
        int connectionTimeout,
        int writeTimeout,
        boolean debug,
        Charset charset,
        Map<String, Object> extraProperties,
        int maxAttemptsOfRequest,
        int attemptDelayOfRequest
    ) {
        super(
            host,
            port,
            encryptionType,
            readTimeout,
            connectionTimeout,
            writeTimeout,
            debug,
            charset,
            extraProperties,
            maxAttemptsOfRequest,
            attemptDelayOfRequest
        );
        this.email = email;
        this.password = password;
        this.partialFetch = partialFetch;
        this.fetchSize = fetchSize;
        this.statusCacheTimeout = statusCacheTimeout;
        this.appendBufferSize = appendBufferSize;
        this.connectionPoolSize = connectionPoolSize;
        this.connectionPoolTimeout = connectionPoolTimeout;
    }

    /**
     * Returns builder for this class.
     */
    public static ImapProperties.ImapPropertiesBuilder builder() {
        return new ImapProperties.ImapPropertiesBuilder();
    }

    /**
     * Builder class for make {@link ImapProperties} class instance.
     */
    @Setter
    @ToString
    @NoArgsConstructor
    @Accessors(chain = true, fluent = true)
    public static class ImapPropertiesBuilder {

        private String email;
        private String password;
        private boolean partialFetch = true;
        private int fetchSize = 1048576;
        private int statusCacheTimeout = 1000;
        private int appendBufferSize = 1000;
        private int connectionPoolSize = 1;
        private int connectionPoolTimeout = 45000;
        private String host;
        private int port;
        private EncryptionType encryptionType = EncryptionType.NONE;
        private int readTimeout = 30000;
        private int connectionTimeout = 30000;
        private int writeTimeout = 30000;
        private boolean debug = false;
        private Charset charset = Charset.defaultCharset();
        private Map<String, Object> extraProperties = new HashMap<>();
        private int maxAttemptsOfRequest = 3;
        private int attemptDelayOfRequest = 0;

        /**
         * Builds and returns a new instance of {@link ImapProperties}.
         */
        public ImapProperties build() {
            return new ImapProperties(
                email,
                password,
                partialFetch,
                fetchSize,
                statusCacheTimeout,
                appendBufferSize,
                connectionPoolSize,
                connectionPoolTimeout,
                host,
                port,
                encryptionType,
                readTimeout,
                connectionTimeout,
                writeTimeout,
                debug,
                charset,
                extraProperties,
                maxAttemptsOfRequest,
                attemptDelayOfRequest
            );
        }
    }
}
