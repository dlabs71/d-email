package ru.dlabs71.library.email.tests.client.receiver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.List;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import ru.dlabs71.library.email.DEmailReceiver;
import ru.dlabs71.library.email.DEmailSender;
import ru.dlabs71.library.email.dto.message.incoming.MessageView;
import ru.dlabs71.library.email.dto.pageable.PageRequest;
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
@Order(425)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IMAPClientPageableTests extends AbstractTestsClass {

    private final static Integer countMessages = 20;
    private final static Integer pageSize = 5;

    private DEmailSender emailSender;
    private DEmailReceiver emailReceiver;
    private ImapProperties sslImapProperties;

    @BeforeAll
    public void loadConfig() {
        ImapProperties[] properties = ReceiveTestUtils.loadProperties();
        this.sslImapProperties = properties[0];
        this.emailSender = SenderTestUtils.createSender();
        this.emailReceiver = DEmailReceiver.of(sslImapProperties);
    }

    @AfterEach
    public void afterTests() {
        this.emailReceiver.clearCurrentFolder();
    }

    @BeforeEach
    @SneakyThrows
    public void sendData() {
        String email = ReceiveTestUtils.getDefaultEmail(sslImapProperties);
        this.emailReceiver.clearCurrentFolder();
        Thread.sleep(sendDelayAfter);
        for (int i = 0; i < countMessages; i++) {
            this.emailSender.sendText(email, "Тестовое сообщение " + i, "Содержание тестового сообщения " + i);
            Thread.sleep(1000);
        }
        Thread.sleep(2L * sendDelayAfter);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link DEmailReceiver#checkEmail(PageRequest)} </li>
     * </ul>
     * <p>
     */
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

    /**
     * The test for:
     * <ul>
     *     <li>{@link DEmailReceiver#checkEmail(PageRequest)} </li>
     * </ul>
     * <p>
     */
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
