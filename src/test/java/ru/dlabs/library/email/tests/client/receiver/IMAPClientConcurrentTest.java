package ru.dlabs.library.email.tests.client.receiver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import ru.dlabs.library.email.DEmailReceiver;
import ru.dlabs.library.email.DEmailSender;
import ru.dlabs.library.email.dto.message.incoming.IncomingMessage;
import ru.dlabs.library.email.dto.message.incoming.MessageView;
import ru.dlabs.library.email.dto.pageable.PageResponse;
import ru.dlabs.library.email.property.ImapProperties;
import ru.dlabs.library.email.support.AbstractTestsClass;
import ru.dlabs.library.email.tests.client.receiver.utils.ReceiveTestUtils;
import ru.dlabs.library.email.tests.client.sender.utils.SenderTestUtils;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-25</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Slf4j
@Order(426)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IMAPClientConcurrentTest extends AbstractTestsClass {

    private final static Integer COUNT_OF_MESSAGES = 3;
    private static final String INBOX_NAME = "INBOX";
    private static final String OUTBOX_NAME = "Sent";

    private DEmailReceiver emailReceiver;
    private DEmailSender emailSender;
    private boolean hasError;

    @BeforeAll
    public void loadConfig() {
        ImapProperties[] properties = ReceiveTestUtils.loadProperties();
        this.emailSender = SenderTestUtils.createSender();
        this.emailReceiver = DEmailReceiver.of(properties[2]);
    }

    @BeforeEach
    @SneakyThrows
    public void sendData() {
        String email = this.emailSender.sender().getEmail();
        this.emailReceiver.clearCurrentFolder();
        this.emailSender.sendText(email, "Тестовое сообщение 1", "Содержание тестового сообщения 1");
        this.emailSender.sendText(email, "Тестовое сообщение 2", "Содержание тестового сообщения 2");
        this.emailSender.sendText(email, "Тестовое сообщение 3", "Содержание тестового сообщения 3");
        Thread.sleep(sendDelayAfter);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link DEmailReceiver} in multithreading environment</li>
     * </ul>
     */
    @Test
    @SneakyThrows
    public void concurrentTest() {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 3000; i++) {
            executorService.execute(this::changeFolder);
            executorService.execute(this::checkEmail);
            executorService.execute(this::readEmail);
        }
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            Thread.sleep(1000);
        }
        assertFalse(hasError);
    }

    public void checkEmail() {
        try {
            PageResponse<MessageView> response = emailReceiver.checkEmail();
            log.info("Read from folder: " + response.getFolderName() + ". Size: " + response.getData().size());
            if (OUTBOX_NAME.equals(response.getFolderName())) {
                assertEquals(0, response.getData().size());
            }
            if (INBOX_NAME.equals(response.getFolderName())) {
                assertEquals(COUNT_OF_MESSAGES, response.getData().size());
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            setError();
        }
    }

    public void readEmail() {
        try {
            PageResponse<IncomingMessage> response = emailReceiver.readEmail();
            log.info("Read from folder: " + response.getFolderName() + ". Size: " + response.getData().size());
            if (OUTBOX_NAME.equals(response.getFolderName())) {
                assertEquals(0, response.getData().size());
            }
            if (INBOX_NAME.equals(response.getFolderName())) {
                assertEquals(COUNT_OF_MESSAGES, response.getData().size());
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            setError();
        }
    }

    public void changeFolder() {
        try {
            String folderName = OUTBOX_NAME.equals(this.emailReceiver.getCurrentFolder()) ? INBOX_NAME : OUTBOX_NAME;
            log.info("Change folder: " + folderName);
            this.emailReceiver.folder(folderName);
            assertEquals(folderName, this.emailReceiver.getCurrentFolder());
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            setError();
        }
    }

    private synchronized void setError() {
        this.hasError = true;
    }
}
