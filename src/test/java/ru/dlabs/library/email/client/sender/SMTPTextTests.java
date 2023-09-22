package ru.dlabs.library.email.client.sender;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import ru.dlabs.library.email.DEmailReceiver;
import ru.dlabs.library.email.DEmailSender;
import ru.dlabs.library.email.client.SendingStatus;
import ru.dlabs.library.email.client.receiver.ReceiveTestUtils;
import ru.dlabs.library.email.dto.message.api.IncomingMessage;
import ru.dlabs.library.email.dto.message.common.EmailAttachment;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;
import ru.dlabs.library.email.dto.pageable.PageResponse;
import ru.dlabs.library.email.util.AttachmentUtils;
import ru.dlabs.library.email.util.EmailMessageUtils;

@Order(31)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SMTPTextTests {

    private final static String BODY = "It's the body of this message. Please don't reply to this one.";
    private final static String SUBJECT = "Test of sending";
    private final static EmailAttachment ATTACHMENT_1 = AttachmentUtils.create("classpath:attachments/file.txt");
    private final static EmailAttachment ATTACHMENT_2 = AttachmentUtils.create("classpath:attachments/file.jpg");

    private String recipientEmail1;
    private String recipientEmail2;
    private DEmailReceiver receiver;
    private DEmailSender sender;

    @BeforeEach
    public void loadConfig() {
        Properties props = SenderTestUtils.loadPropertiesFromFile();
        this.recipientEmail1 = props.getProperty("recipientEmail1");
        this.recipientEmail2 = props.getProperty("recipientEmail2");
        this.receiver = ReceiveTestUtils.createReceiver();
        this.sender = SenderTestUtils.createSender();
    }

    @Test
    @Order(1)
    @SneakyThrows
    public void sendTextTest_1() {
        this.receiver
            .credentialId(ReceiveTestUtils.CREDENTIAL_ID_1)
            .clearCurrentFolder();

        SendingStatus result = this.sender.sendText(this.recipientEmail1, SUBJECT, BODY);

        assertEquals(result, SendingStatus.SUCCESS);

        Thread.sleep(1000);
        assertMailbox(ReceiveTestUtils.CREDENTIAL_ID_1, false, false);
    }


    @Test
    @Order(2)
    @SneakyThrows
    public void sendTextTest_2() {
        this.receiver
            .credentialId(ReceiveTestUtils.CREDENTIAL_ID_1)
            .clearCurrentFolder();
        this.receiver
            .credentialId(ReceiveTestUtils.CREDENTIAL_ID_2)
            .clearCurrentFolder();

        SendingStatus result = this.sender.sendText(
            Arrays.asList(this.recipientEmail1, this.recipientEmail2),
            SUBJECT,
            BODY
        );

        assertEquals(result, SendingStatus.SUCCESS);

        Thread.sleep(1000);
        assertMailbox(ReceiveTestUtils.CREDENTIAL_ID_1, true, false);
        assertMailbox(ReceiveTestUtils.CREDENTIAL_ID_2, true, false);
    }

    @Test
    @Order(3)
    @SneakyThrows
    public void sendTextTest_3() {
        this.receiver
            .credentialId(ReceiveTestUtils.CREDENTIAL_ID_1)
            .clearCurrentFolder();
        this.receiver
            .credentialId(ReceiveTestUtils.CREDENTIAL_ID_2)
            .clearCurrentFolder();

        SendingStatus result = this.sender.sendText(
            Arrays.asList(this.recipientEmail1, this.recipientEmail2),
            SUBJECT,
            BODY,
            ATTACHMENT_1,
            ATTACHMENT_2
        );

        assertEquals(result, SendingStatus.SUCCESS);

        Thread.sleep(3000);
        assertMailbox(ReceiveTestUtils.CREDENTIAL_ID_1, true, true);
        assertMailbox(ReceiveTestUtils.CREDENTIAL_ID_2, true, true);
    }

    @Test
    @Order(4)
    @SneakyThrows
    public void sendTextTest_4() {
        this.receiver
            .credentialId(ReceiveTestUtils.CREDENTIAL_ID_1)
            .clearCurrentFolder();
        this.receiver
            .credentialId(ReceiveTestUtils.CREDENTIAL_ID_2)
            .clearCurrentFolder();

        SendingStatus result = this.sender.sendText(
            Arrays.asList(this.recipientEmail1, this.recipientEmail2),
            SUBJECT,
            BODY,
            Arrays.asList(ATTACHMENT_1, ATTACHMENT_2)
        );

        assertEquals(result, SendingStatus.SUCCESS);

        Thread.sleep(3000);
        assertMailbox(ReceiveTestUtils.CREDENTIAL_ID_1, true, true);
        assertMailbox(ReceiveTestUtils.CREDENTIAL_ID_2, true, true);
    }

    @Test
    @Order(5)
    @SneakyThrows
    public void sendTextTest_5() {
        this.receiver
            .credentialId(ReceiveTestUtils.CREDENTIAL_ID_1)
            .clearCurrentFolder();

        SendingStatus result = this.sender.sendText(
            this.recipientEmail1,
            SUBJECT,
            BODY,
            ATTACHMENT_1,
            ATTACHMENT_2
        );

        assertEquals(result, SendingStatus.SUCCESS);

        Thread.sleep(3000);
        assertMailbox(ReceiveTestUtils.CREDENTIAL_ID_1, false, true);
    }

    @Test
    @Order(6)
    @SneakyThrows
    public void sendTextTest_6() {
        this.receiver
            .credentialId(ReceiveTestUtils.CREDENTIAL_ID_1)
            .clearCurrentFolder();

        SendingStatus result = this.sender.sendText(
            this.recipientEmail1,
            SUBJECT,
            BODY,
            Arrays.asList(ATTACHMENT_1, ATTACHMENT_2)
        );

        assertEquals(result, SendingStatus.SUCCESS);

        Thread.sleep(3000);
        assertMailbox(ReceiveTestUtils.CREDENTIAL_ID_1, false, true);
    }

    @Test
    @Order(7)
    @SneakyThrows
    public void sendTextTest_7() {
        this.receiver
            .credentialId(ReceiveTestUtils.CREDENTIAL_ID_1)
            .clearCurrentFolder();
        this.receiver
            .credentialId(ReceiveTestUtils.CREDENTIAL_ID_2)
            .clearCurrentFolder();


        SendingStatus result = this.sender.sendText(
            new HashSet<>(Arrays.asList(
                EmailParticipant.of(this.recipientEmail1),
                EmailParticipant.of(this.recipientEmail2)
            )),
            SUBJECT,
            BODY,
            Arrays.asList(ATTACHMENT_1, ATTACHMENT_2)
        );

        assertEquals(result, SendingStatus.SUCCESS);

        Thread.sleep(3000);
        assertMailbox(ReceiveTestUtils.CREDENTIAL_ID_1, true, true);
        assertMailbox(ReceiveTestUtils.CREDENTIAL_ID_2, true, true);
    }

    private void assertMailbox(String credentialId, boolean twoRecipients, boolean withAttachments) {
        PageResponse<IncomingMessage> inbox = this.receiver
            .start(0)
            .credentialId(credentialId)
            .nextReadEmail();
        assertEquals(inbox.getData().size(), 1);

        IncomingMessage incomingMessage = inbox.getData().get(0);
        assertIncomingMessage(incomingMessage);
        if (twoRecipients) {
            assertIncomingMessageRecipients_2(incomingMessage);
        } else {
            assertIncomingMessageRecipients_1(incomingMessage);
        }

        if (withAttachments) {
            assertIncomingMessageAttachments(incomingMessage);
        }
    }

    private void assertIncomingMessage(IncomingMessage incomingMessage) {
        assertEquals(incomingMessage.getSender(), this.sender.sender());
        assertEquals(incomingMessage.getSender().getName(), this.sender.sender().getName());

        assertEquals(incomingMessage.getContent(), BODY);
        assertEquals(incomingMessage.getSubject(), SUBJECT);
        assertTrue(incomingMessage.getContentType().contains(EmailMessageUtils.TEXT_CONTENT_TYPE));
        assertEquals(incomingMessage.getEncoding(), EmailMessageUtils.DEFAULT_ENCODING);
    }

    private void assertIncomingMessageRecipients_1(IncomingMessage incomingMessage) {
        assertEquals(incomingMessage.getRecipientEmail().size(), 1);
        EmailParticipant recipient = incomingMessage.getRecipientEmail().stream().findFirst().get();
        assertEquals(recipient.getEmail(), this.recipientEmail1);
        assertEquals(recipient.getName(), this.recipientEmail1);
    }

    private void assertIncomingMessageRecipients_2(IncomingMessage incomingMessage) {
        assertEquals(incomingMessage.getRecipientEmail().size(), 2);
        Set<String> emails = incomingMessage.getRecipientEmail().stream()
            .map(EmailParticipant::getEmail)
            .collect(Collectors.toSet());
        Set<String> names = incomingMessage.getRecipientEmail().stream()
            .map(EmailParticipant::getName)
            .collect(Collectors.toSet());

        assertTrue(emails.contains(this.recipientEmail1));
        assertTrue(emails.contains(this.recipientEmail2));

        assertTrue(names.contains(this.recipientEmail1));
        assertTrue(names.contains(this.recipientEmail2));
    }

    private void assertIncomingMessageAttachments(IncomingMessage incomingMessage) {
        assertEquals(incomingMessage.getAttachments().size(), 2);
        incomingMessage.getAttachments().forEach(attachment -> {
            assertNotNull(attachment.getName());
            if (ATTACHMENT_1.getName().equals(attachment.getName())) {
                assertEquals(ATTACHMENT_1.getSize(), attachment.getSize());
                assertEquals(ATTACHMENT_1.getType(), attachment.getType());
                assertEquals(ATTACHMENT_1.getContentType(), attachment.getContentType());
                assertArrayEquals(ATTACHMENT_1.getData(), attachment.getData());
            } else if (ATTACHMENT_2.getName().equals(attachment.getName())) {
                assertEquals(ATTACHMENT_2.getSize(), attachment.getSize());
                assertEquals(ATTACHMENT_2.getType(), attachment.getType());
                assertEquals(ATTACHMENT_2.getContentType(), attachment.getContentType());
                assertArrayEquals(ATTACHMENT_2.getData(), attachment.getData());
            }
        });
    }
}
