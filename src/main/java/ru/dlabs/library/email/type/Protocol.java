package ru.dlabs.library.email.type;

import lombok.Getter;

/**
 * It's the enum with protocol names
 *
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-08-30
 */
@Getter
public enum Protocol {
    SMTP("smtp", null),
    IMAP("imap", null),
    POP3("pop3", null);

    private final String propName;
    private final String storeProtocolName;

    Protocol(String propName, String storeProtocolName) {
        this.propName = propName;
        this.storeProtocolName = storeProtocolName;
    }


}
