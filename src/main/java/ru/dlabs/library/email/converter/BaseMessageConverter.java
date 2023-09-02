package ru.dlabs.library.email.converter;

import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import java.util.List;
import lombok.experimental.UtilityClass;
import ru.dlabs.library.email.exception.CheckEmailException;
import ru.dlabs.library.email.message.BaseMessage;
import ru.dlabs.library.email.message.EmailAttachment;
import ru.dlabs.library.email.message.EmailParticipant;
import ru.dlabs.library.email.utils.DateTimeUtils;

/**
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-09-01
 */
@UtilityClass
public class BaseMessageConverter {

    public BaseMessage convert(Message message) {
        BaseMessage.BaseMessageBuilder builder = BaseMessage.baseMessageBuilder();
        builder.id(message.getMessageNumber());
        builder.recipientEmail(MessagePartConverter.getParticipants(message));

        try {
            builder.subject(message.getSubject());
        } catch (MessagingException e) {
            throw new CheckEmailException(
                "The attempt to get recipients of the message has failed: " + e.getLocalizedMessage()
            );
        }

        Address[] froms;
        try {
            froms = message.getFrom();
        } catch (MessagingException e) {
            throw new CheckEmailException(
                "The attempt to get senders of the message has failed: " + e.getLocalizedMessage()
            );
        }

        if (froms.length > 0) {
            InternetAddress internetAddress = (InternetAddress) froms[0];
            builder.sender(new EmailParticipant(internetAddress.getAddress(), internetAddress.getPersonal()));
        }

        String content = MessagePartConverter.getContent(message);
        List<EmailAttachment> attachments = MessagePartConverter.getAttachments(message);
        builder.content(content);
        builder.attachments(attachments);

        try {
            builder.contentType(message.getContentType());
            builder.encoding(message.getHeader("Encoding")[0]);
            builder.size(message.getSize());
            builder.sentDate(DateTimeUtils.convert(message.getSentDate()));
            builder.receivedDate(DateTimeUtils.convert(message.getReceivedDate()));
        } catch (MessagingException e) {
            throw new CheckEmailException(
                "The attempt to get recipients of the message has failed: " + e.getLocalizedMessage()
            );
        }

        return builder.build();
    }
}
