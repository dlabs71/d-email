package ru.dlabs.library.email.tests.client.receiver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import ru.dlabs.library.email.DEmailReceiver;
import ru.dlabs.library.email.DEmailSender;
import ru.dlabs.library.email.tests.client.sender.SenderTestUtils;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;
import ru.dlabs.library.email.dto.message.incoming.MessageView;
import ru.dlabs.library.email.dto.pageable.PageResponse;
import ru.dlabs.library.email.property.ImapProperties;
import ru.dlabs.library.email.support.AbstractTestsClass;

/**
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-08-31
 */
@Order(323)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IMAPClientCheckTests extends AbstractTestsClass {

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
        DEmailReceiver.of(this.simpleImapProperties)
            .clearCurrentFolder();
        this.emailSender.sendText(email, "Тестовое сообщение 1", "Содержание тестового сообщения 1");
        this.emailSender.sendText(email, "Тестовое сообщение 2", "Содержание тестового сообщения 2");
        this.emailSender.sendText(email, "Тестовое сообщение 3", "Содержание тестового сообщения 3");
        Thread.sleep(sendDelayAfter);
    }

    @Test
    public void checkSimpleEmailTest() {
        PageResponse<MessageView> response = DEmailReceiver.of(this.simpleImapProperties)
            .checkEmail();
        Assertions.assertEquals(response.getTotalCount(), COUNT_OF_MESSAGES);
        Assertions.assertEquals(response.getData().size(), COUNT_OF_MESSAGES);

        MessageView incomingMessage = response.getData().get(0);
        checkMessage(incomingMessage);
    }

    @Test
    public void checkSSLEmailTest() {
        PageResponse<MessageView> response = DEmailReceiver.of(this.sslImapProperties)
            .checkEmail();
        Assertions.assertEquals(response.getTotalCount(), COUNT_OF_MESSAGES);
        Assertions.assertEquals(response.getData().size(), COUNT_OF_MESSAGES);

        MessageView incomingMessage = response.getData().get(0);
        checkMessage(incomingMessage);
    }

    @Test
    public void checkTLSEmailTest() {
        PageResponse<MessageView> response = DEmailReceiver.of(this.tlsImapProperties)
            .checkEmail();
        Assertions.assertEquals(response.getTotalCount(), COUNT_OF_MESSAGES);
        Assertions.assertEquals(response.getData().size(), COUNT_OF_MESSAGES);

        MessageView incomingMessage = response.getData().get(0);
        checkMessage(incomingMessage);
    }

    private void checkMessage(MessageView incomingMessage) {
        assertNotNull(incomingMessage.getSender());
        assertNotNull(incomingMessage.getTransferEncoder());
        assertNotNull(incomingMessage.getSubject());
        assertNotNull(incomingMessage.getId());
        assertNotNull(incomingMessage.getReceivedDate());
        assertNotNull(incomingMessage.getSentDate());
        assertNotNull(incomingMessage.getSize());
        assertNotNull(incomingMessage.getRecipients());
        assertEquals(incomingMessage.getRecipients().size(), 1);

        assertNotNull(incomingMessage.getContents());
        assertEquals(incomingMessage.getContents().size(), 0);
        assertNotNull(incomingMessage.getAttachments());
        assertEquals(incomingMessage.getAttachments().size(), 0);

        EmailParticipant recipient = incomingMessage.getRecipients().stream().findFirst().orElse(null);
        assertNotNull(recipient);
        assertEquals(recipient.getEmail(), this.recipientEmail);
        assertEquals(incomingMessage.getSender().getEmail(), this.senderEmail);
    }
}
