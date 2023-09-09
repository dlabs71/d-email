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
import ru.dlabs.library.email.converter.BaseMessageConverter;
import ru.dlabs.library.email.converter.MessageViewConverter;
import ru.dlabs.library.email.dto.message.MessageView;
import ru.dlabs.library.email.dto.message.api.IncomingMessage;
import ru.dlabs.library.email.dto.pageable.PageRequest;
import ru.dlabs.library.email.exception.CheckEmailException;
import ru.dlabs.library.email.exception.FolderOperationException;
import ru.dlabs.library.email.exception.SessionException;
import ru.dlabs.library.email.property.ImapProperties;
import ru.dlabs.library.email.property.Protocol;
import ru.dlabs.library.email.util.SessionUtils;

@Slf4j
public class IMAPDClient implements ReceiverDClient {

    public final static String PROTOCOL_NAME = "imap";
    public final static String DEFAULT_INBOX_FOLDER_NAME = "INBOX";
    public final static String DEFAULT_OUTBOX_FOLDER_NAME = "OUTBOX";

    private final ImapProperties imapProperties;
    private final Session session;
    private IMAPStore store;
    private ImapProperties.Credentials currentCredential;

    public IMAPDClient(ImapProperties imapProperties) {
        this.imapProperties = imapProperties;
        try {
            this.session = this.connect();
        } catch (GeneralSecurityException e) {
            throw new SessionException("Creating IMAP session finished with an error: " + e.getLocalizedMessage(), e);
        }
    }

    @Override
    public Session connect() throws GeneralSecurityException {
        Properties props = SessionUtils.createCommonProperties(imapProperties, Protocol.IMAP);
        props.put("mail.imap.partialfetch", imapProperties.isPartialFetch());
        props.put("mail.imap.fetchsize", imapProperties.getFetchSize());
        props.put("mail.imap.statuscachetimeout", imapProperties.getStatusCacheTimeout());
        props.put("mail.imap.appendbuffersize", imapProperties.getAppendBufferSize());
        props.put("mail.imap.connectionpoolsize", imapProperties.getConnectionPoolSize());
        props.put("mail.imap.connectionpooltimeout", imapProperties.getConnectionPoolTimeout());
        return Session.getDefaultInstance(props);
    }

    @Override
    public void setStore(String credentialId) {
        if (!imapProperties.getCredentials().containsKey(credentialId)) {
            throw new SessionException("The credential with id=" + credentialId + " doesn't exist");
        }
        this.currentCredential = imapProperties.getCredentials().get(credentialId);
        try {
            IMAPStore store = (IMAPStore) session.getStore(PROTOCOL_NAME);
            store.connect(this.currentCredential.getEmail(), this.currentCredential.getPassword());
            if (this.store != null) {
                this.store.close();
            }
            this.store = store;
        } catch (MessagingException e) {
            throw new SessionException("Creating session finished with the error: " + e.getLocalizedMessage(), e);
        }
    }

    @Override
    public Integer getTotalCount(String folderName) {
        Folder folder = this.openFolderForRead(folderName);
        try {
            return folder.getMessageCount();
        } catch (MessagingException e) {
            throw new FolderOperationException(
                "Getting a count of messages in the folder with the name " + folderName + " finished with the error: " +
                    e.getLocalizedMessage(), e);
        }
    }

    @Override
    public List<MessageView> checkEmailMessages(String folderName, PageRequest pageRequest) {
        Folder folder = this.openFolderForRead(folderName);
        Stream<Message> messages = this.getMessages(folder, pageRequest);

        List<MessageView> result = messages.map(MessageViewConverter::convert)
            .collect(Collectors.toList());
        closeFolder(folder);
        return result;
    }

    @Override
    public List<IncomingMessage> readMessages(String folderName, PageRequest pageRequest) {
        Folder folder = this.openFolderForWrite(folderName);
        Stream<Message> messages = this.getMessages(folder, pageRequest);

        List<IncomingMessage> result = messages.map(BaseMessageConverter::convertToIncomingMessage)
            .collect(Collectors.toList());
        closeFolder(folder);
        return result;
    }

    @Override
    public IncomingMessage readMessageById(String folderName, int id) {
        Folder folder = this.openFolderForWrite(folderName);

        Message message;
        try {
            message = folder.getMessage(id);
        } catch (MessagingException e) {
            throw new FolderOperationException(
                "Reading the message with id=" + id + " in the folder with name " + folderName +
                    " finished the error: " +
                    e.getLocalizedMessage(), e);
        }

        IncomingMessage incomingMessage = BaseMessageConverter.convertToIncomingMessage(message);
        closeFolder(folder);
        return incomingMessage;
    }

    @Override
    public Folder openFolderForRead(String folderName) {
        if (folderName == null) {
            folderName = DEFAULT_INBOX_FOLDER_NAME;
        }
        return this.openFolder(folderName, Folder.READ_ONLY);
    }

    @Override
    public Folder openFolderForWrite(String folderName) {
        if (folderName == null) {
            folderName = DEFAULT_INBOX_FOLDER_NAME;
        }
        return this.openFolder(folderName, Folder.READ_WRITE);
    }

    private Folder openFolder(String folderName, int mode) {
        if (store == null) {
            throw new FolderOperationException("The store is null");
        }

        Folder folder;
        try {
            folder = store.getFolder(folderName);
            folder.open(mode);
        } catch (MessagingException e) {
            throw new FolderOperationException(
                "The folder with the name " + folderName + " couldn't be opened because of the following error: " +
                    e.getLocalizedMessage(), e);
        }
        return folder;
    }

    @Override
    public void closeFolder(Folder folder) {
        if (folder == null) {
            return;
        }
        try {
            folder.close();
        } catch (MessagingException e) {
            throw new FolderOperationException("The folder with the name " + folder.getName() +
                                                   " couldn't be closed because of the following error: " +
                                                   e.getLocalizedMessage(), e);
        }
    }

    @Override
    public boolean deleteMessage(String folderName, int id) {
        Folder folder = this.openFolderForWrite(folderName);

        try {
            Message message = folder.getMessage(id);
            message.setFlag(Flags.Flag.DELETED, true);
        } catch (MessagingException e) {
            log.warn("The message with id=" + id +
                         " wasn't marked as deleted because of the following error: " +
                         e.getLocalizedMessage());
            this.closeFolder(folder);
            return false;
        }

        try {
            folder.expunge();
        } catch (MessagingException e) {
            throw new FolderOperationException("The folder with the name " + folder.getName() +
                                                   " couldn't be expunge because of the following error: " +
                                                   e.getLocalizedMessage());
        }

        this.closeFolder(folder);
        return true;
    }

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
                log.warn("The message with id=" + id +
                             " wasn't marked as deleted because of the following error: " +
                             e.getLocalizedMessage());
                result.put(id, false);
            }
        });

        try {
            folder.expunge();
        } catch (MessagingException e) {
            throw new FolderOperationException("The folder with the name " + folder.getName() +
                                                   " couldn't be expunge because of the following error: " +
                                                   e.getLocalizedMessage());
        }

        this.closeFolder(folder);
        return result;
    }

    @Override
    public Map<Integer, Boolean> deleteAllMessages(String folderName) {
        Folder folder = this.openFolderForWrite(folderName);
        Map<Integer, Boolean> result = new HashMap<>();
        this.getMessages(folder, PageRequest.of(0, Integer.MAX_VALUE))
            .forEach(message -> {
                try {
                    message.setFlag(Flags.Flag.DELETED, true);
                    result.put(message.getMessageNumber(), true);
                } catch (MessagingException e) {
                    log.warn("The message with id=" + message.getMessageNumber() +
                                 " wasn't marked as deleted because of the following error: " +
                                 e.getLocalizedMessage());
                    result.put(message.getMessageNumber(), false);
                }
            });

        try {
            folder.expunge();
        } catch (MessagingException e) {
            throw new FolderOperationException("The folder with the name " + folder.getName() +
                                                   " couldn't be expunge because of the following error: " +
                                                   e.getLocalizedMessage());
        }

        this.closeFolder(folder);
        return result;
    }

    private Stream<Message> getMessages(Folder folder, PageRequest pageRequest) {
        int totalCount = this.getTotalCount(folder.getName());
        int end = pageRequest.getEnd() + 1;
        if (totalCount < end) {
            end = totalCount;
        }
        try {
            return Arrays.stream(folder.getMessages(pageRequest.getStart() + 1, end));
        } catch (MessagingException e) {
            throw new CheckEmailException("The get list message operation has failed: " + e.getLocalizedMessage());
        }
    }

    private void markedAsSeen(Message message) {
        try {
            message.setFlag(Flags.Flag.SEEN, true);
            message.saveChanges();
        } catch (MessagingException e) {
            log.warn(
                "The message wasn't marked as seen because of the following error: " + e.getLocalizedMessage());
        }
    }
}
