package ru.dlabs.library.email.utils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.dlabs.library.email.message.EmailParticipant;

/**
 * @author Ivanov Danila
 * @version 1.0
 */
@Slf4j
@UtilityClass
public class EmailMessageUtils {

    public final static String CONTENT_TYPE_HDR = "Content-type";
    public final static String FORMAT_HDR = "format";
    public final static String CONTENT_TRANSFER_ENCODING_HDR = "Content-Transfer-Encoding";
    public final static String DEFAULT_ENCODING = StandardCharsets.UTF_8.name();
    public final static String DEFAULT_CONTENT_TYPE = "text/html; charset=" + DEFAULT_ENCODING;

    public void addCommonHeaders(MimeMessage msg) throws MessagingException {
        msg.addHeader(FORMAT_HDR, "flowed");
        msg.addHeader(CONTENT_TRANSFER_ENCODING_HDR, "8bit");
    }

    /**
     * It's creating an {@link InternetAddress} object from email and name strings
     *
     * @param email the mail address string (RFC822 format)
     * @param name  the personal name
     *
     * @return {@link InternetAddress} object
     *
     * @throws UnsupportedEncodingException if the personal name can't be encoded in the given charset
     * @throws AddressException             if the email address doesn't correspond to the specification RFC822
     */
    public InternetAddress createAddress(String email, String name)
        throws UnsupportedEncodingException, AddressException {
        String senderName = name == null ? email : name;
        InternetAddress address = new InternetAddress(email, senderName, DEFAULT_ENCODING);
        address.validate();
        return address;
    }

    /**
     * It's creating an array of {@link InternetAddress} objects from a list of {@link EmailParticipant} objects.
     *
     * @param recipients the list of recipients
     *
     * @return the array of {@link InternetAddress} objects
     */
    public InternetAddress[] createAddresses(Set<EmailParticipant> recipients) {
        return recipients.stream()
            .map(recipient -> {
                try {
                    return createAddress(recipient.getEmail(), recipient.getName());
                } catch (UnsupportedEncodingException | AddressException e) {
                    log.error("Email address " + recipient + " is incorrect. " + e.getLocalizedMessage(), e);
                    return null;
                }
            })
            .filter(Objects::nonNull)
            .collect(Collectors.toList())
            .toArray(new InternetAddress[] { });
    }
}
