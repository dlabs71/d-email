package ru.dlabs.library.email.properties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Ivanov Danila
 * @version 1.0
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmtpProperties {

    private String host;
    private int port;
    private String email;
    private String password;
    private String name;
    private EncryptionType encryptionType = EncryptionType.NONE;
    private Integer readTimeout = 30000;
    private Integer connectionTimeout = 30000;
    private Integer writeTimeout = 30000;
}
