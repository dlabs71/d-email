package ru.dlabs.library.email.client.sender;


import jakarta.mail.Authenticator;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.dlabs.library.email.client.SendingStatus;
import ru.dlabs.library.email.message.Message;
import ru.dlabs.library.email.properties.Protocol;
import ru.dlabs.library.email.properties.SmtpProperties;
import ru.dlabs.library.email.utils.EmailMessageUtils;
import ru.dlabs.library.email.utils.SessionUtils;

/**
 * SMTP email client for sending messages using the SMTP protocol
 *
 * @author Ivanov Danila
 * @version 1.0
 */
@Slf4j
public class SMTPDClient implements SenderDClient {

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

    @SneakyThrows
    @Override
    public Session connect() {
        Properties props = SessionUtils.createCommonProperties(
            this.smtpProperties,
            Protocol.SMTP
        );

        props.put("mail.smtp.auth", "true");
        Authenticator auth = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(smtpProperties.getEmail(), smtpProperties.getPassword());
            }
        };
        return Session.getInstance(props, auth);
    }


    @Override
    public SendingStatus send(Message message) {
        MimeMessage msg = new MimeMessage(session);
        try {
            EmailMessageUtils.addCommonHeaders(msg);
            msg.setFrom(EmailMessageUtils.createAddress(smtpProperties.getEmail(), smtpProperties.getName()));
            msg.reply(false);
            msg.setSubject(message.getSubject(), message.getEncoding());
            msg.setContent(message.getContent(), message.getContentType());
            msg.setSentDate(new Date());
            msg.setRecipients(
                jakarta.mail.Message.RecipientType.TO,
                EmailMessageUtils.createAddresses(message.getRecipientEmail())
            );
        } catch (MessagingException | UnsupportedEncodingException ex) {
            log.error("Message object couldn't be created due to the following error: " + ex.getLocalizedMessage(), ex);
            return SendingStatus.ERROR_IN_MESSAGE;
        }

        try {
            Transport.send(msg);
        } catch (MessagingException ex) {
            log.error("Message couldn't be sent due to the following error: " + ex.getLocalizedMessage(), ex);
            return SendingStatus.ERROR_IN_TRANSPORT;
        }
        return SendingStatus.SUCCESS;
    }
}
