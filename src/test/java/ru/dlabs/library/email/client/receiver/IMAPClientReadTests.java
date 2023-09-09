package ru.dlabs.library.email.client.receiver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ru.dlabs.library.email.client.sender.DEmailSender;
import ru.dlabs.library.email.dto.message.api.IncomingMessage;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;
import ru.dlabs.library.email.dto.pageable.PageResponse;
import ru.dlabs.library.email.property.ImapProperties;

/**
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-08-31
 */
@Order(34)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class IMAPClientReadTests {

    private final static Integer COUNT_OF_MESSAGES = 3;
    private final static Integer delayAfterSend = 1000;

    private ImapProperties sslImapProperties;
    private ImapProperties tlsImapProperties;
    private ImapProperties simpleImapProperties;

    private DEmailSender emailSender;
    private String recipientEmail;
    private String senderEmail;

    @BeforeEach
    public void loadConfig() throws IOException {
        ImapProperties[] properties = ReceiveTestUtils.loadProperties();
        this.sslImapProperties = properties[0];
        this.tlsImapProperties = properties[1];
        this.simpleImapProperties = properties[2];
        this.emailSender = ReceiveTestUtils.createSender();
        this.recipientEmail = this.emailSender.sender().getEmail();

        this.senderEmail = ReceiveTestUtils.getDefaultEmail(this.simpleImapProperties);
        this.sendData(this.senderEmail);
    }

    @SneakyThrows
    private void sendData(String email) {
        DEmailReceiver.of(this.simpleImapProperties).clearCurrentFolder();
        this.emailSender.sendText(email, "Тестовое сообщение 1", "Содержание тестового сообщения");
        this.emailSender.sendText(email, "Тестовое сообщение 2", "Содержание тестового сообщения");
        this.emailSender.sendText(email, "Тестовое сообщение 3", "Содержание тестового сообщения");
        Thread.sleep(delayAfterSend);
    }

    @Test
    public void readSimpleEmailTest() {
        PageResponse<IncomingMessage> response = DEmailReceiver.of(this.simpleImapProperties).nextReadEmail();
        assertEquals(response.getTotalCount(), COUNT_OF_MESSAGES);
        assertEquals(response.getData().size(), COUNT_OF_MESSAGES);

        IncomingMessage incomingMessage = response.getData().get(0);
        assertNotNull(incomingMessage.getSender());
        assertNotNull(incomingMessage.getContentType());
        assertNotNull(incomingMessage.getEncoding());
        assertNotNull(incomingMessage.getSubject());
        assertNotNull(incomingMessage.getContent());
        assertNotNull(incomingMessage.getId());
        assertNotNull(incomingMessage.getReceivedDate());
        assertNotNull(incomingMessage.getSentDate());
        assertNotNull(incomingMessage.getSize());
        assertNotNull(incomingMessage.getRecipientEmail());
        assertEquals(incomingMessage.getRecipientEmail().size(), 1);

        EmailParticipant recipient = incomingMessage.getRecipientEmail().stream().findFirst().orElse(null);
        assertNotNull(recipient);
        assertEquals(recipient.getEmail(), this.recipientEmail);
        assertEquals(incomingMessage.getSender().getEmail(), this.senderEmail);

        response.getData().forEach(message -> {
            assertEquals(message.getContent(), "Содержание тестового сообщения");
        });
    }

    @Test
    public void readSSLEmailTest() {
        PageResponse<IncomingMessage> response = DEmailReceiver.of(this.sslImapProperties).nextReadEmail();
        assertEquals(response.getTotalCount(), COUNT_OF_MESSAGES);
        assertEquals(response.getData().size(), COUNT_OF_MESSAGES);

        IncomingMessage incomingMessage = response.getData().get(0);
        assertNotNull(incomingMessage.getSender());
        assertNotNull(incomingMessage.getContentType());
        assertNotNull(incomingMessage.getEncoding());
        assertNotNull(incomingMessage.getSubject());
        assertNotNull(incomingMessage.getContent());
        assertNotNull(incomingMessage.getId());
        assertNotNull(incomingMessage.getReceivedDate());
        assertNotNull(incomingMessage.getSentDate());
        assertNotNull(incomingMessage.getSize());
        assertNotNull(incomingMessage.getRecipientEmail());
        assertEquals(incomingMessage.getRecipientEmail().size(), 1);

        EmailParticipant recipient = incomingMessage.getRecipientEmail().stream().findFirst().orElse(null);
        assertNotNull(recipient);
        assertEquals(recipient.getEmail(), this.recipientEmail);
        assertEquals(incomingMessage.getSender().getEmail(), this.senderEmail);

        response.getData().forEach(message -> {
            assertEquals(message.getContent(), "Содержание тестового сообщения");
        });
    }

    @Test
    public void readTLSEmailTest() {
        PageResponse<IncomingMessage> response = DEmailReceiver.of(this.tlsImapProperties).nextReadEmail();
        assertEquals(response.getTotalCount(), COUNT_OF_MESSAGES);
        assertEquals(response.getData().size(), COUNT_OF_MESSAGES);

        IncomingMessage incomingMessage = response.getData().get(0);
        assertNotNull(incomingMessage.getSender());
        assertNotNull(incomingMessage.getContentType());
        assertNotNull(incomingMessage.getEncoding());
        assertNotNull(incomingMessage.getSubject());
        assertNotNull(incomingMessage.getContent());
        assertNotNull(incomingMessage.getId());
        assertNotNull(incomingMessage.getReceivedDate());
        assertNotNull(incomingMessage.getSentDate());
        assertNotNull(incomingMessage.getSize());
        assertNotNull(incomingMessage.getRecipientEmail());
        assertEquals(incomingMessage.getRecipientEmail().size(), 1);

        EmailParticipant recipient = incomingMessage.getRecipientEmail().stream().findFirst().orElse(null);
        assertNotNull(recipient);
        assertEquals(recipient.getEmail(), this.recipientEmail);
        assertEquals(incomingMessage.getSender().getEmail(), this.senderEmail);

        response.getData().forEach(message -> {
            assertEquals(message.getContent(), "Содержание тестового сообщения");
        });
    }
}
