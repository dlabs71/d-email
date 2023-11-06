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
import ru.dlabs.library.email.util.JavaCoreUtils;

/**
 * The Utility class to convert a {@link OutgoingMessage} to an instance
 * of the {@link Message} class or its inheritors.
 *
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

    /**
     * It converts an instance of the {@link OutgoingMessage} to the {@link Message}.
     *
     * @param message   an instance of the {@link OutgoingMessage}
     * @param session   an instance of connection to email server. Needs to construct {@link MimeMessage}
     * @param emailFrom an email address a sender
     * @param nameFrom  a real name of a sender
     *
     * @return instance of the {@link Message}
     *
     * @throws CreateMessageException if the message's envelope creation failed
     * @throws MessagingException     if any error while message creating
     */
    public Message convert(
        OutgoingMessage message,
        Session session,
        String emailFrom,
        String nameFrom
    )
        throws CreateMessageException, MessagingException {
        if (message == null) {
            return null;
        }
        JavaCoreUtils.notNullArgument(session, "session");
        JavaCoreUtils.notNullArgument(emailFrom, "emailFrom");
        log.debug(
            "Starts converting outgoing message to jakarta Message. Email = {}, Name = {}, Message class = {}",
            emailFrom,
            nameFrom,
            message.getClass()
        );

        MimeMultipart multipart = new MimeMultipart();
        // It's creating and adding a content of the message
        List<BodyPart> parts = JakartaMessagePartConverter.convertBodyPart(message);
        for (BodyPart part : parts) {
            multipart.addBodyPart(part);
        }
        log.debug("Converts message contents successfully. Size is {}", parts.size());

        // It's creating and adding attachments of the message
        List<BodyPart> attachments = JakartaMessagePartConverter.convertAttachmentParts(message);
        if (attachments != null) {
            for (BodyPart attachment : attachments) {
                multipart.addBodyPart(attachment);
            }
        }
        log.debug("Converts message attachments successfully. Size is {}", attachments.size());

        // It's creating an envelope of the message
        MimeMessage envelop = createEnvelop(message, session, emailFrom, nameFrom);

        // It's putting the content and attachments to the message
        envelop.setContent(multipart);
        envelop.addHeader(CONTENT_TRANSFER_ENCODING_HDR, message.getTransferEncoder().getName());
        return envelop;
    }

    /**
     * Creates envelop of the {@link jakarta.mail.Message} from the {@link OutgoingMessage}.
     * Creates a new instance and fills in common fields.
     *
     * @param message   an instance of the {@link OutgoingMessage}
     * @param session   an instance of connection to email server. Needs to construct {@link MimeMessage}
     * @param emailFrom an email address a sender
     * @param nameFrom  a real name of a sender
     *
     * @return instance of the {@link Message}
     *
     * @throws CreateMessageException if any error while message creating
     */
    public MimeMessage createEnvelop(
        OutgoingMessage message,
        Session session,
        String emailFrom,
        String nameFrom
    )
        throws CreateMessageException {
        if (message == null) {
            return null;
        }
        JavaCoreUtils.notNullArgument(session, "session");
        JavaCoreUtils.notNullArgument(emailFrom, "emailFrom");

        try {
            MimeMessage envelop = new MimeMessage(session);
            envelop.setFrom(EmailMessageUtils.createAddress(emailFrom, nameFrom));
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
