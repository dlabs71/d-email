package ru.dlabs.library.email.client.receiver;

import jakarta.mail.Folder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import ru.dlabs.library.email.client.DClient;
import ru.dlabs.library.email.dto.message.MessageView;
import ru.dlabs.library.email.dto.message.api.IncomingMessage;
import ru.dlabs.library.email.dto.pageable.PageRequest;

public interface ReceiverDClient extends DClient {

    void setStore(String credentialId);

    Integer getTotalCount(String folderName);

    List<MessageView> checkEmailMessages(String folderName, PageRequest pageRequest);

    List<IncomingMessage> readMessages(String folderName, PageRequest pageRequest);

    IncomingMessage readMessageById(String folderName, int id);

    Folder openFolderForRead(String folderName);

    Folder openFolderForWrite(String folderName);

    void closeFolder(Folder folder);

    boolean deleteMessage(String folderName, int id);

    Map<Integer, Boolean> deleteMessages(String folderName, Collection<Integer> ids);

    Map<Integer, Boolean> deleteAllMessages(String folderName);
}
