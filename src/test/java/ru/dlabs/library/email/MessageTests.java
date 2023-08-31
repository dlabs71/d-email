package ru.dlabs.library.email;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Set;
import org.junit.jupiter.api.Test;
import ru.dlabs.library.email.exception.ValidationMessageException;
import ru.dlabs.library.email.message.EmailParticipant;
import ru.dlabs.library.email.message.TemplatedMessage;
import ru.dlabs.library.email.message.TextMessage;

public class MessageTests {

    @Test
    public void textMessageValidationTest() {
        Exception exception = assertThrows(
            RuntimeException.class,
            () -> new TextMessage(Set.of(new EmailParticipant("email@example.ru")), "Test subject", null)
        );
        assertInstanceOf(ValidationMessageException.class, exception);
        assertEquals(exception.getMessage(), "Content cannot be null in the email message");

        exception = assertThrows(
            RuntimeException.class,
            () -> new TextMessage(Set.of(new EmailParticipant("email@example.ru")), null, null)
        );
        assertInstanceOf(ValidationMessageException.class, exception);
        assertEquals(exception.getMessage(), "Subject cannot be null in the email message");

        exception = assertThrows(
            RuntimeException.class,
            () -> new TextMessage(Set.of(), null, null)
        );
        assertInstanceOf(ValidationMessageException.class, exception);
        assertEquals(exception.getMessage(), "List recipients cannot be null or empty in the email message");
    }

    @Test
    public void templateMessageValidationTest() {
        Exception exception = assertThrows(
            RuntimeException.class,
            () -> new TemplatedMessage(Set.of(new EmailParticipant("email@example.ru")), "Test subject", null, null)
        );
        assertInstanceOf(ValidationMessageException.class, exception);
        assertEquals(exception.getMessage(), "Content cannot be null in the email message");

        exception = assertThrows(
            RuntimeException.class,
            () -> new TemplatedMessage(Set.of(new EmailParticipant("email@example.ru")), null, null, null)
        );
        assertInstanceOf(ValidationMessageException.class, exception);
        assertEquals(exception.getMessage(), "Subject cannot be null in the email message");

        exception = assertThrows(
            RuntimeException.class,
            () -> new TemplatedMessage(Set.of(), null, null, null)
        );
        assertInstanceOf(ValidationMessageException.class, exception);
        assertEquals(exception.getMessage(), "List recipients cannot be null or empty in the email message");
    }
}
