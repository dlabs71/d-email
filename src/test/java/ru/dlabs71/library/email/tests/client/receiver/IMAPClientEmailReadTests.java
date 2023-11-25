package ru.dlabs71.library.email.tests.client.receiver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import ru.dlabs71.library.email.DEmailReceiver;
import ru.dlabs71.library.email.DEmailSender;
import ru.dlabs71.library.email.dto.message.common.EmailParticipant;
import ru.dlabs71.library.email.dto.message.incoming.DefaultIncomingMessage;
import ru.dlabs71.library.email.dto.message.incoming.IncomingMessage;
import ru.dlabs71.library.email.dto.message.incoming.MessageView;
import ru.dlabs71.library.email.dto.pageable.PageResponse;
import ru.dlabs71.library.email.property.ImapProperties;
import ru.dlabs71.library.email.support.AbstractTestsClass;
import ru.dlabs71.library.email.tests.client.receiver.utils.ReceiveTestUtils;
import ru.dlabs71.library.email.tests.client.sender.utils.SenderTestUtils;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-08-31</div>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Order(424)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IMAPClientEmailReadTests extends AbstractTestsClass {

    private final static Integer COUNT_OF_MESSAGES = 3;

    private ImapProperties sslImapProperties;
    private ImapProperties tlsImapProperties;
    private ImapProperties simpleImapProperties;

    private DEmailSender emailSender;
    private String recipientEmail;
    private String senderEmail;

    @BeforeAll
    public void loadConfig() {
        ImapProperties[] properties = ReceiveTestUtils.loadProperties();
        this.sslImapProperties = properties[0];
        this.tlsImapProperties = properties[1];
        this.simpleImapProperties = properties[2];
        this.emailSender = SenderTestUtils.createSender();

        this.senderEmail = this.emailSender.sender().getEmail();
        this.recipientEmail = ReceiveTestUtils.getDefaultEmail(this.simpleImapProperties);
    }

    @BeforeEach
    @SneakyThrows
    public void sendData() {
        DEmailReceiver.of(this.simpleImapProperties)
            .clearCurrentFolder();
        this.emailSender.sendText(this.recipientEmail, "Тестовое сообщение 1", "Содержание тестового сообщения");
        this.emailSender.sendText(this.recipientEmail, "Тестовое сообщение 2", "Содержание тестового сообщения");
        this.emailSender.sendText(this.recipientEmail, "Тестовое сообщение 3", "Содержание тестового сообщения");
        Thread.sleep(sendDelayAfter);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link DEmailReceiver#readEmail()}</li>
     * </ul>
     * <p>
     *  Using of the simple type connection (NO SSL and NO TLS)
     */
    @Test
    @Order(1)
    public void readSimpleEmailTest() {
        PageResponse<IncomingMessage> response = DEmailReceiver.of(this.simpleImapProperties)
            .readEmail();
        assertEquals(COUNT_OF_MESSAGES, response.getTotalCount());
        assertEquals(COUNT_OF_MESSAGES, response.getData().size());

        response.getData().forEach(item -> {
            assertInstanceOf(DefaultIncomingMessage.class, item);
            DefaultIncomingMessage incomingMessage = (DefaultIncomingMessage) item;
            assertMessage(incomingMessage);
        });
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link DEmailReceiver#readEmail()}</li>
     * </ul>
     * <p>
     *  Using of the SSL connection
     */
    @Test
    @Order(2)
    public void readSSLEmailTest() {
        PageResponse<IncomingMessage> response = DEmailReceiver.of(this.sslImapProperties)
            .readEmail();
        assertEquals(COUNT_OF_MESSAGES, response.getTotalCount());
        assertEquals(COUNT_OF_MESSAGES, response.getData().size());

        response.getData().forEach(item -> {
            assertInstanceOf(DefaultIncomingMessage.class, item);
            DefaultIncomingMessage incomingMessage = (DefaultIncomingMessage) item;
            assertMessage(incomingMessage);
        });
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link DEmailReceiver#readEmail()}</li>
     * </ul>
     * <p>
     *  Using of the TLS connection
     */
    @Test
    @Order(3)
    public void readTLSEmailTest() {
        PageResponse<IncomingMessage> response = DEmailReceiver.of(this.tlsImapProperties)
            .readEmail();
        assertEquals(COUNT_OF_MESSAGES, response.getTotalCount());
        assertEquals(COUNT_OF_MESSAGES, response.getData().size());

        response.getData().forEach(item -> {
            assertInstanceOf(DefaultIncomingMessage.class, item);
            DefaultIncomingMessage incomingMessage = (DefaultIncomingMessage) item;
            assertMessage(incomingMessage);
        });
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link DEmailReceiver#readMessageById(Integer)} ()}</li>
     * </ul>
     * <p>
     *  Checking seen flag
     */
    @Test
    @Order(4)
    public void seenMessagesTest() throws InterruptedException {
        DEmailReceiver.of(this.simpleImapProperties)
            .clearCurrentFolder();
        this.emailSender.sendText(this.recipientEmail, "Тестовое сообщение 1", "Содержание тестового сообщения");
        Thread.sleep(sendDelayAfter);
        DEmailReceiver client = DEmailReceiver.of(this.sslImapProperties);

        PageResponse<MessageView> checkEmailResponse1 = client.checkEmail();
        MessageView messageView1 = checkEmailResponse1.getData().get(0);

        client.readMessageById(messageView1.getId());

        PageResponse<MessageView> checkEmailResponse2 = client.checkEmail();
        MessageView messageView2 = checkEmailResponse2.getData().get(0);

        assertEquals(messageView1.getId(), messageView2.getId());
        assertFalse(messageView1.isSeen());
        assertTrue(messageView2.isSeen());
    }

    private void assertMessage(DefaultIncomingMessage incomingMessage) {
        assertNotNull(incomingMessage.getSender());
        assertNotNull(incomingMessage.getTransferEncoder());
        assertNotNull(incomingMessage.getSubject());
        assertNotNull(incomingMessage.getContents());
        assertNotNull(incomingMessage.getId());
        assertNotNull(incomingMessage.getReceivedDate());
        assertNotNull(incomingMessage.getSentDate());
        assertNotNull(incomingMessage.getSize());
        assertNotNull(incomingMessage.getRecipients());
        assertEquals(incomingMessage.getRecipients().size(), 1);


        EmailParticipant recipient = incomingMessage.getRecipients().stream().findFirst().orElse(null);
        assertNotNull(recipient);
        assertEquals(this.recipientEmail, recipient.getEmail());
        assertEquals(this.senderEmail, incomingMessage.getSender().getEmail());
        assertNotNull(incomingMessage.getContents());
        assertEquals(1, incomingMessage.getContents().size());
        assertEquals("Содержание тестового сообщения", incomingMessage.getContents().get(0).getData());
        assertEquals("Содержание тестового сообщения", incomingMessage.getAllContentsAsString());
    }
}
