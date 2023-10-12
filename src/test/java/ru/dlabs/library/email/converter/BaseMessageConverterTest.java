package ru.dlabs.library.email.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static ru.dlabs.library.email.util.EmailMessageUtils.CONTENT_TYPE_HDR;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.mail.Address;
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
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import ru.dlabs.library.email.dto.message.common.BaseMessage;
import ru.dlabs.library.email.dto.message.common.EmailAttachment;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;
import ru.dlabs.library.email.util.AttachmentUtils;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-12</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
public class BaseMessageConverterTest {

    @SneakyThrows
    @Test
    public void convertEnvelopDataTest() {
        String subject = "Captain Flint's Map";
        String content = "Treasure Island";

        MimeMessage message = this.createMessage(subject, content);
        BaseMessage baseMessage = BaseMessageConverter.convertEnvelopData(message);

        assertEquals(baseMessage.getId(), message.getMessageNumber());
        assertEquals(baseMessage.getSize(), message.getSize());

        if (baseMessage.getSentDate() == null) {
            assertNull(message.getSentDate());
        } else {
            Long sendDateAfterConvert = baseMessage.getSentDate()
                .atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
            Long sendDateBeforeConvert = message.getSentDate().getTime();
            assertEquals(sendDateAfterConvert, sendDateBeforeConvert);
        }

        if (baseMessage.getReceivedDate() == null) {
            assertNull(message.getReceivedDate());
        } else {
            Long sendDateAfterConvert = baseMessage.getReceivedDate().toEpochSecond(ZoneOffset.UTC);
            Long sendDateBeforeConvert = message.getReceivedDate().getTime();
            assertEquals(sendDateAfterConvert, sendDateBeforeConvert);
        }

//        assertEquals(baseMessage.getEncoding(), message.getEncoding()); ?????????
        assertEquals(baseMessage.getSubject(), subject);

        assertNotNull(baseMessage.getSender());
        InternetAddress from = (InternetAddress) message.getFrom()[0];
        assertEquals(baseMessage.getSender().getEmail(), from.getAddress());
        assertEquals(baseMessage.getSender().getName(), from.getPersonal());

        for (Address address : message.getRecipients(Message.RecipientType.TO)) {
            InternetAddress internetAddress = (InternetAddress) address;
            EmailParticipant participant = baseMessage.getRecipients().stream()
                .filter(item -> item.getEmail().equals(internetAddress.getAddress()))
                .findFirst()
                .orElse(null);
            assertNotNull(participant);
            assertEquals(participant.getEmail(), internetAddress.getAddress());
            assertEquals(participant.getName(), internetAddress.getPersonal());
        }
    }

    @SneakyThrows
    private MimeMessage createMessage(String subject, String textContent) {
        MimeMessage mimeMessage = new MimeMessage((Session) null);
        mimeMessage.addFrom(
            new InternetAddress[] {
                createAddress("john007@island.com", "John Silver"),
                createAddress("smollet@island.com", "Captain Smollett")
            }
        );
        mimeMessage.setRecipients(
            Message.RecipientType.TO,
            new InternetAddress[] {
                createAddress("billy@island.com", "Billy Bones"),
                createAddress("livesey@island.com", "Dr. Livesey"),
                createAddress("pew@island.com", "Blind Pew")
            }
        );
        mimeMessage.setSentDate(new Date());
        mimeMessage.setSubject(subject, StandardCharsets.UTF_8.name());
        mimeMessage.addHeader("Custom-header-1", "data-custom-header-1");
        mimeMessage.addHeader("Custom-header-2", "данные-второго-заголовка");

        MimeMultipart content = new MimeMultipart();

        ByteArrayInputStream is = new ByteArrayInputStream(textContent.getBytes(StandardCharsets.UTF_8));
        MimeBodyPart part = new MimeBodyPart(is);
        part.addHeader("Content-Type", "text/plain; charset=windows-1251");
        content.addBodyPart(part);
        content.addBodyPart(this.createAttachment("classpath:attachments/file.doc"));
        content.addBodyPart(this.createAttachment("classpath:attachments/file.png"));
        content.addBodyPart(this.createAttachment("classpath:attachments/file.zip"));

        mimeMessage.setContent(content);

//        mimeMessage.getEncoding()
        return mimeMessage;
    }

    @SneakyThrows
    private BodyPart createAttachment(String path) {
        EmailAttachment attachment = AttachmentUtils.create(path);

        MimeBodyPart attachmentPart = new MimeBodyPart();
        DataSource dataSource = new ByteArrayDataSource(attachment.getData(), attachment.getContentType());
        attachmentPart.setDataHandler(new DataHandler(dataSource));
        attachmentPart.setFileName(attachment.getName());
        attachmentPart.setHeader(CONTENT_TYPE_HDR, attachment.getContentType());
        return attachmentPart;
    }

    @SneakyThrows
    private InternetAddress createAddress(String email, String name) {
        return new InternetAddress(email, name);
    }

}
