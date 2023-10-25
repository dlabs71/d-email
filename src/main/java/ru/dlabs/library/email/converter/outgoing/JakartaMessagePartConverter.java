package ru.dlabs.library.email.converter.outgoing;

import static ru.dlabs.library.email.util.HttpUtils.CONTENT_TYPE_HDR;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.BodyPart;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.util.ByteArrayDataSource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import ru.dlabs.library.email.dto.message.common.ContentMessage;
import ru.dlabs.library.email.dto.message.common.EmailAttachment;
import ru.dlabs.library.email.dto.message.outgoing.OutgoingMessage;
import ru.dlabs.library.email.exception.CreateMessageException;

/**
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

    public List<BodyPart> createBodyPart(OutgoingMessage message) throws CreateMessageException {
        return message.getContents().stream().map(JakartaMessagePartConverter::createBodyPart).collect(Collectors.toList());
    }

    public BodyPart createBodyPart(ContentMessage content) throws CreateMessageException {
        try {
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(content.getData());
            messageBodyPart.setHeader(CONTENT_TYPE_HDR, content.getContentType());
            return messageBodyPart;
        } catch (MessagingException ex) {
            throw new CreateMessageException(
                "Body part couldn't be created due to the following error: " + ex.getLocalizedMessage(),
                ex
            );
        }
    }

    public List<BodyPart> createAttachmentParts(OutgoingMessage message) throws CreateMessageException {
        if (message.getAttachments() == null || message.getAttachments().isEmpty()) {
            return null;
        }
        return message.getAttachments()
            .stream()
            .map(JakartaMessagePartConverter::createAttachmentPart)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    public BodyPart createAttachmentPart(EmailAttachment attachment) throws CreateMessageException {
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
                "Body part couldn't be created due to the following error: " + e.getLocalizedMessage(),
                e
            );
        }
    }
}
