package ru.dlabs.library.email.client.sender;


import static ru.dlabs.library.email.util.EmailMessageUtils.CONTENT_TRANSFER_ENCODING_HDR;
import static ru.dlabs.library.email.util.EmailMessageUtils.CONTENT_TYPE_HDR;
import static ru.dlabs.library.email.util.EmailMessageUtils.FORMAT_HDR;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.Authenticator;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import ru.dlabs.library.email.client.SendingStatus;
import ru.dlabs.library.email.dto.message.api.OutgoingMessage;
import ru.dlabs.library.email.dto.message.common.EmailAttachment;
import ru.dlabs.library.email.exception.CreateMessageException;
import ru.dlabs.library.email.exception.SessionException;
import ru.dlabs.library.email.property.SmtpProperties;
import ru.dlabs.library.email.type.Protocol;
import ru.dlabs.library.email.util.EmailMessageUtils;
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
            props = SessionUtils.createCommonProperties(
                this.smtpProperties,
                Protocol.SMTP
            );
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
    public SendingStatus send(OutgoingMessage message) {
        MessageValidator.validate(message);

        // It's creating an envelope of the message
        MimeMessage envelop;
        try {
            envelop = createEnvelop(message);
        } catch (CreateMessageException ex) {
            log.error(ex.getLocalizedMessage(), ex);
            return SendingStatus.ERROR_IN_MESSAGE;
        }

        MimeMultipart multipart = new MimeMultipart();
        // It's creating and adding a content of the message
        try {
            BodyPart content = createBodyPart(message);
            multipart.addBodyPart(content);
        } catch (CreateMessageException | MessagingException ex) {
            log.error(ex.getLocalizedMessage(), ex);
            return SendingStatus.ERROR_IN_MESSAGE;
        }

        // It's creating and adding attachments of the message
        try {
            List<BodyPart> attachments = createAttachmentParts(message);
            if (attachments != null) {
                for (BodyPart attachment : attachments) {
                    multipart.addBodyPart(attachment);
                }
            }
        } catch (CreateMessageException | MessagingException ex) {
            log.error(ex.getLocalizedMessage(), ex);
            return SendingStatus.ERROR_IN_MESSAGE;
        }

        // It's putting the content and attachments to the message
        try {
            envelop.setContent(multipart);
        } catch (MessagingException ex) {
            log.error(ex.getLocalizedMessage(), ex);
            return SendingStatus.ERROR_IN_MESSAGE;
        }

        // It's sending the created message
        try {
            Transport.send(envelop);
        } catch (MessagingException ex) {
            log.error("Message couldn't be sent due to the following error: " + ex.getLocalizedMessage(), ex);
            return SendingStatus.ERROR_IN_TRANSPORT;
        }
        return SendingStatus.SUCCESS;
    }

    private MimeMessage createEnvelop(OutgoingMessage message) throws CreateMessageException {
        try {
            MimeMessage envelop = new MimeMessage(session);
            envelop.addHeader(FORMAT_HDR, "flowed");
            envelop.addHeader(CONTENT_TRANSFER_ENCODING_HDR, "8bit");
            envelop.setFrom(EmailMessageUtils.createAddress(smtpProperties.getEmail(), smtpProperties.getName()));
            envelop.reply(false);
            envelop.setSubject(message.getSubject(), message.getEncoding());
            envelop.setSentDate(new Date());
            envelop.setRecipients(
                Message.RecipientType.TO,
                EmailMessageUtils.createAddresses(message.getRecipients())
            );
            return envelop;
        } catch (MessagingException | UnsupportedEncodingException ex) {
            throw new CreateMessageException(
                "Message object couldn't be created due to the following error: " + ex.getLocalizedMessage(), ex);
        }
    }

    private BodyPart createBodyPart(OutgoingMessage message) throws CreateMessageException {
        try {
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(message.getContent());
            messageBodyPart.setHeader(CONTENT_TYPE_HDR, message.getContentType());
            return messageBodyPart;
        } catch (MessagingException ex) {
            throw new CreateMessageException(
                "Body part couldn't be created due to the following error: " + ex.getLocalizedMessage(), ex);
        }
    }

    private List<BodyPart> createAttachmentParts(OutgoingMessage message) throws CreateMessageException {
        if (message.getAttachments() == null || message.getAttachments().isEmpty()) {
            return null;
        }
        return message.getAttachments().stream()
            .map(this::createAttachmentPart)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    private BodyPart createAttachmentPart(EmailAttachment attachment) throws CreateMessageException {
        if (attachment == null || attachment.getData() == null || attachment.getData().length == 0) {
            return null;
        }
        try {
            MimeBodyPart attachmentPart = new MimeBodyPart();
            DataSource dataSource = new ByteArrayDataSource(attachment.getData(), attachment.getContentType());
            attachmentPart.setDataHandler(new DataHandler(dataSource));
            attachmentPart.setFileName(attachment.getName());
            attachmentPart.setHeader(CONTENT_TYPE_HDR, attachment.getContentType());
            return attachmentPart;
        } catch (MessagingException e) {
            throw new CreateMessageException(
                "Body part couldn't be created due to the following error: " + e.getLocalizedMessage(), e);
        }
    }
}
