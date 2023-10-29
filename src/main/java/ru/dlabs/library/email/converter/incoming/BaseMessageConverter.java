package ru.dlabs.library.email.converter.incoming;

import static ru.dlabs.library.email.util.HttpUtils.CONTENT_TRANSFER_ENCODING_HDR;
import static ru.dlabs.library.email.util.HttpUtils.HTML_CONTENT_TYPE;
import static ru.dlabs.library.email.util.HttpUtils.TEXT_CONTENT_TYPE;

import jakarta.mail.Address;
import jakarta.mail.Flags;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.angus.mail.imap.IMAPMessage;
import ru.dlabs.library.email.dto.message.common.BaseMessage;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;
import ru.dlabs.library.email.dto.message.incoming.DefaultIncomingMessage;
import ru.dlabs.library.email.exception.CheckEmailException;
import ru.dlabs.library.email.type.TransferEncoder;
import ru.dlabs.library.email.util.EmailMessageUtils;
import ru.dlabs.library.email.util.JavaCoreUtils;

/**
 * The Utility class to convert a jakarta.mail.Message to an instance of the BaseMessage class or its inheritors
 * </p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-09-01</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Slf4j
@UtilityClass
public class BaseMessageConverter {

    /**
     * It converts a message to a BaseMessage instance.
     *
     * @param message the source message
     *
     * @return the instance of the BaseMessage class
     */
    public BaseMessage convert(Message message) {
        if (message == null) {
            return null;
        }
        BaseMessage baseMessage = convertEnvelopData(message);

        MessagePartConverter.ContentAndAttachments data = MessagePartConverter.getContent(message);
        baseMessage.setContents(data.getContentByType(TEXT_CONTENT_TYPE));
        baseMessage.addAllContent(data.getContentByType(HTML_CONTENT_TYPE));
        baseMessage.setAttachments(data.getAttachments());
        return baseMessage;
    }

    /**
     * It converts a message to a DefaultIncomingMessage instance.
     *
     * @param message the source message
     *
     * @return the instance of the DefaultIncomingMessage class
     */
    public DefaultIncomingMessage convertToIncomingMessage(Message message) {
        if (message == null) {
            return null;
        }
        BaseMessage baseMessage = convert(message);
        return new DefaultIncomingMessage(baseMessage);
    }

    /**
     * It converts a message to a BaseMessage instance without the content and attachments.
     *
     * @param message the source message
     *
     * @return the instance of the BaseMessage class
     */
    public BaseMessage convertEnvelopData(Message message) {
        if (message == null) {
            return null;
        }
        BaseMessage baseMessage = new BaseMessage();
        baseMessage.setId(message.getMessageNumber());
        baseMessage.setRecipients(MessagePartConverter.getRecipients(message));

        try {
            if (message.getSubject() != null) {
                String decodedSubject = EmailMessageUtils.decodeData(message.getSubject());
                baseMessage.setSubject(decodedSubject);
            }
        } catch (MessagingException e) {
            throw new CheckEmailException(
                "The attempt to get recipients of the message has failed: " + e.getMessage());
        }

        Address[] froms;
        try {
            froms = message.getFrom();
        } catch (MessagingException e) {
            throw new CheckEmailException(
                "The attempt to get senders of the message has failed: " + e.getMessage());
        }

        if (froms != null && froms.length > 0) {
            InternetAddress internetAddress = (InternetAddress) froms[0];
            baseMessage.setSender(new EmailParticipant(internetAddress.getAddress(), internetAddress.getPersonal()));
        }

        // Set the read flag
        try {
            baseMessage.setSeen(message.isSet(Flags.Flag.SEEN));
        } catch (MessagingException e) {
            log.warn(
                "It is impossible to determine whether a message has been flagged as seen. " + e.getMessage());
        }

        try {
            String transferEncoder = ((MimeMessage) message).getHeader(CONTENT_TRANSFER_ENCODING_HDR, null);
            baseMessage.setTransferEncoder(TransferEncoder.forName(transferEncoder));

            if (message instanceof IMAPMessage) {
                baseMessage.setSize(((IMAPMessage) message).getSizeLong());
            } else {
                baseMessage.setSize((long) message.getSize());
            }

            if (message.getSentDate() != null) {
                baseMessage.setSentDate(JavaCoreUtils.convert(message.getSentDate()));
            }
            if (message.getReceivedDate() != null) {
                baseMessage.setReceivedDate(JavaCoreUtils.convert(message.getReceivedDate()));
            }
        } catch (MessagingException e) {
            throw new CheckEmailException(
                "The attempt to get recipients of the message has failed: " + e.getMessage());
        }

        return baseMessage;
    }
}
