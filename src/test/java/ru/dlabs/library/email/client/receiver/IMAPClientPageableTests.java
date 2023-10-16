package ru.dlabs.library.email.client.receiver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import ru.dlabs.library.email.DEmailReceiver;
import ru.dlabs.library.email.DEmailSender;
import ru.dlabs.library.email.client.sender.SenderTestUtils;
import ru.dlabs.library.email.dto.message.incoming.MessageView;
import ru.dlabs.library.email.dto.pageable.PageResponse;
import ru.dlabs.library.email.property.ImapProperties;
import ru.dlabs.library.email.support.AbstractTestsClass;

/**
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-08-31
 */
@Order(322)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IMAPClientPageableTests extends AbstractTestsClass {

    private final static Integer countMessages = 20;
    private final static Integer pageSize = 5;

    private DEmailSender emailSender;
    private DEmailReceiver emailReceiver;

    @BeforeEach
    public void loadConfig() throws IOException {
        ImapProperties[] properties = ReceiveTestUtils.loadProperties();
        ImapProperties sslImapProperties = properties[0];
        this.emailSender = SenderTestUtils.createSender();
        this.emailReceiver = DEmailReceiver.of(sslImapProperties)
            .credentialId(ReceiveTestUtils.CREDENTIAL_ID_1);

        String email = ReceiveTestUtils.getDefaultEmail(sslImapProperties);
        this.sendData(email);
    }

    @AfterEach
    public void afterTests() {
        this.emailReceiver.clearCurrentFolder();
    }

    @SneakyThrows
    private void sendData(String email) {
        this.emailReceiver.clearCurrentFolder();
        for (int i = 0; i < countMessages; i++) {
            this.emailSender.sendText(email, "Тестовое сообщение " + i, "Содержание тестового сообщения " + i);
            Thread.sleep(500);
        }
        Thread.sleep(sendDelayAfter);
    }

    @Test
    public void pageableTest() {
        this.emailReceiver.start(0);
        this.emailReceiver.pageSize(pageSize);
        PageResponse<MessageView> response1 = this.emailReceiver.nextCheckEmail();

        assertEquals(response1.getTotalCount(), countMessages);
        assertEquals(response1.getData().size(), pageSize);

        PageResponse<MessageView> response2 = this.emailReceiver.nextCheckEmail();

        List<String> subjectResponse1 = response1.getData().stream()
            .map(MessageView::getSubject)
            .collect(Collectors.toList());

        List<String> subjectResponse2 = response2.getData().stream()
            .map(MessageView::getSubject)
            .collect(Collectors.toList());

        boolean intersection = subjectResponse1.stream().anyMatch(subjectResponse2::contains);
        assertFalse(intersection);
    }

    @Test
    public void changePageSizeTest() {
        this.emailReceiver.start(0);
        this.emailReceiver.pageSize(pageSize);
        PageResponse<MessageView> response1 = this.emailReceiver.nextCheckEmail();

        assertEquals(response1.getTotalCount(), countMessages);
        assertEquals(response1.getData().size(), pageSize);

        int newPageSize = 2 * pageSize;
        this.emailReceiver.pageSize(newPageSize);
        PageResponse<MessageView> response2 = this.emailReceiver.nextCheckEmail();

        assertEquals(response2.getTotalCount(), countMessages);
        assertEquals(response2.getData().size(), newPageSize);
    }

    @Test
    public void changeStartTest() {
        this.emailReceiver.start(0);
        this.emailReceiver.pageSize(pageSize);
        PageResponse<MessageView> response1 = this.emailReceiver.nextCheckEmail();

        assertEquals(response1.getTotalCount(), countMessages);
        assertEquals(response1.getData().size(), pageSize);

        this.emailReceiver.start(0);
        PageResponse<MessageView> response2 = this.emailReceiver.nextCheckEmail();

        assertEquals(response2.getTotalCount(), countMessages);
        assertEquals(response2.getData().size(), pageSize);

        List<String> subjectResponse1 = response1.getData().stream()
            .map(MessageView::getSubject)
            .collect(Collectors.toList());

        List<String> subjectResponse2 = response2.getData().stream()
            .map(MessageView::getSubject)
            .collect(Collectors.toList());

        boolean fullIntersection = subjectResponse2.containsAll(subjectResponse1);
        assertTrue(fullIntersection);
    }
}
