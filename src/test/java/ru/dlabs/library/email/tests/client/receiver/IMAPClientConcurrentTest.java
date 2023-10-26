package ru.dlabs.library.email.tests.client.receiver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.dlabs.library.email.tests.client.receiver.ReceiveTestUtils.CREDENTIAL_ID_1;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import ru.dlabs.library.email.DEmailReceiver;
import ru.dlabs.library.email.DEmailSender;
import ru.dlabs.library.email.client.receiver.IMAPDClient;
import ru.dlabs.library.email.dto.message.incoming.MessageView;
import ru.dlabs.library.email.dto.pageable.PageResponse;
import ru.dlabs.library.email.property.ImapProperties;
import ru.dlabs.library.email.support.AbstractTestsClass;
import ru.dlabs.library.email.tests.client.sender.SenderTestUtils;

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
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IMAPClientConcurrentTest extends AbstractTestsClass {

    private final static Integer COUNT_OF_MESSAGES = 3;

    private ImapProperties sslImapProperties;
    private ImapProperties tlsImapProperties;
    private ImapProperties simpleImapProperties;

    private DEmailReceiver emailReceiver;
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
        this.emailReceiver = DEmailReceiver.of(this.simpleImapProperties);

        this.sendData(this.recipientEmail);
    }

    @SneakyThrows
    private void sendData(String email) {
        this.emailReceiver.credentialId(CREDENTIAL_ID_1)
            .clearCurrentFolder();
        this.emailSender.sendText(email, "Тестовое сообщение 1", "Содержание тестового сообщения 1");
        this.emailSender.sendText(email, "Тестовое сообщение 2", "Содержание тестового сообщения 2");
        this.emailSender.sendText(email, "Тестовое сообщение 3", "Содержание тестового сообщения 3");
        Thread.sleep(sendDelayAfter);
    }

    @Test
    @SneakyThrows
    public void concurrentTest() {
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 100; i++) {
            executorService.execute(this::changeFolder);
            executorService.execute(this::readMessages);
        }
        executorService.shutdown();
        while (!executorService.isTerminated()) {
            Thread.sleep(1000);
        }
    }

    public void readMessages() {
        String currentFolder = emailReceiver.getCurrentFolder();
        PageResponse<MessageView> response = emailReceiver
            .credentialId(CREDENTIAL_ID_1)
            .checkEmail();
        System.out.println(
            "Read from folder: " + currentFolder + ". Size: " + response.getData().size());
        if ("Sent".equals(currentFolder)) {
            assertEquals(0, response.getData().size());
        }
        if (IMAPDClient.DEFAULT_INBOX_FOLDER_NAME.equals(currentFolder)) {
            assertEquals(3, response.getData().size());
        }
    }

    public void changeFolder() {
        String folderName = "Sent".equals(this.emailReceiver.getCurrentFolder())
            ? IMAPDClient.DEFAULT_INBOX_FOLDER_NAME : "Sent";
        System.out.println("Change folder: " + folderName);
        this.emailReceiver.folder(folderName);
    }
}
