package ru.dlabs.library.email.client.sender;


import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import ru.dlabs.library.email.client.SendingStatus;
import ru.dlabs.library.email.converter.JakartaMessageConverter;
import ru.dlabs.library.email.dto.message.outgoing.OutgoingMessage;
import ru.dlabs.library.email.exception.CreateMessageException;
import ru.dlabs.library.email.exception.SessionException;
import ru.dlabs.library.email.property.SmtpProperties;
import ru.dlabs.library.email.type.Protocol;
import ru.dlabs.library.email.util.MessageValidator;
import ru.dlabs.library.email.util.SessionUtils;

/**
 * SMTP email client for sending messages using the SMTP protocol
 *
 * @author Ivanov Danila
 * @version 1.0
 */
@Slf4j
public class SMTPDClient implements SenderDClient {

    public final static String PROTOCOL_NAME = "smtp";

    private final SmtpProperties smtpProperties;
    private final Session session;

    /**
     * Default constructor. It creates the email client object and connects to an SMTP server
     *
     * @param smtpProperties the properties for connecting to an SMTP server
     */
    public SMTPDClient(SmtpProperties smtpProperties) {
        this.smtpProperties = smtpProperties;
        this.session = this.connect();
    }

    @Override
    public Session connect() throws SessionException {
        Properties props;
        try {
            props = SessionUtils.createCommonProperties(this.smtpProperties, Protocol.SMTP);
        } catch (Exception e) {
            throw new SessionException(
                "The creation of a connection failed because of the following error: " + e.getLocalizedMessage());
        }

        props.put("mail.smtp.auth", "true");
        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpProperties.getEmail(), smtpProperties.getPassword());
            }
        };
        try {
            return Session.getInstance(props, auth);
        } catch (Exception e) {
            throw new SessionException(
                "The creation of a connection failed because of the following error: " + e.getLocalizedMessage());
        }
    }

    @Override
    public String getProtocolName() {
        return PROTOCOL_NAME;
    }


    @Override
    public SendingStatus send(OutgoingMessage message) {
        MessageValidator.validate(message);

        // It's creating an envelope of the message
        Message jakartaMessage;
        try {
            jakartaMessage = JakartaMessageConverter.convert(message,
                                                             session,
                                                             smtpProperties.getEmail(),
                                                             smtpProperties.getName()
            );
        } catch (CreateMessageException | MessagingException ex) {
            log.error(ex.getLocalizedMessage(), ex);
            return SendingStatus.ERROR_IN_MESSAGE;
        }

        // It's sending the created message
        try {
            Transport.send(jakartaMessage);
        } catch (MessagingException ex) {
            log.error("Message couldn't be sent due to the following error: " + ex.getLocalizedMessage(), ex);
            return SendingStatus.ERROR_IN_TRANSPORT;
        }
        return SendingStatus.SUCCESS;
    }
}
