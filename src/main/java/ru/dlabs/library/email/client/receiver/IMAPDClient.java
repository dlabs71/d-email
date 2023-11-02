package ru.dlabs.library.email.client.receiver;

import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.angus.mail.imap.IMAPStore;
import ru.dlabs.library.email.converter.incoming.BaseMessageConverter;
import ru.dlabs.library.email.converter.incoming.MessageViewConverter;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;
import ru.dlabs.library.email.dto.message.incoming.IncomingMessage;
import ru.dlabs.library.email.dto.message.incoming.MessageView;
import ru.dlabs.library.email.dto.pageable.PageRequest;
import ru.dlabs.library.email.exception.FolderOperationException;
import ru.dlabs.library.email.exception.SessionException;
import ru.dlabs.library.email.property.ImapProperties;
import ru.dlabs.library.email.property.SessionPropertyCollector;
import ru.dlabs.library.email.type.Protocol;
import ru.dlabs.library.email.util.JavaCoreUtils;

/**
 * This class is an implementation of the interface {@link ReceiverDClient}.
 * It provides opportunities for reading messages from email using the IMAP protocol. An IMAP connection will be
 * established by creating the class at once. Then, you need to set up a store - email account. An IMAP connection may
 * work with only one account at one time.
 *
 * <p>You should use the instance of the {@link ImapProperties} class, for configure this class.
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-25</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Slf4j
public class IMAPDClient implements ReceiverDClient {

    /** The using protocol in this class. **/
    private static final Protocol PROTOCOL = Protocol.IMAP;

    /** The default name of the inbox mail folder. **/
    public static final String DEFAULT_INBOX_FOLDER_NAME = "INBOX";

    /** The default name of the outbox mail folder. **/
    public static final String DEFAULT_OUTBOX_FOLDER_NAME = "OUTBOX";


    private final Session session;
    private final Properties properties;
    private final IMAPStore store;
    private final EmailParticipant principal;

    /**
     * Constructor of the class.
     *
     * <p>An IMAP connection and store will be created with the class at once.
     *
     * @param imapProperties properties for creating an IMAP connection
     */
    public IMAPDClient(ImapProperties imapProperties) {
        JavaCoreUtils.notNullArgument(imapProperties, "imapProperties");
        this.principal = EmailParticipant.of(imapProperties.getEmail());
        this.properties = this.collectProperties(imapProperties);
        this.session = this.connect();
        this.store = createStore(this.session, imapProperties.getEmail(), imapProperties.getPassword());
    }

    private Properties collectProperties(ImapProperties imapProperties) {
        Properties props;
        try {
            props = SessionPropertyCollector.createCommonProperties(imapProperties, PROTOCOL);
        } catch (GeneralSecurityException e) {
            throw new SessionException(
                "The creation of a connection failed because of the following error: " + e.getMessage());
        }
        props.put("mail.imap.partialfetch", imapProperties.isPartialFetch());
        props.put("mail.imap.fetchsize", imapProperties.getFetchSize());
        props.put("mail.imap.statuscachetimeout", imapProperties.getStatusCacheTimeout());
        props.put("mail.imap.appendbuffersize", imapProperties.getAppendBufferSize());
        props.put("mail.imap.connectionpoolsize", imapProperties.getConnectionPoolSize());
        props.put("mail.imap.connectionpooltimeout", imapProperties.getConnectionPoolTimeout());
        return props;
    }

    /**
     * Connects to the email server using the IMAP protocol.
     *
     * @return {@link Session} object
     *
     * @throws SessionException The connection to the server has failed. The properties are broken.
     */
    @Override
    public Session connect() throws SessionException {
        try {
            return Session.getInstance(this.properties);
        } catch (Exception e) {
            throw new SessionException(
                "The creation of a connection failed because of the following error: " + e.getMessage());
        }
    }

    /**
     * Returns a using protocol name.
     */
    @Override
    public String getProtocolName() {
        return PROTOCOL.getProtocolName();
    }

    /**
     * Returns name and email address used for connection.
     */
    @Override
    public EmailParticipant getPrincipal() {
        return principal;
    }

    /**
     * Create and connect to store (email account).
     *
     * @param session  has already configured session
     * @param email    an email address of the mailbox
     * @param password a password of the mailbox
     *
     * @return an instance of the {@link IMAPStore} class
     */
    private static IMAPStore createStore(Session session, String email, String password) {
        try {
            log.info("Connect to mailbox: " + email);
            IMAPStore store = (IMAPStore) session.getStore(PROTOCOL.getProtocolName());
            store.connect(email, password);
            return store;
        } catch (MessagingException e) {
            throw new SessionException("Creating session finished with the error: " + e.getMessage(), e);
        }
    }

    /**
     * Returns the total count of email messages in the folder.
     *
     * @param folderName the folder name (For example: INBOX, OUTBOX, etc.)
     *
     * @return total count of messages
     */
    @Override
    public Integer getTotalCount(String folderName) {
        Folder folder = this.openFolderForRead(folderName);
        try {
            return this.getTotalCount(folder);
        } finally {
            closeFolder(folder);
        }
    }

    private Integer getTotalCount(Folder folder) throws FolderOperationException {
        try {
            return folder.getMessageCount();
        } catch (MessagingException e) {
            throw new FolderOperationException(
                "Getting a count of messages in the folder with the name "
                    + folder.getName()
                    + " finished with the error: "
                    + e.getMessage(), e);
        }
    }

    /**
     * Returns short information about messages. without content. Use it when you "check" an email.
     * Messages won't be marked as read.
     *
     * @param folderName  the folder name in you account
     * @param pageRequest selection size information
     *
     * @return list of a {@link MessageView}
     */
    @Override
    public List<MessageView> checkEmailMessages(String folderName, PageRequest pageRequest) {
        Folder folder = this.openFolderForRead(folderName);
        Stream<Message> messages = this.getMessages(folder, pageRequest);

        List<MessageView> result = messages.map(MessageViewConverter::convert).collect(Collectors.toList());
        closeFolder(folder);
        return result;
    }

    /**
     * Return full information about messages. Use it when you "read" an email.
     * Messages will be marked as read.
     *
     * @param folderName  the folder name in you account
     * @param pageRequest selection size information
     *
     * @return list of a {@link IncomingMessage}
     */
    @Override
    public List<IncomingMessage> readMessages(String folderName, PageRequest pageRequest) {
        Folder folder = this.openFolderForWrite(folderName);
        Stream<Message> messages = this.getMessages(folder, pageRequest);

        List<IncomingMessage> result =
            messages.map(BaseMessageConverter::convertToIncomingMessage).collect(Collectors.toList());
        closeFolder(folder);
        return result;
    }

    /**
     * Reads message by its ID (number message).
     * Message will be marked as read.
     *
     * @param folderName the folder name in you account
     * @param id         unique identifier of a message within the folder
     *
     * @return object of a class {@link IncomingMessage}
     */
    @Override
    public IncomingMessage readMessageById(String folderName, int id) {
        Folder folder = this.openFolderForWrite(folderName);

        Message message;
        try {
            message = folder.getMessage(id);
        } catch (MessagingException e) {
            throw new FolderOperationException(
                "Reading the message with id=" + id + " in the folder with name " + folderName
                    + " finished the error: " + e.getMessage(), e);
        }

        IncomingMessage incomingMessage = BaseMessageConverter.convertToIncomingMessage(message);
        closeFolder(folder);
        return incomingMessage;
    }

    /**
     * Opens the folder for only read. Changes are forbidden.
     * If you even read the message, it won't be marked as read.
     *
     * @param folderName the folder name in you account
     *
     * @return an object of the class {@link Folder}
     */
    @Override
    public Folder openFolderForRead(String folderName) {
        if (folderName == null) {
            folderName = DEFAULT_INBOX_FOLDER_NAME;
        }
        return this.openFolder(folderName, Folder.READ_ONLY);
    }

    /**
     * Opens the folder for read and write.
     *
     * @param folderName the folder name in you account
     *
     * @return an object of the class {@link Folder}
     */
    @Override
    public Folder openFolderForWrite(String folderName) {
        if (folderName == null) {
            folderName = DEFAULT_INBOX_FOLDER_NAME;
        }
        return this.openFolder(folderName, Folder.READ_WRITE);
    }

    private Folder openFolder(String folderName, int mode) {
        log.debug("Try to open folder: " + folderName);
        if (store == null) {
            throw new FolderOperationException("The store is null");
        }

        Folder folder;
        try {
            folder = store.getFolder(folderName);
            folder.open(mode);
        } catch (MessagingException e) {
            throw new FolderOperationException(
                "The folder with the name " + folderName + " couldn't be opened because of the following error: "
                    + e.getMessage(), e);
        }
        return folder;
    }

    /**
     * Closes the opened folder.
     *
     * @param folder an object of the class {@link Folder}
     */
    @Override
    public void closeFolder(Folder folder) {
        if (folder == null) {
            return;
        }
        try {
            folder.close();
        } catch (MessagingException e) {
            log.warn("The folder with the name " + folder.getName()
                         + " couldn't be closed because of the following error: " + e.getMessage());
        }
    }

    /**
     * Deletes a message in a folder by its ID.
     *
     * @param folderName the folder name in you account
     * @param id         unique identifier of a message within the folder
     *
     * @return true if the message was deleted successfully, or else false
     */
    @Override
    public boolean deleteMessage(String folderName, int id) {
        Folder folder = this.openFolderForWrite(folderName);

        try {
            Message message = folder.getMessage(id);
            message.setFlag(Flags.Flag.DELETED, true);
        } catch (MessagingException e) {
            log.warn("The message with id=" + id
                         + " wasn't marked as deleted because of the following error: " + e.getMessage());
            this.closeFolder(folder);
            return false;
        }

        try {
            folder.expunge();
        } catch (MessagingException e) {
            throw new FolderOperationException(
                "The folder with the name " + folder.getName()
                    + " couldn't be expunge because of the following error: " + e.getMessage());
        } finally {
            this.closeFolder(folder);
        }
        return true;
    }

    /**
     * Deletes several messages in a folder by their IDs.
     *
     * @param folderName the folder name in you account
     * @param ids        unique identifiers of messages within the folder
     *
     * @return a map with a key is a message ID, and a value is the result of deletion (true or false).
     */
    @Override
    public Map<Integer, Boolean> deleteMessages(String folderName, Collection<Integer> ids) {
        Folder folder = this.openFolderForWrite(folderName);
        Map<Integer, Boolean> result = new HashMap<>();
        ids.forEach(id -> {
            try {
                Message message = folder.getMessage(id);
                message.setFlag(Flags.Flag.DELETED, true);
                result.put(id, true);
            } catch (MessagingException e) {
                log.warn("The message with id=" + id + " wasn't marked as deleted because of the following error: "
                             + e.getMessage());
                result.put(id, false);
            }
        });

        try {
            folder.expunge();
        } catch (MessagingException e) {
            throw new FolderOperationException(
                "The folder with the name " + folder.getName()
                    + " couldn't be expunge because of the following error: "
                    + e.getMessage());
        }

        this.closeFolder(folder);
        return result;
    }

    /**
     * Deletes all messages in a folder.
     *
     * @param folderName the folder name in you account
     *
     * @return a map with a key is a message ID, and a value is the result of deletion (true or false).
     */
    @Override
    public Map<Integer, Boolean> deleteAllMessages(String folderName) {
        Folder folder = this.openFolderForWrite(folderName);
        Map<Integer, Boolean> result = new HashMap<>();
        this.getMessages(folder, PageRequest.of(0, Integer.MAX_VALUE)).forEach(message -> {
            try {
                message.setFlag(Flags.Flag.DELETED, true);
                result.put(message.getMessageNumber(), true);
            } catch (MessagingException e) {
                log.warn("The message with id=" + message.getMessageNumber()
                             + " wasn't marked as deleted because of the following error: " + e.getMessage());
                result.put(message.getMessageNumber(), false);
            }
        });

        try {
            folder.expunge();
        } catch (MessagingException e) {
            throw new FolderOperationException(
                "The folder with the name " + folder.getName()
                    + " couldn't be expunge because of the following error: "
                    + e.getMessage());
        }

        this.closeFolder(folder);
        return result;
    }

    private Stream<Message> getMessages(Folder folder, PageRequest pageRequest) {
        int totalCount = this.getTotalCount(folder);
        int end = pageRequest.getEnd() + 1;
        if (totalCount < end) {
            end = totalCount;
        }
        try {
            return Arrays.stream(folder.getMessages(pageRequest.getStart() + 1, end));
        } catch (MessagingException e) {
            throw new FolderOperationException("The get list message operation has failed: " + e.getMessage());
        }
    }
}
