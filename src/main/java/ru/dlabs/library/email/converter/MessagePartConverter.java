package ru.dlabs.library.email.converter;

import jakarta.mail.BodyPart;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.internet.InternetAddress;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import ru.dlabs.library.email.exception.CheckEmailException;
import ru.dlabs.library.email.exception.ReadMessageException;
import ru.dlabs.library.email.message.AttachmentType;
import ru.dlabs.library.email.message.EmailAttachment;
import ru.dlabs.library.email.message.EmailParticipant;

/**
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-09-02
 */
@UtilityClass
public class MessagePartConverter {

    public Set<EmailParticipant> getParticipants(Message message) {
        try {
            return Arrays.stream(message.getRecipients(Message.RecipientType.TO))
                .map(address -> {
                    if (address instanceof InternetAddress) {
                        InternetAddress internetAddress = (InternetAddress) address;
                        return new EmailParticipant(internetAddress.getAddress(), internetAddress.getPersonal());
                    }
                    return null;
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        } catch (MessagingException e) {
            throw new CheckEmailException(
                "The attempt to get recipients of the message has failed: " + e.getLocalizedMessage(), e
            );
        }
    }

    public List<EmailAttachment> getAttachments(Message message) {
        List<EmailAttachment> attachments = new ArrayList<>();
        try {
            if (message.isMimeType("multipart/*")) {
                Multipart mp = (Multipart) message.getContent();
                int count = mp.getCount();
                for (int i = 0; i < count; i++) {
                    BodyPart bodyPart = mp.getBodyPart(i);
                    byte[] content = getContentDefaultAsBytes(bodyPart);
                    EmailAttachment attachment = EmailAttachment.builder()
                        .name(bodyPart.getFileName())
                        .data(content)
                        .type(AttachmentType.find(bodyPart.getContentType()))
                        .contentType(bodyPart.getContentType())
                        .size(content.length)
                        .build();
                    attachments.add(attachment);
                }
            }
        } catch (MessagingException | IOException e) {
            throw new ReadMessageException(
                "An error occurred in getting attachments from the message: " + e.getLocalizedMessage(), e);
        }
        return attachments;
    }

    public String getContent(Message message) {
        try {
            // this is a nested message
            if (message.isMimeType("message/rfc822")) {
                getContent((Message) message.getContent());
            }
        } catch (MessagingException | IOException e) {
            throw new ReadMessageException(
                "An error occurred in getting content from the message: " + e.getLocalizedMessage(), e);
        }

        return getContentDefault(message);
    }

    public String getContentDefault(Message message) {
        Object content;
        try {
            content = message.getContent();
        } catch (IOException | MessagingException e) {
            throw new ReadMessageException(
                "An error occurred in getting content from the message: " + e.getLocalizedMessage(), e);
        }
        if (content instanceof String) {
            return (String) content;
        } else if (content instanceof InputStream) {
            InputStream is = (InputStream) content;
            return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
        }
        return content.toString();
    }

    public byte[] getContentDefaultAsBytes(BodyPart bodyPart) {
        Object content;
        try {
            content = bodyPart.getContent();
        } catch (IOException | MessagingException e) {
            throw new ReadMessageException(
                "An error occurred in getting content from the message: " + e.getLocalizedMessage(), e);
        }
        if (content instanceof String) {
            return ((String) content).getBytes(StandardCharsets.UTF_8);
        } else if (content instanceof InputStream) {
            InputStream is = (InputStream) content;

            try {
                byte[] buffer = new byte[is.available()];
                is.read(buffer);
                return buffer;
            } catch (IOException e) {
                throw new ReadMessageException(
                    "An error occurred while reading the input stream of the message: " + e.getLocalizedMessage(), e);
            }
        }
        return content.toString().getBytes(StandardCharsets.UTF_8);
    }
}
