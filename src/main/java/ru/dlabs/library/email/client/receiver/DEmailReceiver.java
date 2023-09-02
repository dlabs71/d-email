package ru.dlabs.library.email.client.receiver;

import static ru.dlabs.library.email.client.receiver.IMAPDClient.DEFAULT_FOLDER_NAME;

import java.util.ArrayList;
import java.util.List;
import ru.dlabs.library.email.message.BaseMessage;
import ru.dlabs.library.email.message.MessageView;
import ru.dlabs.library.email.message.PageRequest;
import ru.dlabs.library.email.message.PageResponse;
import ru.dlabs.library.email.properties.ImapProperties;

/**
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-09-02
 */
public class DEmailReceiver {

    private final ReceiverDClient receiverClient;
    private String folderName = DEFAULT_FOLDER_NAME;
    private final PageRequest pageRequest;

    public DEmailReceiver(ImapProperties properties) {
        this.receiverClient = new IMAPDClient(properties);
        this.pageRequest = PageRequest.of(0, 50);
        if (!properties.getCredentials().isEmpty()) {
            String firstCredentialId = properties.getCredentials().keySet().iterator().next();
            this.credentialId(firstCredentialId);
        }
    }

    public static DEmailReceiver of(ImapProperties properties) {
        return new DEmailReceiver(properties);
    }

    public DEmailReceiver folder(String folderName) {
        this.folderName = folderName;
        return this;
    }

    public DEmailReceiver pageSize(int pageSize) {
        this.pageRequest.setLength(pageSize);
        return this;
    }

    public DEmailReceiver start(int start) {
        this.pageRequest.setStart(start);
        return this;
    }

    public DEmailReceiver credentialId(String credentialId) {
        this.receiverClient.setStore(credentialId);
        return this;
    }

    public PageResponse<MessageView> nextCheckEmail() {
        int totalCount = this.receiverClient.getTotalCount();
        if (totalCount <= 0 || totalCount <= this.pageRequest.getStart()) {
            return PageResponse.of(new ArrayList<>(), totalCount);
        }
        List<MessageView> messageViews = this.receiverClient.checkEmailMessages(folderName, pageRequest);
        pageRequest.incrementStart();
        return PageResponse.of(messageViews, totalCount);
    }

    public PageResponse<BaseMessage> nextReadEmail() {
        int totalCount = this.receiverClient.getTotalCount();
        if (totalCount <= 0 || totalCount <= this.pageRequest.getStart()) {
            return PageResponse.of(new ArrayList<>(), totalCount);
        }
        List<BaseMessage> messageViews = this.receiverClient.readMessages(folderName, pageRequest);
        pageRequest.incrementStart();
        return PageResponse.of(messageViews, totalCount);
    }

    public BaseMessage readEmail(Integer id) {
        if (id == null) {
            return null;
        }
        return this.receiverClient.readMessageById(id);
    }
}
