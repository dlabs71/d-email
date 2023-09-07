package ru.dlabs.library.email.util;

import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

@Slf4j
public class EmailMessageUtilTests {

    @Test
    public void createAddressTest1() throws UnsupportedEncodingException, AddressException {
        String email = "abcdefg@example.ru";
        String name = "Username";

        InternetAddress address = EmailMessageUtils.createAddress(email, name);

        Assertions.assertEquals(email, address.getAddress());
        Assertions.assertEquals(name, address.getPersonal());
        log.info("Test #1 is success in creating an address object");
    }

    @Test
    public void createAddressTest2() throws UnsupportedEncodingException, AddressException {
        String email = "abcdefg@example.ru";
        String name = null;

        InternetAddress address = EmailMessageUtils.createAddress(email, name);

        Assertions.assertEquals(email, address.getAddress());
        Assertions.assertEquals(email, address.getPersonal());

        log.info("Test #2 is success in creating an address object");
    }

    @Test
    public void createAddressTest3() throws UnsupportedEncodingException {
        String email = "alskdalskdmlaskmd";
        String name = null;

        try {
            EmailMessageUtils.createAddress(email, name);
        } catch (AddressException e) {
            // it's correct answer. The address doesn't correspond the specification RFC822
        }
        log.info("Test #3 is success in creating an address object");
    }
}
