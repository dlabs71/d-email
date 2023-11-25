package ru.dlabs71.library.email.dto.message.common;

import jakarta.mail.internet.HeaderTokenizer;
import jakarta.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.dlabs71.library.email.exception.ValidationMessageException;

/**
 * This class describes an email message participant (sender or receiver).
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-08-27</div>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@EqualsAndHashCode(of = { "email" })
public class EmailParticipant {

    private static final String rfc822phrase =
        HeaderTokenizer.RFC822.replace(' ', '\0').replace('\t', '\0');

    @Getter
    private final String email;
    @Getter
    private final String name;

    /**
     * Uses for only toString.
     */
    private String displayViewCache;

    /**
     * The constructor of this class.
     *
     * @param email a participant email. It must is not null.
     */
    public EmailParticipant(String email) {
        if (email == null) {
            throw new ValidationMessageException("The recipient's email must not be null");
        }
        this.email = email;
        this.name = null;
    }

    /**
     * The constructor of this class.
     *
     * @param email a participant email. It must is not null.
     * @param name  a real name of the participant. For example: John Silver, Billy Bones, etc.
     */
    public EmailParticipant(String email, String name) {
        if (email == null) {
            throw new ValidationMessageException("The recipient's email must not be null");
        }
        this.email = email;
        this.name = name;
    }

    /**
     * The builder of this class.
     *
     * @param email a participant email. It must is not null.
     * @param name  a real name of the participant.
     *
     * @return a created object
     */
    public static EmailParticipant of(String email, String name) {
        return new EmailParticipant(email, name);
    }

    /**
     * The builder of this class.
     *
     * @param email a participant email. It must is not null.
     *
     * @return a created object
     */
    public static EmailParticipant of(String email) {
        return new EmailParticipant(email);
    }

    /**
     * Returns a string email address in conformity with RFC822.
     */
    @Override
    public String toString() {
        if (displayViewCache != null) {
            return displayViewCache;
        }
        String encodedName = name;
        if (name != null) {
            try {
                encodedName = MimeUtility.encodeWord(name);
            } catch (UnsupportedEncodingException ignored) {
                encodedName = name;
            }
        }

        if (encodedName != null) {
            displayViewCache = MimeUtility.quote(encodedName, rfc822phrase) + " <" + email + ">";
        } else {
            displayViewCache = "<" + email + ">";
        }
        return displayViewCache;
    }
}
