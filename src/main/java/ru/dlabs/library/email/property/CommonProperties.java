package ru.dlabs.library.email.property;

import java.nio.charset.Charset;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.dlabs.library.email.type.EncryptionType;

/**
 * The common class of properties for connecting
 *
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-08-30
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class CommonProperties {

    private String host;
    private Integer port;
    private EncryptionType encryptionType = EncryptionType.NONE;
    private Integer readTimeout = 30000;
    private Integer connectionTimeout = 30000;
    private Integer writeTimeout = 30000;
    private boolean debug = false;
    private Charset charset = Charset.defaultCharset();
}
