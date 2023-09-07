package ru.dlabs.library.email.client.receiver;

import jakarta.mail.Folder;
import java.util.List;
import ru.dlabs.library.email.client.DClient;
import ru.dlabs.library.email.dto.message.common.BaseMessage;
import ru.dlabs.library.email.dto.message.MessageView;
import ru.dlabs.library.email.dto.pageable.PageRequest;

public interface ReceiverDClient extends DClient {

    void setStore(String credentialId);

    Integer getTotalCount(String folderName);

    Integer getTotalCount();

    List<MessageView> checkEmailMessages(PageRequest pageRequest);

    List<MessageView> checkEmailMessages(String folderName, PageRequest pageRequest);

    List<BaseMessage> readMessages(String folderName, PageRequest pageRequest);

    List<BaseMessage> readMessages(PageRequest pageRequest);

    BaseMessage readMessageById(String folderName, int id);

    BaseMessage readMessageById(int id);

    Folder openFolder(String folderName);

    void closeFolder(Folder folder);
}
