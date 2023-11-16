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
import ru.dlabs.library.email.util.JavaCoreUtils;
import ru.dlabs.library.email.util.MessageValidator;

/**
 * This class is an implementation of the interface {@link SenderDClient}.
 * It provides opportunities for sending email messages using the SMTP protocol. An SMTP connection will be
 * established by creating the class at once.
 *
 * <p>You should use the instance of the {@link SmtpProperties} class, for configure this class.
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-08-27</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Slf4j
public class SMTPDClient implements SenderDClient {

    private static final Protocol PROTOCOL = Protocol.SMTP;
    private final Session session;
    private final Properties properties;
    private final PasswordAuthentication authentication;
    private final EmailParticipant principal;

    /**
     * Default constructor. It creates the email client object and connects to an SMTP server.
     *
     * @param smtpProperties the properties for connecting to an SMTP server
     */
    public SMTPDClient(SmtpProperties smtpProperties) {
        JavaCoreUtils.notNullArgument(smtpProperties, "smtpProperties");
        this.principal = new EmailParticipant(smtpProperties.getEmail(), smtpProperties.getName());
        this.authentication = new PasswordAuthentication(smtpProperties.getEmail(), smtpProperties.getPassword());
        log.debug("Principal and authentication object were created. {}", this.principal);
        try {
            this.properties = SessionPropertyCollector.createCommonProperties(smtpProperties, PROTOCOL);
            this.properties.put("mail.smtp.auth", "true");
        } catch (Exception e) {
            throw new SessionException(
                "The creation of a connection failed because of the following error: " + e.getMessage());
        }
        log.debug("Configuration properties were created");
        this.session = this.connect();
        log.debug("Session was created. Client is ready to sending messages!");
    }

    /**
     * Connects to the email server using the SMTP protocol.
     *
     * @return {@link Session} object
     *
     * @throws SessionException The connection to the server has failed. The properties are broken.
     */
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

    /**
     * Returns a using protocol name.
     */
    @Override
    public final String getProtocolName() {
        return PROTOCOL.getProtocolName();
    }

    /**
     * Returns name and email address used for connection.
     */
    @Override
    public final EmailParticipant getPrincipal() {
        return this.principal;
    }

    /**
     * It sends to message.
     *
     * @param message the message object
     *
     * @return the result status {@link SendingStatus}
     */
    @Override
    public SendingStatus send(OutgoingMessage message) {
        MessageValidator.validate(message);
        log.debug("Starts sending message. Message is {}", message);

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
            log.error(
                "Convert outgoing message DTO to jakarta message object failed by the next reason: " + ex.getMessage(),
                ex
            );
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
