package ru.dlabs.library.email;

import static ru.dlabs.library.email.client.receiver.IMAPDClient.DEFAULT_INBOX_FOLDER_NAME;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import ru.dlabs.library.email.client.receiver.IMAPDClient;
import ru.dlabs.library.email.client.receiver.ReceiverDClient;
import ru.dlabs.library.email.dto.message.MessageView;
import ru.dlabs.library.email.dto.message.api.IncomingMessage;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;
import ru.dlabs.library.email.dto.pageable.PageRequest;
import ru.dlabs.library.email.dto.pageable.PageResponse;
import ru.dlabs.library.email.exception.SessionException;
import ru.dlabs.library.email.property.ImapProperties;

/**
 * The class implements a facade pattern for receiving messages.
 * After creating the class, you must set a folder name using the {@link DEmailReceiver#folder(String folderName)} method.
 * If you don't, then the client will try to use the "INBOX" folder.
 * <p>
 * If you use several accounts, you must clearly set credentialId by using
 * the {@link DEmailReceiver#credentialId(String credentialId)} method. It creates the correct connection. But if you use only one
 * account to connect, the connection will create with creating the client (in a constructor).
 * <p>
 * Checking and reading emails executes as pageable. By the default page has a size = 50 elements. You can change it
 * by using the {@link DEmailReceiver#pageSize(int pageSize)} method.
 * Then you call {@link DEmailReceiver#nextCheckEmail()} or {@link DEmailReceiver#nextReadEmail()} the page number increase by 1.
 * Accordingly, to read or check email messages from a 0 page, you must use
 * the {@link DEmailReceiver#start(int start)} method before it.
 *
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

    /**
     * The constructor of the class.
     *
     * @param properties properties for connecting to an email server by IMAP protocol
     */
    public DEmailReceiver(ImapProperties properties) {
        this.properties = properties;
        this.receiverClient = new IMAPDClient(properties);
        this.pageRequest = PageRequest.of(0, 50);
        if (properties.getCredentials().size() == 1) {
            String firstCredentialId = properties.getCredentials().keySet().iterator().next();
            this.credentialId(firstCredentialId);
        }
    }

    /**
     * Creates instance of the {@link DEmailReceiver} class
     *
     * @param properties properties for connecting to an email server by IMAP protocol
     *
     * @return instance of the {@link DEmailReceiver} class
     */
    public static DEmailReceiver of(ImapProperties properties) {
        return new DEmailReceiver(properties);
    }

    /**
     * Changes folder for reading messages
     *
     * @param folderName the name of folder
     *
     * @return instance of the {@link DEmailReceiver} class
     */
    public DEmailReceiver folder(String folderName) {
        this.folderName = folderName;
        return this;
    }

    /**
     * Changes page size of selection messages from a folder
     *
     * @param pageSize page size
     *
     * @return instance of the {@link DEmailReceiver} class
     */
    public DEmailReceiver pageSize(int pageSize) {
        this.pageRequest.setLength(pageSize);
        return this;
    }

    /**
     * Sets start page number for reading messages from a folder
     *
     * @param start page number
     *
     * @return instance of the {@link DEmailReceiver} class
     */
    public DEmailReceiver start(int start) {
        this.pageRequest.setStart(start);
        return this;
    }

    /**
     * Sets the using email credentials for connecting to email server
     *
     * @param credentialId credentials identifier from Map credentials in the property connection
     *
     * @return instance of the {@link DEmailReceiver} class
     */
    public DEmailReceiver credentialId(String credentialId) {
        this.credentialId = credentialId;
        this.receiverClient.setStore(credentialId);
        return this;
    }

    /**
     * Returns email receiver object
     *
     * @return object of the {@link EmailParticipant} class
     */
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

    /**
     * Checks email. Returns only common information about messages. The messages won't have a read flag.
     * <p>
     * This method supports page requests. After successful execution, the global {@link DEmailReceiver#pageRequest} parameter
     * will increase the page number.
     * Use the {@link DEmailReceiver#start(int start)} and {@link DEmailReceiver#pageSize(int pageSize)} methods for managing page parameters.
     *
     * @return object of class {@link PageResponse}. Elements in the list of data have the type {@link MessageView}.
     */
    public PageResponse<MessageView> nextCheckEmail() {
        return this.сheckEmail(this.pageRequest);
    }

    /**
     * Checks email. Returns only common information about messages. The messages won't have a read flag.
     * <p>
     * This method supports page requests. You need to manage a page request yourself.
     * Use the pageRequest parameter for it.
     *
     * @param pageRequest the configuration of a page request
     *
     * @return object of class {@link PageResponse}. Elements in the list of data have the type {@link MessageView}.
     */
    public PageResponse<MessageView> сheckEmail(PageRequest pageRequest) {
        int totalCount = this.receiverClient.getTotalCount(folderName);
        if (totalCount <= 0 || totalCount <= this.pageRequest.getStart()) {
            return PageResponse.of(new ArrayList<>(), totalCount);
        }
        List<MessageView> messageViews = this.receiverClient.checkEmailMessages(folderName, pageRequest);
        pageRequest.incrementStart();
        return PageResponse.of(messageViews, totalCount);
    }

    /**
     * Reads email. Returns full information about messages (with a content and attachment). The read flag will be
     * set up in every message.
     * <p>
     * This method supports page requests. After successful execution, the global {@link DEmailReceiver#pageRequest} parameter
     * will increase the page number.
     * Use the {@link DEmailReceiver#start(int start)} and {@link DEmailReceiver#pageSize(int pageSize)} methods for managing page parameters.
     *
     * @return object of class {@link PageResponse}. Elements in the list of data have the type {@link IncomingMessage}.
     */
    public PageResponse<IncomingMessage> nextReadEmail() {
        return this.readEmail(this.pageRequest);
    }

    /**
     * Reads email. Returns full information about messages (with a content and attachment). The read flag will be
     * set up in every message.
     * <p>
     * This method supports page requests. You need to manage a page request yourself.
     * Use the pageRequest parameter for it.
     *
     * @param pageRequest the configuration of a page request
     *
     * @return object of class {@link PageResponse}. Elements in the list of data have the type {@link IncomingMessage}.
     */
    public PageResponse<IncomingMessage> readEmail(PageRequest pageRequest) {
        int totalCount = this.receiverClient.getTotalCount(folderName);
        if (totalCount <= 0 || totalCount <= this.pageRequest.getStart()) {
            return PageResponse.of(new ArrayList<>(), totalCount);
        }
        List<IncomingMessage> messageViews = this.receiverClient.readMessages(folderName, pageRequest);
        pageRequest.incrementStart();
        return PageResponse.of(messageViews, totalCount);
    }

    /**
     * Reads one email message by its identifier.
     *
     * @param id identifier message
     *
     * @return object of the class {@link IncomingMessage}
     */
    public IncomingMessage readEmail(Integer id) {
        if (id == null) {
            return null;
        }
        return this.receiverClient.readMessageById(folderName, id);
    }

    /**
     * Delete all messages in the current folder.
     * Use the method {@link DEmailReceiver#folder(String folderName)} to change folder. By default, it's "INBOX".
     *
     * @return a map with a key is a message ID, and a value is the result of deletion (true or false).
     */
    public Map<Integer, Boolean> clearCurrentFolder() {
        return this.receiverClient.deleteAllMessages(folderName);
    }

    /**
     * Delete several messages by its identifiers
     *
     * @param ids the list of message identifiers
     *
     * @return a map with a key is a message ID, and a value is the result of deletion (true or false).
     */
    public Map<Integer, Boolean> deleteMessages(Collection<Integer> ids) {
        return this.receiverClient.deleteMessages(folderName, ids);
    }

    /**
     * Delete one message by the identifier
     *
     * @param id the message identifier
     *
     * @return true if the message was deleted successfully, or else false
     */
    public boolean deleteMessage(Integer id) {
        return this.receiverClient.deleteMessage(folderName, id);
    }
}
