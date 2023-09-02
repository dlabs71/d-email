package ru.dlabs.library.email;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import ru.dlabs.library.email.exception.ValidationMessageException;
import ru.dlabs.library.email.message.EmailParticipant;
import ru.dlabs.library.email.message.TemplatedMessage;
import ru.dlabs.library.email.message.TextMessage;
import ru.dlabs.library.email.utils.MessageValidator;

public class MessageValidatorTests {

    @Test
    public void textMessageValidationTest() {
        Set<EmailParticipant> participants = new HashSet<>();
        participants.add(new EmailParticipant("email@example.ru"));

        Exception exception = assertThrows(
            RuntimeException.class,
            () -> MessageValidator.validate(new TextMessage("Test subject", null, participants, null))
        );
        assertInstanceOf(ValidationMessageException.class, exception);
        assertEquals(exception.getMessage(), "Content cannot be null in the email message");

        exception = assertThrows(
            RuntimeException.class,
            () -> MessageValidator.validate(new TextMessage(null, null, participants, null))
        );
        assertInstanceOf(ValidationMessageException.class, exception);
        assertEquals(exception.getMessage(), "Subject cannot be null in the email message");

        exception = assertThrows(
            RuntimeException.class,
            () -> MessageValidator.validate(new TextMessage(null, null, new HashSet<>(), null))
        );
        assertInstanceOf(ValidationMessageException.class, exception);
        assertEquals(exception.getMessage(), "List recipients cannot be null or empty in the email message");
    }

    @Test
    public void templateMessageValidationTest() {
        Set<EmailParticipant> participants = new HashSet<>();
        participants.add(new EmailParticipant("email@example.ru"));

        Exception exception = assertThrows(
            RuntimeException.class,
            () -> MessageValidator.validate(new TemplatedMessage("Test subject", null, null, participants))
        );
        assertInstanceOf(ValidationMessageException.class, exception);
        assertEquals(exception.getMessage(), "Content cannot be null in the email message");

        exception = assertThrows(
            RuntimeException.class,
            () -> MessageValidator.validate(new TemplatedMessage(null, null, null, participants))
        );
        assertInstanceOf(ValidationMessageException.class, exception);
        assertEquals(exception.getMessage(), "Subject cannot be null in the email message");

        exception = assertThrows(
            RuntimeException.class,
            () -> MessageValidator.validate(new TemplatedMessage(null, null, null, new HashSet<>()))
        );
        assertInstanceOf(ValidationMessageException.class, exception);
        assertEquals(exception.getMessage(), "List recipients cannot be null or empty in the email message");
    }
}
