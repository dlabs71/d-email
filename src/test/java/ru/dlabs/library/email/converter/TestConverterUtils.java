package ru.dlabs.library.email.converter;

import static ru.dlabs.library.email.util.HttpUtils.CONTENT_TRANSFER_ENCODING_HDR;
import static ru.dlabs.library.email.util.HttpUtils.CONTENT_TYPE_HDR;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.mail.util.ByteArrayDataSource;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import ru.dlabs.library.email.dto.message.common.EmailAttachment;
import ru.dlabs.library.email.dto.message.common.TransferEncoder;
import ru.dlabs.library.email.util.AttachmentUtils;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-15</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@UtilityClass
public class TestConverterUtils {

    public MimeMessage createMessage(String subject, String textContent) {
        return constructMessage(
            subject,
            textContent,
            new InternetAddress[] {
                createAddress("john007@island.com", "John Silver"),
                createAddress("smollet@island.com", "Captain Smollett")
            },
            new InternetAddress[] {
                createAddress("billy@island.com", "Billy Bones"),
                createAddress("livesey@island.com", "Dr. Livesey"),
                createAddress("pew@island.com", "Blind Pew")
            },
            null
        );
    }

    public MimeMessage createMessageWithAttachments(String subject, String textContent, List<String> paths) {
        return constructMessage(
            subject,
            textContent,
            new InternetAddress[] {
                createAddress("john007@island.com", "John Silver"),
                createAddress("smollet@island.com", "Captain Smollett")
            },
            new InternetAddress[] {
                createAddress("billy@island.com", "Billy Bones"),
                createAddress("livesey@island.com", "Dr. Livesey"),
                createAddress("pew@island.com", "Blind Pew")
            },
            paths
        );
    }

    public MimeMessage createEmptyMessage() {
        return new MimeMessage((Session) null);
    }

    @SneakyThrows
    public MimeMessage constructMessage(
        String subject,
        String textContent,
        InternetAddress[] from,
        InternetAddress[] to,
        List<String> attachments
    ) {
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        if (from != null) {
            mimeMessage.addFrom(from);
        }
        if (to != null) {
            mimeMessage.setRecipients(Message.RecipientType.TO, to);
        }
        mimeMessage.setSentDate(new Date());
        mimeMessage.setSubject(subject, StandardCharsets.UTF_8.name());
        mimeMessage.addHeader("Custom-header-1", "data-custom-header-1");
        mimeMessage.addHeader("Custom-header-2", "данные-второго-заголовка");

        MimeMultipart content = new MimeMultipart();
        if (textContent == null && (attachments == null || attachments.isEmpty())) {
            return mimeMessage;
        }

        if (textContent != null) {
            ByteArrayInputStream is = new ByteArrayInputStream(textContent.getBytes(StandardCharsets.UTF_8));

            MimeBodyPart part = new MimeBodyPart(is);
            part.addHeader(CONTENT_TYPE_HDR, "text/plain; charset=windows-1251");
            content.addBodyPart(part);
        }

        if (attachments != null) {
            for (String attachment : attachments) {
                content.addBodyPart(createAttachment(attachment));
            }
        }

        mimeMessage.setContent(content);
        mimeMessage.addHeader(CONTENT_TRANSFER_ENCODING_HDR, TransferEncoder.EIGHT_BIT.getName());
        return mimeMessage;
    }

    @SneakyThrows
    public BodyPart createAttachment(String path) {
        EmailAttachment attachment = AttachmentUtils.create(path);

        MimeBodyPart attachmentPart = new MimeBodyPart();
        DataSource dataSource = new ByteArrayDataSource(attachment.getData(), attachment.getContentType());
        attachmentPart.setDataHandler(new DataHandler(dataSource));
        attachmentPart.setFileName(attachment.getName());
        attachmentPart.setHeader(CONTENT_TYPE_HDR, attachment.getContentType());
        return attachmentPart;
    }

    @SneakyThrows
    public InternetAddress createAddress(String email, String name) {
        return new InternetAddress(email, name);
    }
}
