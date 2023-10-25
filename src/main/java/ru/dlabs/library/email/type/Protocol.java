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
    SMTP("smtp", "smtp"),
    IMAP("imap", "imap"),
    POP3("pop3", "pop3");

    private final String propName;
    private final String protocolName;

    Protocol(String propName, String protocolName) {
        this.propName = propName;
        this.protocolName = protocolName;
    }


}
