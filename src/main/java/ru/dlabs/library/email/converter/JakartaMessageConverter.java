package ru.dlabs.library.email.converter;

import static ru.dlabs.library.email.util.HttpUtils.CONTENT_TRANSFER_ENCODING_HDR;
import static ru.dlabs.library.email.util.HttpUtils.CONTENT_TYPE_HDR;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.dlabs.library.email.dto.message.common.ContentMessage;
import ru.dlabs.library.email.dto.message.common.EmailAttachment;
import ru.dlabs.library.email.dto.message.outgoing.OutgoingMessage;
import ru.dlabs.library.email.exception.CreateMessageException;
import ru.dlabs.library.email.util.EmailMessageUtils;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-17</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Slf4j
@UtilityClass
public class JakartaMessageConverter {

    public Message convert(OutgoingMessage message, Session session, String emailFrom, String nameFrom)
        throws CreateMessageException, MessagingException {
        // It's creating an envelope of the message
        MimeMessage envelop = createEnvelop(message, session, emailFrom, nameFrom);

        MimeMultipart multipart = new MimeMultipart();
        // It's creating and adding a content of the message
        List<BodyPart> parts = createBodyPart(message);
        for (BodyPart part : parts) {
            multipart.addBodyPart(part);
        }

        // It's creating and adding attachments of the message
        List<BodyPart> attachments = createAttachmentParts(message);
        if (attachments != null) {
            for (BodyPart attachment : attachments) {
                multipart.addBodyPart(attachment);
            }
        }

        // It's putting the content and attachments to the message
        envelop.setContent(multipart);
        envelop.addHeader(CONTENT_TRANSFER_ENCODING_HDR, message.getTransferEncoder().getName());
        return envelop;
    }

    public MimeMessage createEnvelop(OutgoingMessage message, Session session, String emailFrom, String nameFrom)
        throws CreateMessageException {
        try {
            MimeMessage envelop = new MimeMessage(session);
            envelop.setFrom(EmailMessageUtils.createAddress(emailFrom, nameFrom));
            envelop.reply(false);
            envelop.setSubject(message.getSubject());
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

    public List<BodyPart> createBodyPart(OutgoingMessage message) throws CreateMessageException {
        return message.getContents().stream()
            .map(JakartaMessageConverter::createBodyPart)
            .collect(Collectors.toList());
    }

    public BodyPart createBodyPart(ContentMessage content) throws CreateMessageException {
        try {
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(content.getData());
            messageBodyPart.setHeader(CONTENT_TYPE_HDR, content.getContentType());
            return messageBodyPart;
        } catch (MessagingException ex) {
            throw new CreateMessageException(
                "Body part couldn't be created due to the following error: " + ex.getLocalizedMessage(), ex);
        }
    }

    public List<BodyPart> createAttachmentParts(OutgoingMessage message) throws CreateMessageException {
        if (message.getAttachments() == null || message.getAttachments().isEmpty()) {
            return null;
        }
        return message.getAttachments().stream()
            .map(JakartaMessageConverter::createAttachmentPart)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    public BodyPart createAttachmentPart(EmailAttachment attachment) throws CreateMessageException {
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
