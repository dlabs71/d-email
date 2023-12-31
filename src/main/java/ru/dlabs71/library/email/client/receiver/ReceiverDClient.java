package ru.dlabs71.library.email.client.receiver;

import jakarta.mail.Folder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import ru.dlabs71.library.email.client.DClient;
import ru.dlabs71.library.email.dto.message.incoming.IncomingMessage;
import ru.dlabs71.library.email.dto.message.incoming.MessageView;
import ru.dlabs71.library.email.dto.pageable.PageRequest;

/**
 * The general interface for a receiver client (IMAP, POP3, etc.).
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-08-31</div>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
public interface ReceiverDClient extends DClient {

    /**
     * Returns the total count of email messages in the folder.
     *
     * @param folderName the folder name (For example: INBOX, OUTBOX, etc.)
     *
     * @return total count of messages
     */
    Integer getTotalCount(String folderName);

    /**
     * Returns short information about messages. without content. Use it when you "check" an email.
     * Messages won't be marked as read.
     *
     * @param folderName  the folder name in you account
     * @param pageRequest selection size information
     *
     * @return list of a {@link MessageView}
     */
    List<MessageView> checkEmailMessages(String folderName, PageRequest pageRequest);

    /**
     * Return full information about messages. Use it when you "read" an email.
     * Messages will be marked as read.
     *
     * @param folderName  the folder name in you account
     * @param pageRequest selection size information
     *
     * @return list of a {@link IncomingMessage}
     */
    List<IncomingMessage> readMessages(String folderName, PageRequest pageRequest);

    /**
     * Reads message by its ID (number message).
     * Message will be marked as read.
     *
     * @param folderName the folder name in you account
     * @param id         unique identifier of a message within the folder
     *
     * @return object of a class {@link IncomingMessage}
     */
    IncomingMessage readMessageById(String folderName, int id);

    /**
     * Opens the folder for only read. Changes are forbidden.
     * If you even read the message, it won't be marked as read.
     *
     * @param folderName the folder name in you account
     *
     * @return an object of the class {@link Folder}
     */
    Folder openFolderForRead(String folderName);

    /**
     * Opens the folder for read and write.
     *
     * @param folderName the folder name in you account
     *
     * @return an object of the class {@link Folder}
     */
    Folder openFolderForWrite(String folderName);

    /**
     * Closes the opened folder.
     *
     * @param folder an object of the class {@link Folder}
     */
    void closeFolder(Folder folder);

    /**
     * Deletes a message in a folder by its ID.
     *
     * @param folderName the folder name in you account
     * @param id         unique identifier of a message within the folder
     *
     * @return true if the message was deleted successfully, or else false
     */
    boolean deleteMessage(String folderName, int id);

    /**
     * Deletes several messages in a folder by their IDs.
     *
     * @param folderName the folder name in you account
     * @param ids        unique identifiers of messages within the folder
     *
     * @return a map with a key is a message ID, and a value is the result of deletion (true or false).
     */
    Map<Integer, Boolean> deleteMessages(String folderName, Collection<Integer> ids);

    /**
     * Deletes all messages in a folder.
     *
     * @param folderName the folder name in you account
     *
     * @return a map with a key is a message ID, and a value is the result of deletion (true or false).
     */
    Map<Integer, Boolean> deleteAllMessages(String folderName);
}
