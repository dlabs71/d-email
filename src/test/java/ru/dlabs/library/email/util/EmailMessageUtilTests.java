package ru.dlabs.library.email.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static ru.dlabs.library.email.util.EmailMessageUtils.DEFAULT_ENCODING;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeUtility;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import ru.dlabs.library.email.dto.message.common.EmailParticipant;

@Slf4j
public class EmailMessageUtilTests {

    /**
     * The test for:
     * <ul>
     *     <li>{@link EmailMessageUtils#createAddress(String, String)}</li>
     * </ul>
     */
    @Test
    public void createAddressTest1() throws UnsupportedEncodingException, AddressException {
        String email1 = "abcdefg@example.ru";
        String name1 = "Username";

        InternetAddress address1 = EmailMessageUtils.createAddress(email1, name1);

        assertNotNull(address1);
        assertEquals(email1, address1.getAddress());
        assertEquals(name1, address1.getPersonal());

        // -------------------------------------------------------

        String email2 = "abcdefg@example.ru";
        String name2 = null;

        InternetAddress address2 = EmailMessageUtils.createAddress(email2, name2);

        assertNotNull(address2);
        assertEquals(email2, address2.getAddress());
        assertEquals(email2, address2.getPersonal());

        // -------------------------------------------------------

        String email3 = "alskdalskdmlaskmd";
        String name3 = null;

        try {
            EmailMessageUtils.createAddress(email3, name3);
        } catch (AddressException e) {
            // it's correct answer. The address doesn't correspond the specification RFC822
        }
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link EmailMessageUtils#createAddresses(Set)}</li>
     * </ul>
     */
    @Test
    public void createAddressTest2() throws UnsupportedEncodingException, AddressException {
        HashSet<EmailParticipant> participants = new HashSet<>();
        participants.add(new EmailParticipant("abcdefg@example.ru", "Username"));
        participants.add(new EmailParticipant("abcdefg2@example.ru", null));
        participants.add(new EmailParticipant("alskdalskdmlaskmd", null));

        EmailParticipant[] participantsArray = participants.toArray(new EmailParticipant[0]);

        InternetAddress[] addresses = EmailMessageUtils.createAddresses(participants);
        assertEquals(addresses.length, 2);

        // -------------------------------------------------------

        InternetAddress address1 = addresses[0];
        EmailParticipant participant1 = participantsArray[0];

        assertNotNull(address1);
        assertEquals(participant1.getEmail(), address1.getAddress());
        assertEquals(participant1.getName(), address1.getPersonal());

        // -------------------------------------------------------

        InternetAddress address2 = addresses[1];
        EmailParticipant participant2 = participantsArray[1];

        assertNotNull(address2);
        assertEquals(participant2.getEmail(), address2.getAddress());
        assertEquals(participant2.getEmail(), address2.getPersonal());
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link EmailMessageUtils#decodeData(String)}</li>
     * </ul>
     */
    @Test
    public void decodeDataTest() throws UnsupportedEncodingException {
        String data1 = "qwertyuiop[1234345";
        String data2 = MimeUtility.encodeText("йцукенгшqwertyui123", "UTF-8", "B");

        String result1 = EmailMessageUtils.decodeData(data1);
        assertEquals(result1, data1);

        String result2 = EmailMessageUtils.decodeData(data2);
        assertEquals(result2, "йцукенгшqwertyui123");
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link EmailMessageUtils#contentTypeWithEncoding(String)}</li>
     *     <li>{@link EmailMessageUtils#contentTypeWithEncoding(String, String)}</li>
     * </ul>
     */
    @Test
    public void contentTypeWithEncodingTest() {
        String testValue1 = "text/html; charset=" + DEFAULT_ENCODING.toLowerCase();
        String result1 = EmailMessageUtils.contentTypeWithEncoding("text/html");
        assertEquals(result1, testValue1);

        String testValue2 = "text/html; charset=" + DEFAULT_ENCODING.toLowerCase();
        String result2 = EmailMessageUtils.contentTypeWithEncoding(testValue2);
        assertEquals(result2, testValue2);

        String testValue3 = "text/html; charset=" + StandardCharsets.ISO_8859_1.name().toLowerCase();
        String result3 = EmailMessageUtils.contentTypeWithEncoding("text/html", StandardCharsets.ISO_8859_1.name());
        assertEquals(result3, testValue3);

        String testValue4 = "text/html; charset=" + StandardCharsets.ISO_8859_1.name().toLowerCase();
        String result4 = EmailMessageUtils.contentTypeWithEncoding(testValue4);
        assertEquals(result4, testValue4);
    }
}
