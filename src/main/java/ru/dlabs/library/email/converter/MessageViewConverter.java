package ru.dlabs.library.email.converter;

import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import lombok.experimental.UtilityClass;
import ru.dlabs.library.email.exception.CheckEmailException;
import ru.dlabs.library.email.message.EmailParticipant;
import ru.dlabs.library.email.message.MessageView;
import ru.dlabs.library.email.utils.DateTimeUtils;

/**
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-09-01
 */
@UtilityClass
public class MessageViewConverter {

    public MessageView convert(Message message) {
        MessageView.MessageViewBuilder builder = MessageView.builder();
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
