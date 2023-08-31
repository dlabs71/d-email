package ru.dlabs.library.email.utils;

import lombok.experimental.UtilityClass;
import ru.dlabs.library.email.exception.ValidationMessageException;
import ru.dlabs.library.email.message.Message;

@UtilityClass
public class MessageValidator {

    public void validate(Message message) {
        if (message.getRecipientEmail() == null || message.getRecipientEmail().isEmpty()) {
            throw new ValidationMessageException("List recipients cannot be null or empty in the email message");
        }
        if (message.getSubject() == null) {
            throw new ValidationMessageException("Subject cannot be null in the email message");
        }
        if (message.getContent() == null) {
//            throw new ValidationMessageException("Content cannot be null in the email message");
        }
    }
}
