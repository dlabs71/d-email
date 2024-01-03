package ru.dlabs71.library.email.tests.converter.incoming.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.time.Instant;
import java.util.Date;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-29</div>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
public class TestReceivedMessage extends MimeMessage {

    public TestReceivedMessage() {
        super((Session) null);
    }

    @Override
    public Date getReceivedDate() throws MessagingException {
        return Date.from(Instant.parse("2023-10-20T10:15:30.000Z"));
    }

    @Override
    public int getSize() throws MessagingException {
        return 2098765;
    }
}
