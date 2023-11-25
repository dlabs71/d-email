package ru.dlabs71.library.email.tests.converter.incoming.utils;

import static ru.dlabs71.library.email.util.ProtocolUtils.CONTENT_TRANSFER_ENCODING_HDR;
import static ru.dlabs71.library.email.util.ProtocolUtils.CONTENT_TYPE_HDR;

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
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import ru.dlabs71.library.email.dto.message.common.EmailAttachment;
import ru.dlabs71.library.email.type.TransferEncoder;
import ru.dlabs71.library.email.util.AttachmentUtils;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-15</div>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@UtilityClass
public class TestConverterUtils {

    public MimeMessage createSimpleMessage(String subject, String textContent) {
        return constructMessage(
            subject,
            textContent == null ? null : Arrays.asList(textContent),
            null,
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
            textContent == null ? null : Arrays.asList(textContent),
            null,
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

    public MimeMessage createMessageWithoutSenders(String subject, String textContent) {
        return constructMessage(
            subject,
            Arrays.asList(textContent),
            null,
            new InternetAddress[] { },
            new InternetAddress[] {
                createAddress("billy@island.com", "Billy Bones"),
                createAddress("livesey@island.com", "Dr. Livesey"),
                createAddress("pew@island.com", "Blind Pew")
            },
            null
        );
    }

    public MimeMessage createMessageWithHtml(String subject, String textContent, String htmlContent) {
        return createMessageWithMultipleContent(subject, Arrays.asList(textContent), Arrays.asList(htmlContent));
    }

    public MimeMessage createMessageWithMultipleTextContent(String subject, List<String> textContent) {
        return createMessageWithMultipleContent(subject, textContent, null);
    }

    public MimeMessage createMessageWithMultipleHtmlContent(String subject, List<String> htmlContent) {
        return createMessageWithMultipleContent(subject, null, htmlContent);
    }

    public MimeMessage createMessageWithMultipleContent(
        String subject,
        List<String> textContents,
        List<String> htmlContents
    ) {
        return constructMessage(
            subject,
            textContents,
            htmlContents,
            new InternetAddress[] {
                createAddress("john007@island.com", "John Silver"),
                },
            new InternetAddress[] {
                createAddress("billy@island.com", "Billy Bones"),
                createAddress("livesey@island.com", "Dr. Livesey"),
                createAddress("pew@island.com", "Blind Pew")
            },
            null
        );
    }

    @SneakyThrows
    public MimeMessage constructMessage(
        String subject,
        List<String> textContents,
        List<String> htmlContents,
        InternetAddress[] from,
        InternetAddress[] to,
        List<String> attachments
    ) {
        MimeMessage mimeMessage = new TestReceivedMessage();
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
        if ((textContents == null || textContents.isEmpty())
            && (htmlContents == null || htmlContents.isEmpty())
            && (attachments == null || attachments.isEmpty())
        ) {
            mimeMessage.setHeader(CONTENT_TYPE_HDR, content.getContentType());
            return mimeMessage;
        }

        if (textContents != null) {
            for (String textContent : textContents) {
                content.addBodyPart(createContent(textContent, "plain"));
            }
        }

        if (htmlContents != null) {
            for (String htmlContent : htmlContents) {
                content.addBodyPart(createContent(htmlContent, "html"));
            }
        }

        if (attachments != null) {
            for (String attachment : attachments) {
                content.addBodyPart(createAttachment(attachment));
            }
        }

        mimeMessage.setContent(content);
        mimeMessage.addHeader(CONTENT_TRANSFER_ENCODING_HDR, TransferEncoder.EIGHT_BIT.getName());
        mimeMessage.addHeader(CONTENT_TYPE_HDR, content.getContentType());
        return mimeMessage;
    }

    @SneakyThrows
    public BodyPart createContent(String content, String subtype) {
        MimeBodyPart part = new MimeBodyPart();
        part.setText(content, StandardCharsets.UTF_8.displayName(), subtype);
        part.addHeader(
            CONTENT_TYPE_HDR,
            "text/" + subtype + "; charset=" + StandardCharsets.UTF_8.displayName()
        );
        return part;
    }

    @SneakyThrows
    public BodyPart createAttachment(String path) {
        EmailAttachment attachment = AttachmentUtils.create(path);

        MimeBodyPart attachmentPart = new MimeBodyPart();
        DataSource dataSource = new ByteArrayDataSource(attachment.getData(), attachment.getContentType());
        attachmentPart.setDataHandler(new DataHandler(dataSource));
        attachmentPart.setFileName(attachment.getName());
        attachmentPart.addHeader(CONTENT_TYPE_HDR, attachment.getContentType());
        return attachmentPart;
    }

    @SneakyThrows
    public InternetAddress createAddress(String email, String name) {
        return new InternetAddress(email, name);
    }
}
