package ru.dlabs.library.email.util;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Objects;
import java.util.Set;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;

/**
 * The utility class for working with email messages
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

    public final static String CONTENT_TYPE_HDR = "Content-type";
    public final static String FORMAT_HDR = "format";
    public final static String CONTENT_TRANSFER_ENCODING_HDR = "Content-Transfer-Encoding";
    public final static String DEFAULT_ENCODING = StandardCharsets.UTF_8.name();
    public final static String DEFAULT_CONTENT_TYPE = "text/plain; charset=" + DEFAULT_ENCODING;
    public final static String TEXT_CONTENT_TYPE = "text/plain";
    public final static String HTML_CONTENT_TYPE = "text/html";
    public final static String DEFAULT_BINARY_CONTENT_TYPE = "application/octet-stream";

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
     * If any element in the parameter is incorrect then it will miss in the result
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
                log.error("Email address " + recipient + " is incorrect. " + e.getLocalizedMessage());
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
        String decoded;
        try {
            decoded = MimeUtility.decodeText(data);
        } catch (UnsupportedEncodingException e) {
            log.warn("The data (" + data + ") doesn't decode by the following error: " + e.getLocalizedMessage());
            decoded = data;
        }
        return Normalizer.normalize(decoded, Normalizer.Form.NFC);
    }

    /**
     * Returns the string value for the Content-Type header with the 'charset' directive.
     * For example: text/html; charset=utf-8
     *
     * @param contentType data for the 'media-type' directive. A MIME type of resource.
     * @param encoding    the charset encoding standard.
     *
     * @return a prepared string for use as a Content-Type header value
     */
    public String contentTypeWithEncoding(String contentType, String encoding) {
        if (contentType.contains("charset")) {
            return contentType;
        }
        if (encoding == null) {
            return contentType;
        }
        return contentType + "; charset=" + encoding.toLowerCase();
    }

    /**
     * Returns the string value for the Content-Type header with the 'charset' directive.
     * For example: text/html; charset=utf-8
     * It uses the {@link EmailMessageUtils#DEFAULT_ENCODING} constant value
     *
     * @param contentType data for the 'media-type' directive. A MIME type of resource.
     *
     * @return a prepared string for use as a Content-Type header value
     */
    public String contentTypeWithEncoding(String contentType) {
        return contentTypeWithEncoding(contentType, DEFAULT_ENCODING);
    }
}
