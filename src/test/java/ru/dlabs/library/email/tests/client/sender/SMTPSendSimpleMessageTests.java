package ru.dlabs.library.email.tests.client.sender;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import ru.dlabs.library.email.DEmailReceiver;
import ru.dlabs.library.email.DEmailSender;
import ru.dlabs.library.email.dto.message.common.EmailAttachment;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;
import ru.dlabs.library.email.dto.message.incoming.IncomingMessage;
import ru.dlabs.library.email.dto.pageable.PageResponse;
import ru.dlabs.library.email.support.AbstractTestsClass;
import ru.dlabs.library.email.support.PropUtils;
import ru.dlabs.library.email.tests.client.receiver.ReceiveTestUtils;
import ru.dlabs.library.email.tests.client.sender.utils.Assertions;
import ru.dlabs.library.email.tests.client.sender.utils.SenderTestUtils;
import ru.dlabs.library.email.type.SendingStatus;
import ru.dlabs.library.email.util.AttachmentUtils;

@Order(31)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SMTPSendSimpleMessageTests extends AbstractTestsClass {

    private final static String TEXT_BODY = "It's the body of this message. Please don't reply to this one.";
    private final static String HTML_BODY =
        "<div>It's the body of this message. <br/>Please don't reply to this one.</div>";
    private final static String SUBJECT = "Test of sending";
    private final static EmailAttachment ATTACHMENT_1 = AttachmentUtils.create("classpath:attachments/file.txt");
    private final static EmailAttachment ATTACHMENT_2 = AttachmentUtils.create("classpath:attachments/file.jpg");

    private String recipientEmail1;
    private String recipientEmail2;
    private DEmailReceiver receiver1;
    private DEmailReceiver receiver2;
    private DEmailSender sender;

    @BeforeEach
    public void loadConfig() {
        Properties props = PropUtils.loadPropertiesFromFile(SenderTestUtils.PROP_FILE_NAME);
        this.recipientEmail1 = props.getProperty("recipientEmail1");
        this.recipientEmail2 = props.getProperty("recipientEmail2");
        this.receiver1 = ReceiveTestUtils.createReceiver1();
        this.receiver2 = ReceiveTestUtils.createReceiver2();
        this.sender = SenderTestUtils.createSender();
    }

    @Test
    @Order(1)
    @SneakyThrows
    public void sendTextTest_1() {
        this.receiver1.clearCurrentFolder();

        Thread.sleep(sendDelayAfter);
        SendingStatus result = this.sender.sendText(this.recipientEmail1, SUBJECT, TEXT_BODY);

        assertEquals(SendingStatus.SUCCESS, result);

        Thread.sleep(sendDelayAfter);

        IncomingMessage incomingMessage = assertAndGetMessageFromMailbox(this.receiver1);
        Assertions.assertTextIncomingMessage(sender.sender(), SUBJECT, TEXT_BODY, incomingMessage);
        Assertions.assertIncomingMessageRecipients(
            Arrays.asList(this.recipientEmail1),
            incomingMessage.getRecipients()
        );
        Assertions.assertIncomingMessageEmptyAttachments(incomingMessage.getAttachments());
    }


    @Test
    @Order(2)
    @SneakyThrows
    public void sendTextTest_2() {
        this.receiver1.clearCurrentFolder();
        this.receiver2.clearCurrentFolder();

        Thread.sleep(sendDelayAfter);
        SendingStatus result = this.sender.sendText(
            Arrays.asList(this.recipientEmail1, this.recipientEmail2),
            SUBJECT,
            TEXT_BODY
        );

        assertEquals(SendingStatus.SUCCESS, result);

        Thread.sleep(sendDelayAfter);

        IncomingMessage incomingMessage1 = assertAndGetMessageFromMailbox(this.receiver1);
        Assertions.assertTextIncomingMessage(sender.sender(), SUBJECT, TEXT_BODY, incomingMessage1);
        Assertions.assertIncomingMessageRecipients(
            Arrays.asList(this.recipientEmail1, this.recipientEmail2),
            incomingMessage1.getRecipients()
        );
        Assertions.assertIncomingMessageEmptyAttachments(incomingMessage1.getAttachments());

        IncomingMessage incomingMessage2 = assertAndGetMessageFromMailbox(this.receiver2);
        Assertions.assertTextIncomingMessage(sender.sender(), SUBJECT, TEXT_BODY, incomingMessage2);
        Assertions.assertIncomingMessageRecipients(
            Arrays.asList(this.recipientEmail1, this.recipientEmail2),
            incomingMessage2.getRecipients()
        );
        Assertions.assertIncomingMessageEmptyAttachments(incomingMessage2.getAttachments());
    }

    @Test
    @Order(3)
    @SneakyThrows
    public void sendTextTest_3() {
        this.receiver1.clearCurrentFolder();
        this.receiver2.clearCurrentFolder();

        Thread.sleep(sendDelayAfter);
        SendingStatus result = this.sender.sendText(
            Arrays.asList(this.recipientEmail1, this.recipientEmail2),
            SUBJECT,
            TEXT_BODY,
            ATTACHMENT_1,
            ATTACHMENT_2
        );

        assertEquals(SendingStatus.SUCCESS, result);

        Thread.sleep(sendDelayAfter);

        IncomingMessage incomingMessage1 = assertAndGetMessageFromMailbox(this.receiver1);
        Assertions.assertTextIncomingMessage(sender.sender(), SUBJECT, TEXT_BODY, incomingMessage1);
        Assertions.assertIncomingMessageRecipients(
            Arrays.asList(this.recipientEmail1, this.recipientEmail2),
            incomingMessage1.getRecipients()
        );
        Assertions.assertIncomingMessageAttachments(
            Arrays.asList(ATTACHMENT_1, ATTACHMENT_2),
            incomingMessage1.getAttachments()
        );

        IncomingMessage incomingMessage2 = assertAndGetMessageFromMailbox(this.receiver2);
        Assertions.assertTextIncomingMessage(sender.sender(), SUBJECT, TEXT_BODY, incomingMessage2);
        Assertions.assertIncomingMessageRecipients(
            Arrays.asList(this.recipientEmail1, this.recipientEmail2),
            incomingMessage2.getRecipients()
        );
        Assertions.assertIncomingMessageAttachments(
            Arrays.asList(ATTACHMENT_1, ATTACHMENT_2),
            incomingMessage2.getAttachments()
        );
    }

    @Test
    @Order(4)
    @SneakyThrows
    public void sendTextTest_4() {
        this.receiver1.clearCurrentFolder();
        this.receiver2.clearCurrentFolder();

        Thread.sleep(sendDelayAfter);
        SendingStatus result = this.sender.sendText(
            Arrays.asList(this.recipientEmail1, this.recipientEmail2),
            SUBJECT,
            TEXT_BODY,
            Arrays.asList(ATTACHMENT_1, ATTACHMENT_2)
        );

        assertEquals(SendingStatus.SUCCESS, result);

        Thread.sleep(sendDelayAfter);

        IncomingMessage incomingMessage1 = assertAndGetMessageFromMailbox(this.receiver1);
        Assertions.assertTextIncomingMessage(sender.sender(), SUBJECT, TEXT_BODY, incomingMessage1);
        Assertions.assertIncomingMessageRecipients(
            Arrays.asList(this.recipientEmail1, this.recipientEmail2),
            incomingMessage1.getRecipients()
        );
        Assertions.assertIncomingMessageAttachments(
            Arrays.asList(ATTACHMENT_1, ATTACHMENT_2),
            incomingMessage1.getAttachments()
        );

        IncomingMessage incomingMessage2 = assertAndGetMessageFromMailbox(this.receiver2);
        Assertions.assertTextIncomingMessage(sender.sender(), SUBJECT, TEXT_BODY, incomingMessage2);
        Assertions.assertIncomingMessageRecipients(
            Arrays.asList(this.recipientEmail1, this.recipientEmail2),
            incomingMessage2.getRecipients()
        );
        Assertions.assertIncomingMessageAttachments(
            Arrays.asList(ATTACHMENT_1, ATTACHMENT_2),
            incomingMessage2.getAttachments()
        );
    }

    @Test
    @Order(5)
    @SneakyThrows
    public void sendTextTest_5() {
        this.receiver1.clearCurrentFolder();

        Thread.sleep(sendDelayAfter);
        SendingStatus result = this.sender.sendText(
            this.recipientEmail1,
            SUBJECT,
            TEXT_BODY,
            ATTACHMENT_1,
            ATTACHMENT_2
        );

        assertEquals(SendingStatus.SUCCESS, result);

        Thread.sleep(sendDelayAfter);

        IncomingMessage incomingMessage1 = assertAndGetMessageFromMailbox(this.receiver1);
        Assertions.assertTextIncomingMessage(sender.sender(), SUBJECT, TEXT_BODY, incomingMessage1);
        Assertions.assertIncomingMessageRecipients(
            Arrays.asList(this.recipientEmail1),
            incomingMessage1.getRecipients()
        );
        Assertions.assertIncomingMessageAttachments(
            Arrays.asList(ATTACHMENT_1, ATTACHMENT_2),
            incomingMessage1.getAttachments()
        );
    }

    @Test
    @Order(6)
    @SneakyThrows
    public void sendTextTest_6() {
        this.receiver1.clearCurrentFolder();

        Thread.sleep(sendDelayAfter);
        SendingStatus result = this.sender.sendText(
            this.recipientEmail1,
            SUBJECT,
            TEXT_BODY,
            Arrays.asList(ATTACHMENT_1, ATTACHMENT_2)
        );

        assertEquals(SendingStatus.SUCCESS, result);

        Thread.sleep(sendDelayAfter);

        IncomingMessage incomingMessage1 = assertAndGetMessageFromMailbox(this.receiver1);
        Assertions.assertTextIncomingMessage(sender.sender(), SUBJECT, TEXT_BODY, incomingMessage1);
        Assertions.assertIncomingMessageRecipients(
            Arrays.asList(this.recipientEmail1),
            incomingMessage1.getRecipients()
        );
        Assertions.assertIncomingMessageAttachments(
            Arrays.asList(ATTACHMENT_1, ATTACHMENT_2),
            incomingMessage1.getAttachments()
        );
    }

    @Test
    @Order(7)
    @SneakyThrows
    public void sendTextTest_7() {
        this.receiver1.clearCurrentFolder();
        this.receiver2.clearCurrentFolder();

        Thread.sleep(sendDelayAfter);
        SendingStatus result = this.sender.sendText(
            new HashSet<>(Arrays.asList(
                EmailParticipant.of(this.recipientEmail1),
                EmailParticipant.of(this.recipientEmail2)
            )),
            SUBJECT,
            TEXT_BODY,
            Arrays.asList(ATTACHMENT_1, ATTACHMENT_2)
        );

        assertEquals(SendingStatus.SUCCESS, result);

        Thread.sleep(sendDelayAfter);

        IncomingMessage incomingMessage1 = assertAndGetMessageFromMailbox(this.receiver1);
        Assertions.assertTextIncomingMessage(sender.sender(), SUBJECT, TEXT_BODY, incomingMessage1);
        Assertions.assertIncomingMessageRecipients(
            Arrays.asList(this.recipientEmail1, this.recipientEmail2),
            incomingMessage1.getRecipients()
        );
        Assertions.assertIncomingMessageAttachments(
            Arrays.asList(ATTACHMENT_1, ATTACHMENT_2),
            incomingMessage1.getAttachments()
        );

        IncomingMessage incomingMessage2 = assertAndGetMessageFromMailbox(this.receiver2);
        Assertions.assertTextIncomingMessage(sender.sender(), SUBJECT, TEXT_BODY, incomingMessage2);
        Assertions.assertIncomingMessageRecipients(
            Arrays.asList(this.recipientEmail1, this.recipientEmail2),
            incomingMessage2.getRecipients()
        );
        Assertions.assertIncomingMessageAttachments(
            Arrays.asList(ATTACHMENT_1, ATTACHMENT_2),
            incomingMessage2.getAttachments()
        );
    }

    @Test
    @Order(8)
    @SneakyThrows
    public void sendTextTest_8() {
        this.receiver1.clearCurrentFolder();

        Thread.sleep(sendDelayAfter);
        SendingStatus result = this.sender.sendHtml(this.recipientEmail1, SUBJECT, HTML_BODY);

        assertEquals(SendingStatus.SUCCESS, result);

        Thread.sleep(sendDelayAfter);

        IncomingMessage incomingMessage = assertAndGetMessageFromMailbox(this.receiver1);
        Assertions.assertHtmlIncomingMessage(sender.sender(), SUBJECT, HTML_BODY, incomingMessage);
        Assertions.assertIncomingMessageRecipients(
            Arrays.asList(this.recipientEmail1),
            incomingMessage.getRecipients()
        );
        Assertions.assertIncomingMessageEmptyAttachments(incomingMessage.getAttachments());
    }


    @Test
    @Order(9)
    @SneakyThrows
    public void sendTextTest_9() {
        this.receiver1.clearCurrentFolder();
        this.receiver2.clearCurrentFolder();

        Thread.sleep(sendDelayAfter);
        SendingStatus result = this.sender.sendHtml(
            Arrays.asList(this.recipientEmail1, this.recipientEmail2),
            SUBJECT,
            HTML_BODY
        );

        assertEquals(SendingStatus.SUCCESS, result);

        Thread.sleep(sendDelayAfter);

        IncomingMessage incomingMessage1 = assertAndGetMessageFromMailbox(this.receiver1);
        Assertions.assertHtmlIncomingMessage(sender.sender(), SUBJECT, HTML_BODY, incomingMessage1);
        Assertions.assertIncomingMessageRecipients(
            Arrays.asList(this.recipientEmail1, this.recipientEmail2),
            incomingMessage1.getRecipients()
        );
        Assertions.assertIncomingMessageEmptyAttachments(incomingMessage1.getAttachments());

        IncomingMessage incomingMessage2 = assertAndGetMessageFromMailbox(this.receiver2);
        Assertions.assertHtmlIncomingMessage(sender.sender(), SUBJECT, HTML_BODY, incomingMessage2);
        Assertions.assertIncomingMessageRecipients(
            Arrays.asList(this.recipientEmail1, this.recipientEmail2),
            incomingMessage2.getRecipients()
        );
        Assertions.assertIncomingMessageEmptyAttachments(incomingMessage2.getAttachments());
    }

    @Test
    @Order(10)
    @SneakyThrows
    public void sendTextTest_10() {
        this.receiver1.clearCurrentFolder();
        this.receiver2.clearCurrentFolder();

        Thread.sleep(sendDelayAfter);
        SendingStatus result = this.sender.sendHtml(
            Arrays.asList(this.recipientEmail1, this.recipientEmail2),
            SUBJECT,
            HTML_BODY,
            ATTACHMENT_1,
            ATTACHMENT_2
        );

        assertEquals(SendingStatus.SUCCESS, result);

        Thread.sleep(sendDelayAfter);

        IncomingMessage incomingMessage1 = assertAndGetMessageFromMailbox(this.receiver1);
        Assertions.assertHtmlIncomingMessage(sender.sender(), SUBJECT, HTML_BODY, incomingMessage1);
        Assertions.assertIncomingMessageRecipients(
            Arrays.asList(this.recipientEmail1, this.recipientEmail2),
            incomingMessage1.getRecipients()
        );
        Assertions.assertIncomingMessageAttachments(
            Arrays.asList(ATTACHMENT_1, ATTACHMENT_2),
            incomingMessage1.getAttachments()
        );

        IncomingMessage incomingMessage2 = assertAndGetMessageFromMailbox(this.receiver2);
        Assertions.assertHtmlIncomingMessage(sender.sender(), SUBJECT, HTML_BODY, incomingMessage2);
        Assertions.assertIncomingMessageRecipients(
            Arrays.asList(this.recipientEmail1, this.recipientEmail2),
            incomingMessage2.getRecipients()
        );
        Assertions.assertIncomingMessageAttachments(
            Arrays.asList(ATTACHMENT_1, ATTACHMENT_2),
            incomingMessage2.getAttachments()
        );
    }

    @Test
    @Order(11)
    @SneakyThrows
    public void sendTextTest_11() {
        this.receiver1.clearCurrentFolder();
        this.receiver2.clearCurrentFolder();

        Thread.sleep(sendDelayAfter);
        SendingStatus result = this.sender.sendHtml(
            Arrays.asList(this.recipientEmail1, this.recipientEmail2),
            SUBJECT,
            HTML_BODY,
            Arrays.asList(ATTACHMENT_1, ATTACHMENT_2)
        );

        assertEquals(SendingStatus.SUCCESS, result);

        Thread.sleep(sendDelayAfter);

        IncomingMessage incomingMessage1 = assertAndGetMessageFromMailbox(this.receiver1);
        Assertions.assertHtmlIncomingMessage(sender.sender(), SUBJECT, HTML_BODY, incomingMessage1);
        Assertions.assertIncomingMessageRecipients(
            Arrays.asList(this.recipientEmail1, this.recipientEmail2),
            incomingMessage1.getRecipients()
        );
        Assertions.assertIncomingMessageAttachments(
            Arrays.asList(ATTACHMENT_1, ATTACHMENT_2),
            incomingMessage1.getAttachments()
        );

        IncomingMessage incomingMessage2 = assertAndGetMessageFromMailbox(this.receiver2);
        Assertions.assertHtmlIncomingMessage(sender.sender(), SUBJECT, HTML_BODY, incomingMessage2);
        Assertions.assertIncomingMessageRecipients(
            Arrays.asList(this.recipientEmail1, this.recipientEmail2),
            incomingMessage2.getRecipients()
        );
        Assertions.assertIncomingMessageAttachments(
            Arrays.asList(ATTACHMENT_1, ATTACHMENT_2),
            incomingMessage2.getAttachments()
        );
    }

    @Test
    @Order(12)
    @SneakyThrows
    public void sendTextTest_12() {
        this.receiver1.clearCurrentFolder();

        Thread.sleep(sendDelayAfter);
        SendingStatus result = this.sender.sendHtml(
            this.recipientEmail1,
            SUBJECT,
            HTML_BODY,
            ATTACHMENT_1,
            ATTACHMENT_2
        );

        assertEquals(SendingStatus.SUCCESS, result);

        Thread.sleep(sendDelayAfter);

        IncomingMessage incomingMessage1 = assertAndGetMessageFromMailbox(this.receiver1);
        Assertions.assertHtmlIncomingMessage(sender.sender(), SUBJECT, HTML_BODY, incomingMessage1);
        Assertions.assertIncomingMessageRecipients(
            Arrays.asList(this.recipientEmail1),
            incomingMessage1.getRecipients()
        );
        Assertions.assertIncomingMessageAttachments(
            Arrays.asList(ATTACHMENT_1, ATTACHMENT_2),
            incomingMessage1.getAttachments()
        );
    }

    @Test
    @Order(13)
    @SneakyThrows
    public void sendTextTest_13() {
        this.receiver1.clearCurrentFolder();

        Thread.sleep(sendDelayAfter);
        SendingStatus result = this.sender.sendHtml(
            this.recipientEmail1,
            SUBJECT,
            HTML_BODY,
            Arrays.asList(ATTACHMENT_1, ATTACHMENT_2)
        );

        assertEquals(SendingStatus.SUCCESS, result);

        Thread.sleep(sendDelayAfter);

        IncomingMessage incomingMessage1 = assertAndGetMessageFromMailbox(this.receiver1);
        Assertions.assertHtmlIncomingMessage(sender.sender(), SUBJECT, HTML_BODY, incomingMessage1);
        Assertions.assertIncomingMessageRecipients(
            Arrays.asList(this.recipientEmail1),
            incomingMessage1.getRecipients()
        );
        Assertions.assertIncomingMessageAttachments(
            Arrays.asList(ATTACHMENT_1, ATTACHMENT_2),
            incomingMessage1.getAttachments()
        );
    }

    @Test
    @Order(14)
    @SneakyThrows
    public void sendTextTest_14() {
        this.receiver1.clearCurrentFolder();
        this.receiver2.clearCurrentFolder();

        Thread.sleep(sendDelayAfter);
        SendingStatus result = this.sender.sendHtml(
            new HashSet<>(Arrays.asList(
                EmailParticipant.of(this.recipientEmail1),
                EmailParticipant.of(this.recipientEmail2)
            )),
            SUBJECT,
            HTML_BODY,
            Arrays.asList(ATTACHMENT_1, ATTACHMENT_2)
        );

        assertEquals(SendingStatus.SUCCESS, result);

        Thread.sleep(sendDelayAfter);

        IncomingMessage incomingMessage1 = assertAndGetMessageFromMailbox(this.receiver1);
        Assertions.assertHtmlIncomingMessage(sender.sender(), SUBJECT, HTML_BODY, incomingMessage1);
        Assertions.assertIncomingMessageRecipients(
            Arrays.asList(this.recipientEmail1, this.recipientEmail2),
            incomingMessage1.getRecipients()
        );
        Assertions.assertIncomingMessageAttachments(
            Arrays.asList(ATTACHMENT_1, ATTACHMENT_2),
            incomingMessage1.getAttachments()
        );

        IncomingMessage incomingMessage2 = assertAndGetMessageFromMailbox(this.receiver2);
        Assertions.assertHtmlIncomingMessage(sender.sender(), SUBJECT, HTML_BODY, incomingMessage2);
        Assertions.assertIncomingMessageRecipients(
            Arrays.asList(this.recipientEmail1, this.recipientEmail2),
            incomingMessage2.getRecipients()
        );
        Assertions.assertIncomingMessageAttachments(
            Arrays.asList(ATTACHMENT_1, ATTACHMENT_2),
            incomingMessage2.getAttachments()
        );
    }

    private IncomingMessage assertAndGetMessageFromMailbox(DEmailReceiver receiver) {
        PageResponse<IncomingMessage> inbox = receiver.readEmail();
        assertEquals(1, inbox.getData().size());

        IncomingMessage incomingMessage = inbox.getData().get(0);
        assertNotNull(incomingMessage);
        return incomingMessage;
    }
}
