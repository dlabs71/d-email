package ru.dlabs.library.email.tests.client.receiver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import ru.dlabs.library.email.DEmailReceiver;
import ru.dlabs.library.email.DEmailSender;
import ru.dlabs.library.email.dto.message.incoming.MessageView;
import ru.dlabs.library.email.dto.pageable.PageResponse;
import ru.dlabs.library.email.property.ImapProperties;
import ru.dlabs.library.email.support.AbstractTestsClass;
import ru.dlabs.library.email.tests.client.sender.SenderTestUtils;

/**
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-08-31
 */
@Order(321)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IMAPClientDeleteTests extends AbstractTestsClass {

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

    @SneakyThrows
    private void sendData(String email) {
        this.emailSender.sendText(email, "Тестовое сообщение 1", "Содержание тестового сообщения 1");
        this.emailSender.sendText(email, "Тестовое сообщение 2", "Содержание тестового сообщения 2");
        this.emailSender.sendText(email, "Тестовое сообщение 3", "Содержание тестового сообщения 3");
        this.emailSender.sendText(email, "Тестовое сообщение 4", "Содержание тестового сообщения 4");
        this.emailSender.sendText(email, "Тестовое сообщение 5", "Содержание тестового сообщения 5");
        Thread.sleep(sendDelayAfter);
    }

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
