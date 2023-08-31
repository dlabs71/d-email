package ru.dlabs.library.email.properties;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-08-30
 */
@Getter
@Setter
@NoArgsConstructor
public class ImapProperties extends CommonProperties {

    private Map<String, Credentials> credentials;
    private boolean partialFetch = true;
    private Integer fetchSize = 1048576;
    private Integer statusCacheTimeout = 1000;
    private Integer appendBufferSize = 1000;
    private Integer connectionPoolSize = 1;
    private Integer connectionPoolTimeout = 45000;

    public ImapProperties(
        Map<String, Credentials> credentials,
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
        boolean debug
    ) {
        super(host, port, encryptionType, readTimeout, connectionTimeout, writeTimeout, debug);
        this.credentials = credentials;
        this.partialFetch = partialFetch;
        this.fetchSize = fetchSize;
        this.statusCacheTimeout = statusCacheTimeout;
        this.appendBufferSize = appendBufferSize;
        this.connectionPoolSize = connectionPoolSize;
        this.connectionPoolTimeout = connectionPoolTimeout;
    }

    @Getter
    @AllArgsConstructor
    public static class Credentials {

        private String email;
        private String password;
    }

    public static ImapProperties.ImapPropertiesBuilder builder() { return new ImapProperties.ImapPropertiesBuilder(); }

    @Setter
    @ToString
    @NoArgsConstructor
    @Accessors(chain = true, fluent = true)
    public static class ImapPropertiesBuilder {

        private Map<String, Credentials> credentials;
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

        public ImapProperties build() {
            return new ImapProperties(
                credentials,
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
                debug
            );
        }
    }
}
