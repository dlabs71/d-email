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
import org.eclipse.angus.mail.util.MailSSLSocketFactory;
import ru.dlabs.library.email.client.SendingStatus;
import ru.dlabs.library.email.message.Message;
import ru.dlabs.library.email.properties.EncryptionType;
import ru.dlabs.library.email.properties.SmtpProperties;
import ru.dlabs.library.email.utils.EmailMessageUtils;

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
    private final boolean debug;

    /**
     * Default constructor. It creates the email client object and connects to an SMTP server
     *
     * @param smtpProperties the properties for connecting to an SMTP server
     * @param debug          the flag for switching on the debug level
     */
    public SMTPDClient(SmtpProperties smtpProperties, boolean debug) {
        this.smtpProperties = smtpProperties;
        this.debug = debug;
        this.session = this.connect();
    }

    @SneakyThrows
    @Override
    public Session connect() {
        Properties props = new Properties();
        props.put("mail.smtp.host", smtpProperties.getHost());
        props.put("mail.smtp.port", String.valueOf(smtpProperties.getPort()));
        props.put("mail.smtp.timeout", smtpProperties.getReadTimeout());
        props.put("mail.smtp.connectiontimeout", smtpProperties.getConnectionTimeout());
        props.put("mail.smtp.writetimeout", smtpProperties.getWriteTimeout());
        props.put("mail.mime.allowutf8", "true");
        props.put("mail.debug", this.debug);

        if (EncryptionType.SSL.equals(smtpProperties.getEncryptionType())) {
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.socketFactory.port", String.valueOf(smtpProperties.getPort()));
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            props.put("mail.smtp.ssl.socketFactory", sf);
            props.put("mail.smtp.ssl.checkserveridentity", "true");
        } else if (EncryptionType.TLS.equals(smtpProperties.getEncryptionType())) {
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
        }

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
