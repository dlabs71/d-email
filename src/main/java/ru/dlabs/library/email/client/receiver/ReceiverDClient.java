package ru.dlabs.library.email.client.receiver;

import jakarta.mail.MessagingException;
import java.util.List;
import ru.dlabs.library.email.client.DClient;
import ru.dlabs.library.email.message.TextMessage;

public interface ReceiverDClient extends DClient {

    void setStore(String credentialId) throws MessagingException;

    List<TextMessage> checkEmailMessages() throws MessagingException;

    List<TextMessage> getEmailMessages();
}
