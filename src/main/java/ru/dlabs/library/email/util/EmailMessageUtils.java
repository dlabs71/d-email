package ru.dlabs.library.email.util;

import static ru.dlabs.library.email.util.HttpUtils.DEFAULT_ENCODING;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import java.text.Normalizer;
import java.util.Objects;
import java.util.Set;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;

/**
 * The utility class is for working with email messages and parts of ones.
 *
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-09-26</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Slf4j
@UtilityClass
public class EmailMessageUtils {

    /**
     * It's creating an {@link InternetAddress} object from email and name strings.
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
        log.debug("Creates instance of the InternetAddress class with email={} and name={}", email, senderName);
        InternetAddress address = new InternetAddress(email, senderName, DEFAULT_ENCODING);
        address.validate();
        return address;
    }

    /**
     * It's creating an array of {@link InternetAddress} objects from a list of {@link EmailParticipant} objects.
     * If any element in the parameter is incorrect then it will miss in the result.
     *
     * @param recipients the list of recipients
     *
     * @return the array of {@link InternetAddress} objects
     */
    public InternetAddress[] createAddresses(Set<EmailParticipant> recipients) {
        return recipients.stream().map(recipient -> {
            try {
                return createAddress(recipient.getEmail(), recipient.getName());
            } catch (UnsupportedEncodingException | AddressException e) {
                log.error("Email address " + recipient + " is incorrect. " + e.getMessage());
                return null;
            }
        }).filter(Objects::nonNull).toArray(InternetAddress[]::new);
    }

    /**
     * Return decoded and normalized string.
     * This method use {@link MimeUtility} and {@link Normalizer} for its work.
     *
     * @param data encoded string data
     *
     * @return decoded string data
     */
    public String decodeData(String data) {
        log.debug("Tries to decode the data {}", data);
        if (data == null) {
            return null;
        }
        String decoded;
        try {
            decoded = MimeUtility.decodeText(data);
        } catch (UnsupportedEncodingException e) {
            log.warn("The data (" + data + ") doesn't decode by the following error: " + e.getMessage());
            decoded = data;
        }
        return Normalizer.normalize(decoded, Normalizer.Form.NFC);
    }
}
