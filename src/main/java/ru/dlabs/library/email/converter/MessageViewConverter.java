package ru.dlabs.library.email.converter;

import static ru.dlabs.library.email.util.HttpUtils.CONTENT_TRANSFER_ENCODING_HDR;

import jakarta.mail.Address;
import jakarta.mail.Flags;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;
import ru.dlabs.library.email.dto.message.common.TransferEncoder;
import ru.dlabs.library.email.dto.message.incoming.MessageView;
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
        builder.recipients(MessagePartConverter.getParticipants(message));

        // Extraction of the subject
        try {
            builder.subject(message.getSubject());
        } catch (MessagingException e) {
            throw new CheckEmailException(
                "The attempt to get recipients of the message has failed: " + e.getLocalizedMessage()
            );
        }

        // Extraction of the sender address
        Address[] froms;
        try {
            froms = message.getFrom();
        } catch (MessagingException e) {
            throw new CheckEmailException(
                "The attempt to get senders of the message has failed: " + e.getLocalizedMessage()
            );
        }

        // Senders of email messages can be several. But we take only one — the first.
        if (froms.length > 0) {
            InternetAddress internetAddress = (InternetAddress) froms[0];
            builder.sender(new EmailParticipant(internetAddress.getAddress(), internetAddress.getPersonal()));
        }

        // Set the read flag
        try {
            builder.seen(message.isSet(Flags.Flag.SEEN));
        } catch (MessagingException e) {
            log.warn("It is impossible to determine whether a message has been flagged as seen. "
                         + e.getLocalizedMessage());
        }

        // Set a metadata of the message
        try {
            String transferEncoder = ((MimeMessage) message).getHeader(CONTENT_TRANSFER_ENCODING_HDR, null);
            builder.transferEncoder(TransferEncoder.forName(transferEncoder));

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
