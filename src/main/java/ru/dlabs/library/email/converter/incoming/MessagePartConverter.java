package ru.dlabs.library.email.converter.incoming;

import jakarta.mail.Address;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.internet.ContentType;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.ParseException;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.dlabs.library.email.dto.message.common.ContentMessage;
import ru.dlabs.library.email.dto.message.common.EmailAttachment;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;
import ru.dlabs.library.email.exception.CheckEmailException;
import ru.dlabs.library.email.exception.ReadMessageException;
import ru.dlabs.library.email.type.AttachmentType;
import ru.dlabs.library.email.util.EmailMessageUtils;
import ru.dlabs.library.email.util.JavaCoreUtils;

/**
 * Utility class for converting different parts of a message {@link jakarta.mail.Message} for using in
 * a message DTO implement {@link ru.dlabs.library.email.dto.message.common.Message} interface.
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-09-02</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Slf4j
@UtilityClass
public class MessagePartConverter {

    /**
     * Returns a Set of {@link EmailParticipant} from message recipients have the type
     * {@link jakarta.mail.Message.RecipientType#TO}.
     *
     * @param message the message from Java API
     *
     * @return Set of {@link EmailParticipant}
     */
    public Set<EmailParticipant> getRecipients(Message message) {
        if (message == null) {
            return null;
        }
        try {
            Address[] recipients = message.getRecipients(Message.RecipientType.TO);
            if (recipients == null || recipients.length == 0) {
                return new HashSet<>();
            }
            return Arrays.stream(recipients).map(address -> {
                if (address instanceof InternetAddress) {
                    InternetAddress internetAddress = (InternetAddress) address;
                    return new EmailParticipant(internetAddress.getAddress(), internetAddress.getPersonal());
                }
                return null;
            }).filter(Objects::nonNull).collect(Collectors.toSet());
        } catch (MessagingException e) {
            throw new CheckEmailException(
                "The attempt to get recipients of the message has failed: " + e.getMessage(), e);
        }
    }

    /**
     * Converts the part of a {@link jakarta.mail.Message} to an object of the class {@link EmailAttachment}.
     *
     * @param part the part of a {@link jakarta.mail.Message}
     *
     * @return an object of the class {@link EmailAttachment}
     */
    public EmailAttachment getAttachment(Part part) {
        if (part == null) {
            return null;
        }
        try {
            byte[] content = getContentDefaultAsBytes(part);
            return EmailAttachment.builder()
                .name(EmailMessageUtils.decodeData(part.getFileName()))
                .data(content)
                .type(AttachmentType.find(part.getContentType()))
                .contentType(EmailMessageUtils.decodeData(part.getContentType()))
                .size(content == null ? 0 : content.length)
                .build();
        } catch (MessagingException e) {
            throw new ReadMessageException(
                "An error occurred in getting attachments from the message: " + e.getMessage(),
                e
            );
        }
    }

    /**
     * Returns the special class {@link ContentAndAttachments}, which contains the message's body and attachments.
     *
     * @param part the income message
     *
     * @return an object of the class {@link ContentAndAttachments}
     */
    public ContentAndAttachments getContent(Part part) {
        ContentAndAttachments result = new ContentAndAttachments();
        getContent(part, result);
        return result;
    }


    /**
     * Gets all the contents and attachments from the email message and fills in the 'result' argument.
     *
     * @param part   an email message
     * @param result a container for result
     */
    public void getContent(Part part, ContentAndAttachments result) {
        if (part == null) {
            return;
        }
        try {
            part.getContent();
        } catch (MessagingException e) {
            throw new ReadMessageException(
                "An error occurred in getting content from the message: " + e.getMessage(),
                e
            );
        } catch (IOException e) {
            log.warn(e.getMessage());
            // This is mean that content message is full empty
            return;
        }

        try {
            if (!Part.ATTACHMENT.equals(part.getDisposition())) {
                if (part.isMimeType("text/*")) {
                    result.addContent(part.getContentType(), (String) part.getContent());
                    return;
                }

                // this is a nested message
                if (part.isMimeType("message/rfc822")) {
                    getContent((Message) part.getContent(), result);
                    return;
                }

                // check if the content has several parts
                if (part.isMimeType("multipart/*")) {
                    Multipart mp = (Multipart) part.getContent();
                    int count = mp.getCount();
                    for (int i = 0; i < count; i++) {
                        getContent(mp.getBodyPart(i), result);
                    }
                    return;
                }
            }

            // check if the part is attachment
            if (!AttachmentType.UNKNOWN.equals(AttachmentType.find(part.getContentType()))) {
                EmailAttachment attachment = getAttachment(part);
                if (attachment != null) {
                    result.addAttachment(attachment);
                    return;
                }
            }

            // adds result
            result.addContent(part.getContentType(), getContentDefault(part));
        } catch (MessagingException | IOException e) {
            throw new ReadMessageException(
                "An error occurred in getting content from the message: " + e.getMessage(),
                e
            );
        }
    }

    /**
     * Tries to get content as string.
     *
     * @param part the part of a {@link jakarta.mail.Message}
     *
     * @return a content email message as string
     */
    public String getContentDefault(Part part) {
        if (part == null) {
            return null;
        }
        Object content;
        try {
            content = part.getContent();
        } catch (IOException ex) {
            log.warn(ex.getMessage());
            return null;
        } catch (MessagingException ex) {
            throw new ReadMessageException(
                "An error occurred in getting content from the message: " + ex.getMessage(),
                ex
            );
        }
        if (content instanceof String) {
            return (String) content;
        } else if (content instanceof InputStream) {
            InputStream is = (InputStream) content;
            return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines()
                .collect(Collectors.joining("\n"));
        }

        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            part.writeTo(bos);
            bos.close();
            return bos.toString(StandardCharsets.UTF_8.name());
        } catch (MessagingException | IOException e) {
            throw new ReadMessageException(
                "An error occurred in writing content to ByteArrayOutputStream: " + e.getMessage(),
                e
            );
        }

    }

    /**
     * Tries to get content as array of bytes.
     *
     * @param bodyPart the part of a {@link jakarta.mail.Message}
     *
     * @return a content email message as an erray of bytes.
     */
    public byte[] getContentDefaultAsBytes(Part bodyPart) {
        if (bodyPart == null) {
            return null;
        }
        Object content;
        try {
            content = bodyPart.getContent();
        } catch (IOException ex) {
            log.warn(ex.getMessage());
            return null;
        } catch (MessagingException ex) {
            throw new ReadMessageException(
                "An error occurred in getting content from the message: " + ex.getMessage(),
                ex
            );
        }
        if (content instanceof String) {
            return ((String) content).getBytes(StandardCharsets.UTF_8);
        } else if (content instanceof InputStream) {
            InputStream is = (InputStream) content;
            try {
                return JavaCoreUtils.toByteArray(is);
            } catch (IOException e) {
                throw new ReadMessageException(
                    "An error occurred while reading the input stream of the message: " + e.getMessage(),
                    e
                );
            }
        }
        return content.toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Special transporting class contains a content and attachments of a message.
     * It is used for only the {@link #getContent(Part)} method.
     * One message can contain several body (content) with different content types.
     */
    @Getter
    public static class ContentAndAttachments {

        private final List<Content> contents = new ArrayList<>();
        private final List<EmailAttachment> attachments = new ArrayList<>();

        /**
         * Adds content to list of contents.
         *
         * @param contentType a value of Content-Type header for this content
         * @param data        a content as string
         */
        public void addContent(String contentType, String data) {
            this.contents.add(new Content(contentType, data));
        }

        /**
         * Adds attachment to the corresponding list.
         *
         * @param attachment instance of the {@link EmailAttachment} class
         */
        public void addAttachment(EmailAttachment attachment) {
            this.attachments.add(attachment);
        }

        /**
         * Returns the content by the content type.
         * If there are several contents with the specified type,
         * then returns all this the contents separated by the '\n'
         *
         * @param contentType the type of the content
         *
         * @return string from contents separated by the '\n'
         */
        public List<ContentMessage> getContentByType(String contentType) {
            return this.contents.stream().filter(item -> item.isMimeType(contentType)).map(item -> new ContentMessage(
                item.getData(),
                item.getContentType()
            )).collect(Collectors.toList());
        }

        /**
         * Returns true if all the lists are empty.
         */
        public boolean isEmpty() {
            return contents.isEmpty() && attachments.isEmpty();
        }
    }

    /**
     * Inner class describing a content of a message.
     */
    @Getter
    @AllArgsConstructor
    public static class Content {

        private String contentType;
        private String data;

        /**
         * Returns true if the content type pattern matches the content type in this class.
         *
         * @param contentTypePattern a content type pattern
         */
        public boolean isMimeType(String contentTypePattern) {
            try {
                return new ContentType(contentTypePattern).match(this.contentType);
            } catch (ParseException e) {
                return this.contentType.contains(contentTypePattern);
            }
        }
    }
}
