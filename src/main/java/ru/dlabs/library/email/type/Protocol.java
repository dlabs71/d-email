package ru.dlabs.library.email.type;

import lombok.Getter;

/**
 * It's the enum with supported email protocol names.
 *
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-08-31</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Getter
public enum Protocol {
    SMTP("smtp", "smtp"),
    IMAP("imap", "imap"),
    POP3("pop3", "pop3");

    private final String propName;
    private final String protocolName;

    /**
     * The constructor of this enum.
     *
     * @param propName     a prefix for the configuration properties of Jakarta Mail
     * @param protocolName a protocol name for the configuration of Jakarta Mail
     */
    Protocol(String propName, String protocolName) {
        this.propName = propName;
        this.protocolName = protocolName;
    }


}
