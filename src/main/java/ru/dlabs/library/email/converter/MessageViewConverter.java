package ru.dlabs.library.email.converter;

import jakarta.mail.Address;
import jakarta.mail.Flags;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import java.nio.charset.StandardCharsets;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.angus.mail.imap.IMAPMessage;
import ru.dlabs.library.email.dto.message.MessageView;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;
import ru.dlabs.library.email.exception.CheckEmailException;
import ru.dlabs.library.email.util.DateTimeUtils;

/**
 * Utility class is for converting a jakarta.mail.Message to an instance of the MessageView class
 *
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-09-01
 */
@Slf4j
@UtilityClass
public class MessageViewConverter {

    /**
     * It converts the message to an instance of the MessageView class
     *
     * @param message the source message
     *
     * @return instance of the MessageView class
     */
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

        try {
            builder.seen(message.isSet(Flags.Flag.SEEN));
        } catch (MessagingException e) {
            log.warn("It is impossible to determine whether a message has been flagged as seen. "
                         + e.getLocalizedMessage());
        }

        if (froms.length > 0) {
            InternetAddress internetAddress = (InternetAddress) froms[0];
            builder.sender(new EmailParticipant(internetAddress.getAddress(), internetAddress.getPersonal()));
        }

        try {
            String encoding = ((IMAPMessage) message).getEncoding();

            builder.contentType(message.getContentType());
            builder.encoding(encoding != null ? encoding : StandardCharsets.UTF_8.name());
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
