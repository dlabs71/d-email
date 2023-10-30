package ru.dlabs.library.email.property;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.dlabs.library.email.type.EncryptionType;

/**
 * The properties for connecting to an email server by the IMAP protocol
 *
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-08-30
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
    private Integer fetchSize = 1048576;

    /**
     * Timeout value in milliseconds for cache of STATUS command response. Default is 1000 (1 second).
     * Zero disables cache.
     *
     * <p>Corresponds the next jakarta mail properties: mail.imap.statuscachetimeout
     */
    private Integer statusCacheTimeout = 1000;

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
    private Integer appendBufferSize = 1000;

    /**
     * Maximum number of available connections in the connection pool. Default is 10.
     *
     * <p>Corresponds the next jakarta mail properties: mail.imap.connectionpoolsize
     */
    private Integer connectionPoolSize = 10;

    /**
     * Timeout value in milliseconds for connection pool connections. Default is 45000 (45 seconds).
     *
     * <p>Corresponds the next jakarta mail properties: mail.imap.connectionpooltimeout
     */
    private Integer connectionPoolTimeout = 45000;

    public ImapProperties(
        String email,
        String password,
        boolean partialFetch,
        Integer fetchSize,
        Integer statusCacheTimeout,
        Integer appendBufferSize,
        Integer connectionPoolSize,
        Integer connectionPoolTimeout,
        String host,
        Integer port,
        EncryptionType encryptionType,
        Integer readTimeout,
        Integer connectionTimeout,
        Integer writeTimeout,
        boolean debug,
        Charset charset,
        Map<String, Object> extraProperties
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
            extraProperties
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

    public static ImapProperties.ImapPropertiesBuilder builder() {
        return new ImapProperties.ImapPropertiesBuilder();
    }

    @Setter
    @ToString
    @NoArgsConstructor
    @Accessors(chain = true, fluent = true)
    public static class ImapPropertiesBuilder {

        private String email;
        private String password;
        private boolean partialFetch = true;
        private Integer fetchSize = 1048576;
        private Integer statusCacheTimeout = 1000;
        private Integer appendBufferSize = 1000;
        private Integer connectionPoolSize = 1;
        private Integer connectionPoolTimeout = 45000;
        private String host;
        private Integer port;
        private EncryptionType encryptionType = EncryptionType.NONE;
        private Integer readTimeout = 30000;
        private Integer connectionTimeout = 30000;
        private Integer writeTimeout = 30000;
        private boolean debug = false;
        private Charset charset = Charset.defaultCharset();
        private Map<String, Object> extraProperties = new HashMap<>();

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
                extraProperties
            );
        }
    }
}
