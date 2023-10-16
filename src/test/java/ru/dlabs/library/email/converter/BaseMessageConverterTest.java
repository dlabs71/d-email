package ru.dlabs.library.email.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.time.ZoneId;
import java.time.ZoneOffset;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import ru.dlabs.library.email.dto.message.common.BaseMessage;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;

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

    /**
     * The test for:
     * <ul>
     *     <li>{@link BaseMessageConverter#convertEnvelopData(Message)}</li>
     * </ul>
     */
    @Test
    public void convertEnvelopDataTest_1() {
        String subject = "Captain Flint's Map";
        String content = "Treasure Island";

        MimeMessage message = TestConverterUtils.createMessage(subject, content);
        BaseMessage baseMessage = BaseMessageConverter.convertEnvelopData(message);
        this.assertMessages(baseMessage, message, subject);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link BaseMessageConverter#convertEnvelopData(Message)}</li>
     * </ul>
     */
    @Test
    public void convertEnvelopDataTest_2() {
        MimeMessage message = TestConverterUtils.createEmptyMessage();
        BaseMessage baseMessage = BaseMessageConverter.convertEnvelopData(message);
        this.assertMessages(baseMessage, message, null);
    }

    @SneakyThrows
    private void assertMessages(BaseMessage baseMessage, MimeMessage message, String subject) {
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

        assertEquals(baseMessage.getSubject(), subject);
        if (message.getEncoding() != null) {
            assertEquals(baseMessage.getTransferEncoder().getName(), message.getEncoding());
        } else {
            assertNull(baseMessage.getTransferEncoder());
        }

        if (message.getFrom() != null) {
            assertNotNull(baseMessage.getSender());
            InternetAddress from = (InternetAddress) message.getFrom()[0];
            assertEquals(baseMessage.getSender().getEmail(), from.getAddress());
            assertEquals(baseMessage.getSender().getName(), from.getPersonal());
        } else {
            assertNull(baseMessage.getSender());
        }

        if (message.getRecipients(Message.RecipientType.TO) != null) {
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
        } else {
            assertNotNull(baseMessage.getRecipients());
            assertEquals(baseMessage.getRecipients().size(), 0);
        }
    }

}
