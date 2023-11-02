package ru.dlabs.library.email;

import static ru.dlabs.library.email.client.receiver.IMAPDClient.DEFAULT_INBOX_FOLDER_NAME;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import ru.dlabs.library.email.client.receiver.IMAPDClient;
import ru.dlabs.library.email.client.receiver.ReceiverDClient;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;
import ru.dlabs.library.email.dto.message.incoming.IncomingMessage;
import ru.dlabs.library.email.dto.message.incoming.MessageView;
import ru.dlabs.library.email.dto.pageable.PageRequest;
import ru.dlabs.library.email.dto.pageable.PageResponse;
import ru.dlabs.library.email.property.ImapProperties;
import ru.dlabs.library.email.util.JavaCoreUtils;

/**
 * The class implements a facade pattern for receiving messages.
 * After creating the class, you must set a folder name using
 * the {@link DEmailReceiver#folder(String folderName)} method.
 * If you don't it, then the client will try to use the "INBOX" folder.
 *
 * <p>Checking and reading emails executes as pageable. By the default page has the size = 50 elements
 * and start index is 0. You can use a customize page request by using the methods supports pageable requests
 * (e.g. {@link DEmailReceiver#readEmail(PageRequest)} or {@link DEmailReceiver#checkEmail(PageRequest)}).
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-09-02</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
public final class DEmailReceiver {

    /** Default page request. **/
    public static final PageRequest DEFAULT_PAGE_REQUEST = PageRequest.of(0, 50);

    /** The key to the folder name in the metadata of the response. **/
    public static final String FOLDER_NAME_KEY_METADATA = "folderName";
    private final ReceiverDClient receiverClient;

    private String folderName = DEFAULT_INBOX_FOLDER_NAME;

    /**
     * The constructor of the class.
     *
     * @param properties properties for connecting to an email server by IMAP protocol
     */
    public DEmailReceiver(ImapProperties properties) {
        this.receiverClient = new IMAPDClient(properties);
    }

    /**
     * Creates instance of the {@link DEmailReceiver} class.
     *
     * @param properties properties for connecting to an email server by IMAP protocol
     *
     * @return instance of the {@link DEmailReceiver} class
     */
    public static DEmailReceiver of(ImapProperties properties) {
        return new DEmailReceiver(properties);
    }

    /**
     * Changes folder for reading messages.
     *
     * @param folderName the name of folder
     *
     * @return instance of the {@link DEmailReceiver} class
     */
    public synchronized DEmailReceiver folder(String folderName) {
        this.folderName = folderName;
        return this;
    }

    /**
     * Returns email receiver object.
     *
     * @return object of the {@link EmailParticipant} class
     */
    public EmailParticipant receiver() {
        return receiverClient.getPrincipal();
    }

    /**
     * Returns a using folder in the mailbox.
     */
    public String getCurrentFolder() {
        return folderName;
    }

    /**
     * Checks email (first 50 messages). Returns only common information about messages.
     * The messages won't have a read flag.
     *
     * <p>This method uses default page requests from the constant {@link DEmailReceiver#DEFAULT_PAGE_REQUEST}.
     *
     * @return object of class {@link PageResponse}. Elements in the list of data have the type {@link MessageView}.
     */
    public PageResponse<MessageView> checkEmail() {
        return this.checkEmail(DEFAULT_PAGE_REQUEST);
    }

    /**
     * Checks email. Returns only common information about messages. The messages won't have a read flag.
     *
     * <p>This method supports page requests. You need to manage a page request yourself.
     * Use the pageRequest parameter for it.
     *
     * @param pageRequest the configuration of a page request
     *
     * @return object of class {@link PageResponse}. Elements in the list of data have the type {@link MessageView}.
     */
    public PageResponse<MessageView> checkEmail(PageRequest pageRequest) {
        String currentFolderName = folderName;
        int totalCount = this.receiverClient.getTotalCount(currentFolderName);
        if (totalCount <= 0 || totalCount < pageRequest.getStart()) {
            return PageResponse.of(new ArrayList<>(), totalCount);
        }
        List<MessageView> messageViews = this.receiverClient.checkEmailMessages(currentFolderName, pageRequest);

        Map<String, Object> metadata = JavaCoreUtils.makeMap(FOLDER_NAME_KEY_METADATA, currentFolderName);
        return PageResponse.of(messageViews, totalCount, metadata);
    }

    /**
     * Reads email (first 50 messages). Returns full information about messages (with a content and attachment).
     * The read flag will be set up in every message.
     *
     * <p>This method uses default page requests from the constant {@link DEmailReceiver#DEFAULT_PAGE_REQUEST}.
     *
     * @return object of class {@link PageResponse}. Elements in the list of data have the type {@link IncomingMessage}.
     */
    public PageResponse<IncomingMessage> readEmail() {
        return this.readEmail(DEFAULT_PAGE_REQUEST);
    }

    /**
     * Reads email. Returns full information about messages (with a content and attachment). The read flag will be
     * set up in every message.
     *
     * <p>This method supports page requests. You need to manage a page request yourself.
     * Use the pageRequest parameter for it.
     *
     * @param pageRequest the configuration of a page request
     *
     * @return object of class {@link PageResponse}. Elements in the list of data have the type {@link IncomingMessage}.
     */
    public PageResponse<IncomingMessage> readEmail(PageRequest pageRequest) {
        int totalCount = this.receiverClient.getTotalCount(folderName);
        Map<String, Object> metadata = JavaCoreUtils.makeMap(FOLDER_NAME_KEY_METADATA, folderName);
        if (totalCount <= 0 || totalCount <= pageRequest.getStart()) {
            return PageResponse.of(new ArrayList<>(), totalCount, metadata);
        }
        List<IncomingMessage> messages = this.receiverClient.readMessages(folderName, pageRequest);
        return PageResponse.of(messages, totalCount, metadata);
    }

    /**
     * Reads one email message by its identifier.
     *
     * @param id identifier message
     *
     * @return object of the class {@link IncomingMessage}
     */
    public IncomingMessage readMessageById(Integer id) {
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
     * Delete several messages by its identifiers.
     *
     * @param ids the list of message identifiers
     *
     * @return a map with a key is a message ID, and a value is the result of deletion (true or false).
     */
    public Map<Integer, Boolean> deleteMessages(Collection<Integer> ids) {
        return this.receiverClient.deleteMessages(folderName, ids);
    }

    /**
     * Delete one message by the identifier.
     *
     * @param id the message identifier
     *
     * @return true if the message was deleted successfully, or else false
     */
    public boolean deleteMessageById(Integer id) {
        return this.receiverClient.deleteMessage(folderName, id);
    }
}
