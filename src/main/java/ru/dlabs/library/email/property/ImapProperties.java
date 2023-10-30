package ru.dlabs.library.email.property;

import java.nio.charset.Charset;
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

    private String email;
    private String password;
    private boolean partialFetch = true;
    private Integer fetchSize = 1048576;
    private Integer statusCacheTimeout = 1000;
    private Integer appendBufferSize = 1000;
    private Integer connectionPoolSize = 10;
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
        Charset charset
    ) {
        super(host, port, encryptionType, readTimeout, connectionTimeout, writeTimeout, debug, charset);
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
                charset
            );
        }
    }
}
