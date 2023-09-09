package ru.dlabs.library.email.client.receiver;

import static ru.dlabs.library.email.client.receiver.IMAPDClient.DEFAULT_INBOX_FOLDER_NAME;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import ru.dlabs.library.email.dto.message.MessageView;
import ru.dlabs.library.email.dto.message.api.IncomingMessage;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;
import ru.dlabs.library.email.dto.pageable.PageRequest;
import ru.dlabs.library.email.dto.pageable.PageResponse;
import ru.dlabs.library.email.exception.SessionException;
import ru.dlabs.library.email.property.ImapProperties;

/**
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-09-02
 */
public final class DEmailReceiver {

    private final ReceiverDClient receiverClient;
    private final ImapProperties properties;
    private final PageRequest pageRequest;

    private String folderName = DEFAULT_INBOX_FOLDER_NAME;
    private String credentialId;

    public DEmailReceiver(ImapProperties properties) {
        this.properties = properties;
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
        this.credentialId = credentialId;
        this.receiverClient.setStore(credentialId);
        return this;
    }

    public EmailParticipant receiver() {
        if (this.credentialId == null) {
            throw new SessionException("Store is not set");
        }
        ImapProperties.Credentials credentials = this.properties.getCredentials().getOrDefault(this.credentialId, null);
        if (credentials == null) {
            throw new SessionException("The credential with id=" + credentialId + " doesn't exist");
        }
        return new EmailParticipant(credentials.getEmail());
    }

    public PageResponse<MessageView> nextCheckEmail() {
        int totalCount = this.receiverClient.getTotalCount(folderName);
        if (totalCount <= 0 || totalCount <= this.pageRequest.getStart()) {
            return PageResponse.of(new ArrayList<>(), totalCount);
        }
        List<MessageView> messageViews = this.receiverClient.checkEmailMessages(folderName, pageRequest);
        pageRequest.incrementStart();
        return PageResponse.of(messageViews, totalCount);
    }

    public PageResponse<IncomingMessage> nextReadEmail() {
        int totalCount = this.receiverClient.getTotalCount(folderName);
        if (totalCount <= 0 || totalCount <= this.pageRequest.getStart()) {
            return PageResponse.of(new ArrayList<>(), totalCount);
        }
        List<IncomingMessage> messageViews = this.receiverClient.readMessages(folderName, pageRequest);
        pageRequest.incrementStart();
        return PageResponse.of(messageViews, totalCount);
    }

    public IncomingMessage readEmail(Integer id) {
        if (id == null) {
            return null;
        }
        return this.receiverClient.readMessageById(folderName, id);
    }

    public Map<Integer, Boolean> clearCurrentFolder() {
        return this.receiverClient.deleteAllMessages(folderName);
    }

    public Map<Integer, Boolean> deleteMessages(Collection<Integer> ids) {
        return this.receiverClient.deleteMessages(folderName, ids);
    }

    public boolean deleteMessage(Integer id) {
        return this.receiverClient.deleteMessage(folderName, id);
    }
}
