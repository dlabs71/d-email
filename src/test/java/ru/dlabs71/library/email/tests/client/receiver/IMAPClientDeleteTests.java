package ru.dlabs71.library.email.tests.client.receiver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
@Order(422)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IMAPClientDeleteTests extends AbstractTestsClass {

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

    @BeforeEach
    @SneakyThrows
    public void sendData() {
        String email = ReceiveTestUtils.getDefaultEmail(sslImapProperties);
        this.emailSender.sendText(email, "Тестовое сообщение 1", "Содержание тестового сообщения 1");
        this.emailSender.sendText(email, "Тестовое сообщение 2", "Содержание тестового сообщения 2");
        this.emailSender.sendText(email, "Тестовое сообщение 3", "Содержание тестового сообщения 3");
        this.emailSender.sendText(email, "Тестовое сообщение 4", "Содержание тестового сообщения 4");
        this.emailSender.sendText(email, "Тестовое сообщение 5", "Содержание тестового сообщения 5");
        Thread.sleep(sendDelayAfter);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link DEmailReceiver#deleteMessageById(Integer)}</li>
     * </ul>
     */
    @Test
    @Order(1)
    public void deleteMessage() {
        PageResponse<MessageView> response = this.emailReceiver.checkEmail();
        int total = response.getTotalCount();

        boolean result = this.emailReceiver.deleteMessageById(response.getData().get(0).getId());
        assertTrue(result);

        response = this.emailReceiver.checkEmail();
        assertEquals(1, total - response.getTotalCount());
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link DEmailReceiver#deleteMessages(Collection)}</li>
     * </ul>
     */
    @Test
    @Order(2)
    public void deleteSeveralMessages() {
        PageResponse<MessageView> response = this.emailReceiver.checkEmail();
        int total = response.getTotalCount();

        List<Integer> ids = new ArrayList<>();
        ids.add(response.getData().get(0).getId());
        ids.add(response.getData().get(1).getId());

        Map<Integer, Boolean> result = this.emailReceiver.deleteMessages(ids);
        assertEquals(2, result.size());
        result.forEach((key, value) -> assertTrue(value));

        response = this.emailReceiver.checkEmail();
        assertEquals(2, total - response.getTotalCount());
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link DEmailReceiver#clearCurrentFolder()}</li>
     * </ul>
     */
    @Test
    @Order(3)
    public void deleteAllMessages() {
        PageResponse<MessageView> response = this.emailReceiver.checkEmail();
        int total = response.getTotalCount();

        Map<Integer, Boolean> result = this.emailReceiver.clearCurrentFolder();
        assertEquals(total, result.size());
        result.forEach((key, value) -> assertTrue(value));
    }
}
