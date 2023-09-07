package ru.dlabs.library.email.converter;

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
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import ru.dlabs.library.email.dto.message.common.EmailAttachment;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;
import ru.dlabs.library.email.exception.CheckEmailException;
import ru.dlabs.library.email.exception.ReadMessageException;
import ru.dlabs.library.email.type.AttachmentType;
import ru.dlabs.library.email.util.EmailMessageUtils;
import ru.dlabs.library.email.util.IOUtils;

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

    public EmailAttachment getAttachment(Part part) {
        try {
            byte[] content = getContentDefaultAsBytes(part);
            return EmailAttachment.builder()
                .name(EmailMessageUtils.decodeData(part.getFileName()))
                .data(content)
                .type(AttachmentType.find(part.getContentType()))
                .contentType(EmailMessageUtils.decodeData(part.getContentType()))
                .size(content.length)
                .build();
        } catch (MessagingException e) {
            throw new ReadMessageException(
                "An error occurred in getting attachments from the message: " + e.getLocalizedMessage(), e);
        }
    }

    public ContentAndAttachments getContent(Part part) {
        ContentAndAttachments result = new ContentAndAttachments();
        getContent(part, result);
        return result;
    }

    @SneakyThrows
    public void getContent(Part part, ContentAndAttachments result) {
        try {
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
            // check if the part is attachment
            if (!AttachmentType.UNKNOWN.equals(AttachmentType.find(part.getContentType()))) {
                EmailAttachment attachment = getAttachment(part);
                if (attachment != null) {
                    result.addAttachment(attachment);
                    return;
                }
            }
        } catch (MessagingException | IOException e) {
            throw new ReadMessageException(
                "An error occurred in getting content from the message: " + e.getLocalizedMessage(), e);
        }
        result.addContent(part.getContentType(), getContentDefault(part));
    }

    @SneakyThrows
    public String getContentDefault(Part part) {
        Object content;
        try {
            content = part.getContent();
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
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        part.writeTo(bos);
        bos.close();
        return bos.toString(StandardCharsets.UTF_8.name());
    }

    public byte[] getContentDefaultAsBytes(Part bodyPart) {
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
                return IOUtils.toByteArray(is);
            } catch (IOException e) {
                throw new ReadMessageException(
                    "An error occurred while reading the input stream of the message: " + e.getLocalizedMessage(), e);
            }
        }
        return content.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Getter
    public static class ContentAndAttachments {

        private final List<Content> contents = new ArrayList<>();
        private final List<EmailAttachment> attachments = new ArrayList<>();

        public void addContent(String contentType, String data) {
            this.contents.add(new Content(contentType, data));
        }

        public void addAttachment(EmailAttachment attachment) {
            this.attachments.add(attachment);
        }

        public String getContentByType(String contentType) {
            return this.contents.stream()
                .filter(item -> item.isMimeType(contentType))
                .map(Content::getData)
                .collect(Collectors.joining(","));
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Content {

        private String contentType;
        private String data;

        public boolean isMimeType(String contentTypePattern) {
            try {
                return new ContentType(contentTypePattern).match(this.contentType);
            } catch (ParseException e) {
                return this.contentType.contains(contentTypePattern);
            }
        }
    }
}
