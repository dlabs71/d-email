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
 * The properties for connecting to an email server by the SMTP protocol.
 *
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-08-31</div>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
public class SmtpProperties extends CommonProperties {

    /**
     * Email address of a mailbox to witch to connect.
     */
    private String email;

    /**
     * Password of a mailbox to witch to connect.
     */
    private String password;

    /**
     * Name of an email account.
     */
    private String name;

    /**
     * The constructor of this class.
     */
    public SmtpProperties(
        String email,
        String password,
        String name,
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
        this.name = name;
    }

    /**
     * Returns the builder of this class.
     */
    public static SmtpPropertiesBuilder builder() {
        return new SmtpPropertiesBuilder();
    }

    /**
     * Builder class for {@link SmtpProperties}.
     */
    @Setter
    @ToString
    @NoArgsConstructor
    @Accessors(chain = true, fluent = true)
    public static class SmtpPropertiesBuilder {

        private String email;
        private String password;
        private String name;
        private String host;
        private Integer port;
        private EncryptionType encryptionType = EncryptionType.NONE;
        private Integer readTimeout = 30000;
        private Integer connectionTimeout = 30000;
        private Integer writeTimeout = 30000;
        private boolean debug = false;
        private Charset charset = Charset.defaultCharset();
        private Map<String, Object> extraProperties = new HashMap<>();
        private int maxAttemptsOfRequest = 3;
        private int attemptDelayOfRequest = 0;

        /**
         * Builds and returns a new instance of {@link SmtpProperties}.
         */
        public SmtpProperties build() {
            return new SmtpProperties(
                email,
                password,
                name,
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
