package ru.dlabs.library.email.dto.message.common;

import jakarta.mail.internet.HeaderTokenizer;
import jakarta.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.dlabs.library.email.exception.ValidationMessageException;

/**
 * Class describe an email message recipient
 *
 * @author Ivanov Danila
 * @version 1.0
 */
@EqualsAndHashCode(of = { "email" })
public class EmailParticipant {

    private static final String rfc822phrase =
        HeaderTokenizer.RFC822.replace(' ', '\0').replace('\t', '\0');

    @Getter
    private final String email;
    @Getter
    private String name;
    private String displayViewCache;

    public EmailParticipant(String email) {
        if (email == null) {
            throw new ValidationMessageException("The recipient's email must not be null");
        }
        this.email = email;
    }

    public EmailParticipant(String email, String name) {
        this(email);
        this.name = name;
    }

    public static EmailParticipant of(String email, String name) {
        return new EmailParticipant(email, name);
    }

    public static EmailParticipant of(String email) {
        return new EmailParticipant(email);
    }

    @Override
    public String toString() {
        if (displayViewCache != null) {
            return displayViewCache;
        }
        String encodedName = name;
        if (name != null) {
            try {
                encodedName = MimeUtility.encodeWord(name);
            } catch (UnsupportedEncodingException ex) {
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
