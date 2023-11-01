package ru.dlabs.library.email.converter.outgoing;

import static ru.dlabs.library.email.util.HttpUtils.CONTENT_TYPE_HDR;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.util.ByteArrayDataSource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import ru.dlabs.library.email.dto.message.common.ContentMessage;
import ru.dlabs.library.email.dto.message.common.EmailAttachment;
import ru.dlabs.library.email.dto.message.outgoing.OutgoingMessage;
import ru.dlabs.library.email.exception.CreateMessageException;

/**
 * Utility class for converting different parts of a message {@link OutgoingMessage} for using in
 * a message DTO implement {@link jakarta.mail.Message} class.
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-25</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@UtilityClass
public class JakartaMessagePartConverter {

    /**
     * Converts all the contents of the {@link OutgoingMessage} to a list of {@link BodyPart}.
     *
     * @param message an instance of the {@link OutgoingMessage}
     *
     * @throws CreateMessageException
     */
    public List<BodyPart> convertBodyPart(OutgoingMessage message) throws CreateMessageException {
        if (message == null || message.getContents() == null || message.getContents().isEmpty()) {
            return Collections.emptyList();
        }
        return message.getContents().stream()
            .map(JakartaMessagePartConverter::convertBodyPart)
            .collect(Collectors.toList());
    }

    /**
     * Converts content from {@link OutgoingMessage} to an instance the {@link BodyPart}.
     *
     * @param content a message content. An instance of the {@link ContentMessage}
     *
     * @return an instance of the {@link BodyPart} to uses in the {@link jakarta.mail.Message}.
     *
     * @throws CreateMessageException
     */
    public BodyPart convertBodyPart(ContentMessage content) throws CreateMessageException {
        if (content == null) {
            return null;
        }
        try {
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(content.getData());
            messageBodyPart.setHeader(CONTENT_TYPE_HDR, content.getContentType());
            return messageBodyPart;
        } catch (MessagingException ex) {
            throw new CreateMessageException(
                "Body part couldn't be created due to the following error: " + ex.getMessage(),
                ex
            );
        }
    }

    public List<BodyPart> convertAttachmentParts(OutgoingMessage message) throws CreateMessageException {
        if (message == null || message.getAttachments() == null || message.getAttachments().isEmpty()) {
            return Collections.emptyList();
        }
        return message.getAttachments()
            .stream()
            .map(JakartaMessagePartConverter::convertAttachmentPart)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    public BodyPart convertAttachmentPart(EmailAttachment attachment) throws CreateMessageException {
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
                "Body part couldn't be created due to the following error: " + e.getMessage(),
                e
            );
        }
    }
}
