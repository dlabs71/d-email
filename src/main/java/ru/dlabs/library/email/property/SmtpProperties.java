package ru.dlabs.library.email.property;

import java.nio.charset.Charset;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import ru.dlabs.library.email.type.EncryptionType;

/**
 * The properties for connecting to an email server by the SMTP protocol
 *
 * @author Ivanov Danila
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
public class SmtpProperties extends CommonProperties {

    private String email;
    private String password;
    private String name;

    public SmtpProperties(
        String email,
        String password,
        String name,
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
        this.name = name;
    }

    public static SmtpPropertiesBuilder builder() { return new SmtpPropertiesBuilder(); }

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
                charset
            );
        }
    }
}
