package ru.dlabs.library.email.tests.client.receiver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
import ru.dlabs.library.email.tests.client.sender.SenderTestUtils;
import ru.dlabs.library.email.dto.message.incoming.MessageView;
import ru.dlabs.library.email.dto.pageable.PageRequest;
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
    public void loadConfig() {
        ImapProperties[] properties = ReceiveTestUtils.loadProperties();
        ImapProperties sslImapProperties = properties[0];
        this.emailSender = SenderTestUtils.createSender();
        this.emailReceiver = DEmailReceiver.of(sslImapProperties);

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
        Thread.sleep(sendDelayAfter + 1000);
    }

    @Test
    public void pageableTest() {
        PageRequest pageRequest = PageRequest.of(0, pageSize);
        PageResponse<MessageView> response1 = this.emailReceiver.checkEmail(pageRequest);

        assertEquals(countMessages, response1.getTotalCount());
        assertEquals(pageSize, response1.getData().size());

        pageRequest.incrementStart();
        PageResponse<MessageView> response2 = this.emailReceiver.checkEmail(pageRequest);

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
        PageResponse<MessageView> response1 = this.emailReceiver.checkEmail(PageRequest.of(0, pageSize));

        assertEquals(countMessages, response1.getTotalCount());
        assertEquals(pageSize, response1.getData().size());

        int newPageSize = 2 * pageSize;
        PageResponse<MessageView> response2 = this.emailReceiver.checkEmail(PageRequest.of(0, newPageSize));

        assertEquals(countMessages, response2.getTotalCount());
        assertEquals(newPageSize, response2.getData().size());
    }
}
