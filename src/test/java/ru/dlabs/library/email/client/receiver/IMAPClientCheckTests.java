package ru.dlabs.library.email.client.receiver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import ru.dlabs.library.email.client.sender.DEmailSender;
import ru.dlabs.library.email.dto.message.MessageView;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;
import ru.dlabs.library.email.dto.pageable.PageResponse;
import ru.dlabs.library.email.property.ImapProperties;

/**
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-08-31
 */
@Order(323)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IMAPClientCheckTests {

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

        this.senderEmail = this.emailSender.sender().getEmail();
        this.recipientEmail = ReceiveTestUtils.getDefaultEmail(this.simpleImapProperties);

        this.sendData(this.recipientEmail);
    }

    @SneakyThrows
    private void sendData(String email) {
        DEmailReceiver.of(this.simpleImapProperties).clearCurrentFolder();
        this.emailSender.sendText(email, "Тестовое сообщение 1", "Содержание тестового сообщения 1");
        this.emailSender.sendText(email, "Тестовое сообщение 2", "Содержание тестового сообщения 2");
        this.emailSender.sendText(email, "Тестовое сообщение 3", "Содержание тестового сообщения 3");
        Thread.sleep(delayAfterSend);
    }

    @Test
    public void checkSimpleEmailTest() {
        PageResponse<MessageView> response = DEmailReceiver.of(this.simpleImapProperties).nextCheckEmail();
        Assertions.assertEquals(response.getTotalCount(), COUNT_OF_MESSAGES);
        Assertions.assertEquals(response.getData().size(), COUNT_OF_MESSAGES);

        MessageView incomingMessage = response.getData().get(0);
        assertNotNull(incomingMessage.getSender());
        assertNotNull(incomingMessage.getContentType());
        assertNotNull(incomingMessage.getEncoding());
        assertNotNull(incomingMessage.getSubject());
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
    }

    @Test
    public void checkSSLEmailTest() {
        PageResponse<MessageView> response = DEmailReceiver.of(this.sslImapProperties).nextCheckEmail();
        Assertions.assertEquals(response.getTotalCount(), COUNT_OF_MESSAGES);
        Assertions.assertEquals(response.getData().size(), COUNT_OF_MESSAGES);

        MessageView incomingMessage = response.getData().get(0);
        assertNotNull(incomingMessage.getSender());
        assertNotNull(incomingMessage.getContentType());
        assertNotNull(incomingMessage.getEncoding());
        assertNotNull(incomingMessage.getSubject());
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
    }

    @Test
    public void checkTLSEmailTest() {
        PageResponse<MessageView> response = DEmailReceiver.of(this.tlsImapProperties).nextCheckEmail();
        Assertions.assertEquals(response.getTotalCount(), COUNT_OF_MESSAGES);
        Assertions.assertEquals(response.getData().size(), COUNT_OF_MESSAGES);

        MessageView incomingMessage = response.getData().get(0);
        assertNotNull(incomingMessage.getSender());
        assertNotNull(incomingMessage.getContentType());
        assertNotNull(incomingMessage.getEncoding());
        assertNotNull(incomingMessage.getSubject());
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
    }
}
