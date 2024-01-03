package ru.dlabs71.library.email.tests.client.receiver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import ru.dlabs71.library.email.client.receiver.IMAPDClient;
import ru.dlabs71.library.email.property.ImapProperties;
import ru.dlabs71.library.email.support.AbstractTestsClass;
import ru.dlabs71.library.email.tests.client.receiver.utils.ReceiveTestUtils;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-30</div>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Order(421)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class IMAPDClientTest extends AbstractTestsClass {

    private ImapProperties sslImapProperties;

    @BeforeAll
    public void loadConfig() {
        ImapProperties[] properties = ReceiveTestUtils.loadProperties();
        this.sslImapProperties = properties[0];
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link IMAPDClient#IMAPDClient(ImapProperties)} </li>
     * </ul>
     */
    @Test
    public void clientTest() {
        assertThrows(IllegalArgumentException.class, () -> new IMAPDClient(null));

        IMAPDClient imapdClient = new IMAPDClient(sslImapProperties);
        assertNotNull(imapdClient);
        assertNotNull(imapdClient.getPrincipal());
        assertNotNull(imapdClient.getProtocolName());
        assertEquals(imapdClient.getPrincipal().getEmail(), sslImapProperties.getEmail());
        assertNull(imapdClient.getPrincipal().getName());
    }
}
