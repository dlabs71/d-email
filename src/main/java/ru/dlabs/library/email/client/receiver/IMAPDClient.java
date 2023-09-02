package ru.dlabs.library.email.client.receiver;

import static jakarta.mail.Folder.READ_ONLY;

import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.eclipse.angus.mail.imap.IMAPStore;
import ru.dlabs.library.email.converter.BaseMessageConverter;
import ru.dlabs.library.email.converter.MessageViewConverter;
import ru.dlabs.library.email.exception.CheckEmailException;
import ru.dlabs.library.email.exception.FolderOperationException;
import ru.dlabs.library.email.exception.SessionException;
import ru.dlabs.library.email.message.BaseMessage;
import ru.dlabs.library.email.message.MessageView;
import ru.dlabs.library.email.message.PageRequest;
import ru.dlabs.library.email.properties.ImapProperties;
import ru.dlabs.library.email.properties.Protocol;
import ru.dlabs.library.email.utils.SessionUtils;

public class IMAPDClient implements ReceiverDClient {

    public final static String IMAP_PROTOCOL_NAME = "imap";
    public final static String DEFAULT_FOLDER_NAME = "INBOX";

    private final ImapProperties imapProperties;
    private final Session session;
    private IMAPStore store;

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
            throw new RuntimeException("The credential ID");
        }
        ImapProperties.Credentials credentials = imapProperties.getCredentials().get(credentialId);
        try {
            IMAPStore store = (IMAPStore) session.getStore(IMAP_PROTOCOL_NAME);
            store.connect(credentials.getEmail(), credentials.getPassword());
            this.store = store;
        } catch (MessagingException e) {
            throw new SessionException("Creating session finished with the error: " + e.getLocalizedMessage(), e);
        }
    }

    @Override
    public Integer getTotalCount() {
        return getTotalCount(DEFAULT_FOLDER_NAME);
    }

    @Override
    public Integer getTotalCount(String folderName) {
        Folder folder = this.openFolder(folderName);
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
        Folder folder = this.openFolder(folderName);
        Stream<Message> messages = this.getMessages(folder, pageRequest);
        closeFolder(folder);

        return messages.map(MessageViewConverter::convert)
            .collect(Collectors.toList());
    }

    @Override
    public List<MessageView> checkEmailMessages(PageRequest pageRequest) {
        return this.checkEmailMessages(DEFAULT_FOLDER_NAME, pageRequest);
    }

    @Override
    public List<BaseMessage> readMessages(String folderName, PageRequest pageRequest) {
        Folder folder = this.openFolder(folderName);
        Stream<Message> messages = this.getMessages(folder, pageRequest);
        closeFolder(folder);

        return messages.map(BaseMessageConverter::convert)
            .collect(Collectors.toList());
    }

    @Override
    public List<BaseMessage> readMessages(PageRequest pageRequest) {
        return this.readMessages(DEFAULT_FOLDER_NAME, pageRequest);
    }

    @Override
    public BaseMessage readMessageById(String folderName, int id) {
        Folder folder = this.openFolder(folderName);

        Message message;
        try {
            message = folder.getMessage(id);
        } catch (MessagingException e) {
            throw new FolderOperationException(
                "Reading the message with id=" + id + " in the folder with name " + folderName +
                    " finished the error: " +
                    e.getLocalizedMessage(), e);
        } finally {
            closeFolder(folder);
        }

        return BaseMessageConverter.convert(message);
    }

    @Override
    public BaseMessage readMessageById(int id) {
        return this.readMessageById(DEFAULT_FOLDER_NAME, id);
    }

    @Override
    public Folder openFolder(String folderName) {
        if (folderName == null) {
            folderName = DEFAULT_FOLDER_NAME;
        }
        if (store == null) {
            throw new FolderOperationException("The store is null");
        }

        Folder folder;
        try {
            folder = store.getFolder(folderName);
            folder.open(READ_ONLY);
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

    private Stream<Message> getMessages(Folder folder, PageRequest pageRequest) {
        int totalCount = this.getTotalCount();
        int end = pageRequest.getEnd() + 1;
        if (totalCount < end) {
            end = totalCount;
        }
        try {
            return Arrays.stream(folder.getMessages(pageRequest.getStart() + 1, end));
        } catch (MessagingException e) {
            throw new CheckEmailException("The check message operation has failed: " + e.getLocalizedMessage());
        }
    }


}
