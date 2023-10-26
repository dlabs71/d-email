package ru.dlabs.library.email.converter.outgoing;

import static ru.dlabs.library.email.util.HttpUtils.CONTENT_TRANSFER_ENCODING_HDR;

import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
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
        List<BodyPart> parts = JakartaMessagePartConverter.createBodyPart(message);
        for (BodyPart part : parts) {
            multipart.addBodyPart(part);
        }

        // It's creating and adding attachments of the message
        List<BodyPart> attachments = JakartaMessagePartConverter.createAttachmentParts(message);
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
            envelop.setRecipients(Message.RecipientType.TO, EmailMessageUtils.createAddresses(message.getRecipients()));
            return envelop;
        } catch (MessagingException | UnsupportedEncodingException ex) {
            throw new CreateMessageException(
                "Message object couldn't be created due to the following error: " + ex.getMessage(),
                ex
            );
        }
    }
}
