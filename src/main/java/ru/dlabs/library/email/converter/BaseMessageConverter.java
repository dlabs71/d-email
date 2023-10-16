package ru.dlabs.library.email.converter;

import static ru.dlabs.library.email.util.HttpUtils.HTML_CONTENT_TYPE;
import static ru.dlabs.library.email.util.HttpUtils.TEXT_CONTENT_TYPE;

import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.dlabs.library.email.dto.message.common.BaseMessage;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;
import ru.dlabs.library.email.dto.message.common.TransferEncoder;
import ru.dlabs.library.email.dto.message.incoming.DefaultIncomingMessage;
import ru.dlabs.library.email.exception.CheckEmailException;
import ru.dlabs.library.email.util.DateTimeUtils;
import ru.dlabs.library.email.util.EmailMessageUtils;

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
     * It converts a message to a BaseMessage instance
     *
     * @param message the source message
     *
     * @return the instance of the BaseMessage class
     */
    public BaseMessage convert(Message message) {
        BaseMessage baseMessage = convertEnvelopData(message);

        MessagePartConverter.ContentAndAttachments data = MessagePartConverter.getContent(message);
        baseMessage.setContents(data.getContentByType(TEXT_CONTENT_TYPE));
        baseMessage.setAttachments(data.getAttachments());
        return baseMessage;
    }

    /**
     * It converts a message to a DefaultIncomingMessage instance
     *
     * @param message the source message
     *
     * @return the instance of the DefaultIncomingMessage class
     */
    public DefaultIncomingMessage convertToIncomingMessage(Message message) {
        BaseMessage baseMessage = convertEnvelopData(message);

        MessagePartConverter.ContentAndAttachments data = MessagePartConverter.getContent(message);
        baseMessage.setContents(data.getContentByType(TEXT_CONTENT_TYPE));
        if (baseMessage.getContents() == null || baseMessage.getContents().isEmpty()) {
            baseMessage.setContents(data.getContentByType(HTML_CONTENT_TYPE));
        }
        baseMessage.setAttachments(data.getAttachments());

        return new DefaultIncomingMessage(baseMessage, data.getContentByType(HTML_CONTENT_TYPE));
    }

    /**
     * It converts a message to a BaseMessage instance without the content and attachments
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
        baseMessage.setRecipients(MessagePartConverter.getParticipants(message));

        try {
            if (message.getSubject() != null) {
                String decodedSubject = EmailMessageUtils.decodeData(message.getSubject());
                baseMessage.setSubject(decodedSubject);
            }
        } catch (MessagingException e) {
            throw new CheckEmailException(
                "The attempt to get recipients of the message has failed: " + e.getLocalizedMessage());
        }

        Address[] froms;
        try {
            froms = message.getFrom();
        } catch (MessagingException e) {
            throw new CheckEmailException(
                "The attempt to get senders of the message has failed: " + e.getLocalizedMessage());
        }

        if (froms != null && froms.length > 0) {
            InternetAddress internetAddress = (InternetAddress) froms[0];
            baseMessage.setSender(new EmailParticipant(internetAddress.getAddress(), internetAddress.getPersonal()));
        }

        try {
            String transferEncoder = ((MimeMessage) message).getEncoding();
            baseMessage.setTransferEncoder(TransferEncoder.forName(transferEncoder));

            baseMessage.setSize(message.getSize());
            if (message.getSentDate() != null) {
                baseMessage.setSentDate(DateTimeUtils.convert(message.getSentDate()));
            }
            if (message.getReceivedDate() != null) {
                baseMessage.setReceivedDate(DateTimeUtils.convert(message.getReceivedDate()));
            }
        } catch (MessagingException e) {
            throw new CheckEmailException(
                "The attempt to get recipients of the message has failed: " + e.getLocalizedMessage());
        }

        return baseMessage;
    }
}
