package ru.dlabs.library.email.converter.incoming;

import static ru.dlabs.library.email.util.ProtocolUtils.CONTENT_TRANSFER_ENCODING_HDR;
import static ru.dlabs.library.email.util.ProtocolUtils.HTML_CONTENT_TYPE;
import static ru.dlabs.library.email.util.ProtocolUtils.TEXT_CONTENT_TYPE;

import jakarta.mail.Address;
import jakarta.mail.Flags;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.angus.mail.imap.IMAPMessage;
import ru.dlabs.library.email.dto.message.common.BaseMessage;
import ru.dlabs.library.email.dto.message.common.ContentMessage;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;
import ru.dlabs.library.email.dto.message.incoming.DefaultIncomingMessage;
import ru.dlabs.library.email.exception.CheckEmailException;
import ru.dlabs.library.email.type.TransferEncoder;
import ru.dlabs.library.email.util.EmailMessageUtils;
import ru.dlabs.library.email.util.JavaCoreUtils;

/**
 * The Utility class to convert a {@link jakarta.mail.Message} to an instance
 * of the {@link BaseMessage} class or its inheritors.
 *
 * <p>
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
        log.debug("Starts convert jakarta Message to BaseMessage. Jakarta Message is {}", message);
        if (message == null) {
            return null;
        }
        BaseMessage baseMessage = convertEnvelopData(message);

        MessagePartConverter.ContentAndAttachments data = MessagePartConverter.getContent(message);

        List<ContentMessage> textContents = data.getContentByType(TEXT_CONTENT_TYPE);
        List<ContentMessage> htmlContents = data.getContentByType(HTML_CONTENT_TYPE);

        baseMessage.setContents(textContents);
        baseMessage.addAllContent(htmlContents);
        baseMessage.setAttachments(data.getAttachments());

        log.debug(
            "BaseMessage has: text contents are {}, html contents are {} and attachments are {}",
            textContents.size(),
            htmlContents.size(),
            data.getAttachments().size()
        );
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
        log.debug("Starts convert content Jakarta Message to envelop of BaseMessage.");
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
        log.debug("Subject converted successfully");

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
            log.debug("Sender converted successfully. The size of the list of senders is {}", froms.length);
        }

        // Set the read flag
        try {
            baseMessage.setSeen(message.isSet(Flags.Flag.SEEN));
            log.debug("Seen flag converted successfully");
        } catch (MessagingException e) {
            log.warn(
                "It is impossible to determine whether a message has been flagged as seen. " + e.getMessage());
        }

        try {
            String transferEncoder = ((MimeMessage) message).getHeader(CONTENT_TRANSFER_ENCODING_HDR, null);
            baseMessage.setTransferEncoder(TransferEncoder.forName(transferEncoder));
            log.debug("Transfer encoding converted successfully. Transfer Encoding is {}", transferEncoder);

            if (message instanceof IMAPMessage) {
                baseMessage.setSize(((IMAPMessage) message).getSizeLong());
            } else {
                baseMessage.setSize((long) message.getSize());
            }
            log.debug("Size of message converted successfully. Size is {}", baseMessage.getSize());

            if (message.getSentDate() != null) {
                baseMessage.setSentDate(JavaCoreUtils.convert(message.getSentDate()));
            }
            log.debug("Sent date of message converted successfully. Sent date is {}", baseMessage.getSentDate());

            if (message.getReceivedDate() != null) {
                baseMessage.setReceivedDate(JavaCoreUtils.convert(message.getReceivedDate()));
            }
            log.debug(
                "Received date of message converted successfully. Received date is {}",
                baseMessage.getReceivedDate()
            );
        } catch (MessagingException e) {
            throw new CheckEmailException(
                "The attempt to get recipients of the message has failed: " + e.getMessage());
        }

        log.debug("The envelop of the message created successfully");
        return baseMessage;
    }
}
