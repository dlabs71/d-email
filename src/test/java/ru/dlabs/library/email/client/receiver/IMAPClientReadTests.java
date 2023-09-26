package ru.dlabs.library.email.client.receiver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import ru.dlabs.library.email.DEmailReceiver;
import ru.dlabs.library.email.DEmailSender;
import ru.dlabs.library.email.client.sender.SenderTestUtils;
import ru.dlabs.library.email.dto.message.MessageView;
import ru.dlabs.library.email.dto.message.api.IncomingMessage;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;
import ru.dlabs.library.email.dto.pageable.PageResponse;
import ru.dlabs.library.email.property.ImapProperties;
import ru.dlabs.library.email.support.AbstractTestsClass;

/**
 * Project name: d-email
 * <p>
 * Creation date: 2023-08-31
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Order(324)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IMAPClientReadTests extends AbstractTestsClass {

    private final static Integer COUNT_OF_MESSAGES = 3;

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
        this.emailSender = SenderTestUtils.createSender();

        this.senderEmail = this.emailSender.sender().getEmail();
        this.recipientEmail = ReceiveTestUtils.getDefaultEmail(this.simpleImapProperties);

        this.sendData(this.recipientEmail);
    }

    @SneakyThrows
    private void sendData(String email) {
        DEmailReceiver.of(this.simpleImapProperties).clearCurrentFolder();
        this.emailSender.sendText(email, "Тестовое сообщение 1", "Содержание тестового сообщения");
        this.emailSender.sendText(email, "Тестовое сообщение 2", "Содержание тестового сообщения");
        this.emailSender.sendText(email, "Тестовое сообщение 3", "Содержание тестового сообщения");
        Thread.sleep(sendDelayAfter);
    }

    @Test
    @Order(1)
    public void readSimpleEmailTest() {
        PageResponse<IncomingMessage> response = DEmailReceiver.of(this.simpleImapProperties).nextReadEmail();
        assertEquals(response.getTotalCount(), COUNT_OF_MESSAGES);
        assertEquals(response.getData().size(), COUNT_OF_MESSAGES);

        response.getData().forEach(incomingMessage -> {
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
            assertEquals(incomingMessage.getContent(), "Содержание тестового сообщения");
        });
    }

    @Test
    @Order(2)
    public void readSSLEmailTest() {
        PageResponse<IncomingMessage> response = DEmailReceiver.of(this.sslImapProperties).nextReadEmail();
        assertEquals(response.getTotalCount(), COUNT_OF_MESSAGES);
        assertEquals(response.getData().size(), COUNT_OF_MESSAGES);

        response.getData().forEach(incomingMessage -> {
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
            assertEquals(incomingMessage.getContent(), "Содержание тестового сообщения");
        });
    }

    @Test
    @Order(3)
    public void readTLSEmailTest() {
        PageResponse<IncomingMessage> response = DEmailReceiver.of(this.tlsImapProperties).nextReadEmail();
        assertEquals(response.getTotalCount(), COUNT_OF_MESSAGES);
        assertEquals(response.getData().size(), COUNT_OF_MESSAGES);

        response.getData().forEach(incomingMessage -> {
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
            assertEquals(incomingMessage.getContent(), "Содержание тестового сообщения");
        });
    }

    @Test
    @Order(4)
    public void seenMessagesTest() throws InterruptedException {
        DEmailReceiver.of(this.simpleImapProperties).clearCurrentFolder();
        this.emailSender.sendText(this.recipientEmail, "Тестовое сообщение 1", "Содержание тестового сообщения");
        Thread.sleep(sendDelayAfter);
        DEmailReceiver client = DEmailReceiver.of(this.sslImapProperties);

        client.start(0);
        PageResponse<MessageView> checkEmailResponse1 = client.nextCheckEmail();
        MessageView messageView1 = checkEmailResponse1.getData().get(0);

        client.start(0);
        client.readEmail(messageView1.getId());

        client.start(0);
        PageResponse<MessageView> checkEmailResponse2 = client.nextCheckEmail();
        MessageView messageView2 = checkEmailResponse2.getData().get(0);

        assertEquals(messageView1.getId(), messageView2.getId());
        assertFalse(messageView1.isSeen());
        assertTrue(messageView2.isSeen());
    }
}
