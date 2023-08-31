package ru.dlabs.library.email.client.receiver;

import static jakarta.mail.Folder.READ_ONLY;

import jakarta.mail.Address;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import org.eclipse.angus.mail.imap.IMAPStore;
import ru.dlabs.library.email.message.EmailParticipant;
import ru.dlabs.library.email.message.MessageView;
import ru.dlabs.library.email.message.TextMessage;
import ru.dlabs.library.email.properties.ImapProperties;
import ru.dlabs.library.email.properties.Protocol;
import ru.dlabs.library.email.utils.SessionUtils;

public class IMAPDClient implements ReceiverDClient {

    public final static String IMAP_PROTOCOL_NAME = "imap";

    private final ImapProperties imapProperties;
    private final Session session;
    private IMAPStore store;

    public IMAPDClient(ImapProperties imapProperties) throws GeneralSecurityException {
        this.imapProperties = imapProperties;
        this.session = this.connect();
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
    public void setStore(String credentialId) throws MessagingException {
        if (!imapProperties.getCredentials().containsKey(credentialId)) {
            throw new RuntimeException("The credential ID");
        }
        ImapProperties.Credentials credentials = imapProperties.getCredentials().get(credentialId);
        IMAPStore store = (IMAPStore) session.getStore(IMAP_PROTOCOL_NAME);
        store.connect(credentials.getEmail(), credentials.getPassword());
        this.store = store;
    }

    @Override
    public List<MessageView> checkEmailMessages() throws MessagingException {
        Folder defaultFolder = store.getFolder("INBOX");
        defaultFolder.open(READ_ONLY);
        return Arrays.stream(defaultFolder.getMessages()).map(item -> {
            MessageView.MessageViewBuilder builder = MessageView.builder();

            Set<EmailParticipant> recipients = null;
            try {
                recipients = Arrays.stream(item.getRecipients(Message.RecipientType.TO))
                    .map(address -> {
                        if (address instanceof InternetAddress internetAddress) {
                            return new EmailParticipant(internetAddress.getAddress(), internetAddress.getPersonal());
                        }
                        return null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            builder.recipientEmail(recipients);

            try {
                builder.subject(item.getSubject());
            } catch (MessagingException e) {
                e.printStackTrace();
            }

            Address[] froms = new Address[0];
            try {
                froms = item.getFrom();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            if (froms.length > 0) {
                InternetAddress internetAddress = (InternetAddress) froms[0];
                builder.sender(new EmailParticipant(internetAddress.getAddress(), internetAddress.getPersonal()));
            }
            return builder.build();
        }).collect(Collectors.toList());
    }

    @Override
    public List<TextMessage> getEmailMessages() {
        return null;
    }
}
