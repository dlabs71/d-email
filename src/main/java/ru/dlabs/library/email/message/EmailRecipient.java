package ru.dlabs.library.email.message;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Class describe email message recipient
 *
 * @author Ivanov Danila
 * @version 1.0
 */
@Getter
@ToString
@AllArgsConstructor
@EqualsAndHashCode(exclude = { "name" })
public class EmailRecipient {

    private String email;
    private String name;

    public EmailRecipient(String email) {
        this.email = email;
    }
}
