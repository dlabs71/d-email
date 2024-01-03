package ru.dlabs71.library.email.util;

import lombok.experimental.UtilityClass;
import ru.dlabs71.library.email.dto.message.common.Message;
import ru.dlabs71.library.email.exception.ValidationMessageException;

/**
 * The utility class contains methods for validating messages.
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-08-27</div>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@UtilityClass
public class MessageValidator {

    /**
     * The method is common validation messages.
     *
     * @param message a message for validation
     *
     * @throws ValidationMessageException if the message didn't pass the validation
     */
    public void validate(Message message) {
        if (message == null) {
            throw new ValidationMessageException("Email message cannot be null");
        }
        if (message.getRecipients() == null || message.getRecipients().isEmpty()) {
            throw new ValidationMessageException("List recipients cannot be null or empty in the email message");
        }
        if (message.getSubject() == null) {
            throw new ValidationMessageException("Subject cannot be null in the email message");
        }
        if (message.getContents() == null || message.getContents().isEmpty()) {
            throw new ValidationMessageException("Content cannot be null in the email message");
        }
    }
}
