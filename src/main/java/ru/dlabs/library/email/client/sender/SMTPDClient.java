package ru.dlabs.library.email.client.sender;


import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import ru.dlabs.library.email.converter.outgoing.JakartaMessageConverter;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;
import ru.dlabs.library.email.dto.message.outgoing.OutgoingMessage;
import ru.dlabs.library.email.exception.CreateMessageException;
import ru.dlabs.library.email.exception.SessionException;
import ru.dlabs.library.email.property.SessionPropertyCollector;
import ru.dlabs.library.email.property.SmtpProperties;
import ru.dlabs.library.email.type.Protocol;
import ru.dlabs.library.email.type.SendingStatus;
import ru.dlabs.library.email.util.MessageValidator;

/**
 * SMTP email client for sending messages using the SMTP protocol
 *
 * @author Ivanov Danila
 * @version 1.0
 */
@Slf4j
public class SMTPDClient implements SenderDClient {

    private static final Protocol PROTOCOL = Protocol.SMTP;
    private final Session session;
    private final Properties properties;
    private final PasswordAuthentication authentication;
    private final EmailParticipant principal;

    /**
     * Default constructor. It creates the email client object and connects to an SMTP server
     *
     * @param smtpProperties the properties for connecting to an SMTP server
     */
    public SMTPDClient(SmtpProperties smtpProperties) {
        this.principal = new EmailParticipant(smtpProperties.getEmail(), smtpProperties.getName());
        this.authentication = new PasswordAuthentication(smtpProperties.getEmail(), smtpProperties.getPassword());
        try {
            this.properties = SessionPropertyCollector.createCommonProperties(smtpProperties, PROTOCOL);
            this.properties.put("mail.smtp.auth", "true");
        } catch (Exception e) {
            throw new SessionException(
                "The creation of a connection failed because of the following error: " + e.getMessage());
        }
        this.session = this.connect();
    }

    @Override
    public Session connect() throws SessionException {
        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return authentication;
            }
        };
        try {
            return Session.getInstance(this.properties, auth);
        } catch (Exception e) {
            throw new SessionException(
                "The creation of a connection failed because of the following error: " + e.getMessage());
        }
    }

    @Override
    public String getProtocolName() {
        return PROTOCOL.getProtocolName();
    }

    @Override
    public EmailParticipant getPrincipal() {
        return this.principal;
    }


    @Override
    public SendingStatus send(OutgoingMessage message) {
        MessageValidator.validate(message);

        // It's creating an envelope of the message
        Message jakartaMessage;
        try {
            jakartaMessage = JakartaMessageConverter.convert(
                message,
                session,
                this.principal.getEmail(),
                this.principal.getName()
            );
        } catch (CreateMessageException | MessagingException ex) {
            log.error(ex.getMessage(), ex);
            return SendingStatus.ERROR_IN_MESSAGE;
        }

        // It's sending the created message
        try {
            Transport.send(jakartaMessage);
        } catch (MessagingException ex) {
            log.error("Message couldn't be sent due to the following error: " + ex.getMessage(), ex);
            return SendingStatus.ERROR_IN_TRANSPORT;
        }
        return SendingStatus.SUCCESS;
    }
}
