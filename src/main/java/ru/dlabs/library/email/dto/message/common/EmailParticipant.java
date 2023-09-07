package ru.dlabs.library.email.dto.message.common;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import ru.dlabs.library.email.exception.ValidationMessageException;

/**
 * Class describe an email message recipient
 *
 * @author Ivanov Danila
 * @version 1.0
 */
@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode(exclude = { "name" })
public class EmailParticipant {

    private String email;
    private String name;

    public EmailParticipant(String email) {
        if (email == null) {
            throw new ValidationMessageException("The recipient's email must not be null");
        }
        this.email = email;
    }
}
