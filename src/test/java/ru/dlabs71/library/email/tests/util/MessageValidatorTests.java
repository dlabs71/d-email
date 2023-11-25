package ru.dlabs71.library.email.tests.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import ru.dlabs71.library.email.dto.message.common.EmailParticipant;
import ru.dlabs71.library.email.dto.message.common.Message;
import ru.dlabs71.library.email.dto.message.outgoing.DefaultOutgoingMessage;
import ru.dlabs71.library.email.dto.message.outgoing.TemplatedOutgoingMessage;
import ru.dlabs71.library.email.exception.ValidationMessageException;
import ru.dlabs71.library.email.util.MessageValidator;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-08-29</div>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Order(120)
public class MessageValidatorTests {

    /**
     * The test for:
     * <ul>
     *     <li>{@link MessageValidator#validate(Message)}</li>
     * </ul>
     */
    @Test
    public void textMessageValidationTest() {
        Set<EmailParticipant> participants = new HashSet<>();
        participants.add(new EmailParticipant("email@example.ru"));

        Exception exception = assertThrows(
            RuntimeException.class,
            () -> MessageValidator.validate(new DefaultOutgoingMessage("Test subject", null, participants, null))
        );
        assertInstanceOf(ValidationMessageException.class, exception);
        assertEquals("Content cannot be null in the email message", exception.getMessage());

        exception = assertThrows(
            RuntimeException.class,
            () -> MessageValidator.validate(new DefaultOutgoingMessage(null, null, participants, null))
        );
        assertInstanceOf(ValidationMessageException.class, exception);
        assertEquals("Subject cannot be null in the email message", exception.getMessage());

        exception = assertThrows(
            RuntimeException.class,
            () -> MessageValidator.validate(new DefaultOutgoingMessage(null, null, new HashSet<>(), null))
        );
        assertInstanceOf(ValidationMessageException.class, exception);
        assertEquals("List recipients cannot be null or empty in the email message", exception.getMessage());
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link MessageValidator#validate(Message)}</li>
     * </ul>
     */
    @Test
    public void templateMessageValidationTest() {
        Set<EmailParticipant> participants = new HashSet<>();
        participants.add(new EmailParticipant("email@example.ru"));

        Exception exception = assertThrows(
            RuntimeException.class,
            () -> MessageValidator.validate(new TemplatedOutgoingMessage("Test subject", null, null, participants))
        );
        assertInstanceOf(ValidationMessageException.class, exception);
        assertEquals("Content cannot be null in the email message", exception.getMessage());

        exception = assertThrows(
            RuntimeException.class,
            () -> MessageValidator.validate(new TemplatedOutgoingMessage(null, null, null, participants))
        );
        assertInstanceOf(ValidationMessageException.class, exception);
        assertEquals("Subject cannot be null in the email message", exception.getMessage());

        exception = assertThrows(
            RuntimeException.class,
            () -> MessageValidator.validate(new TemplatedOutgoingMessage(null, null, null, new HashSet<>()))
        );
        assertInstanceOf(ValidationMessageException.class, exception);
        assertEquals("List recipients cannot be null or empty in the email message", exception.getMessage());
    }
}
