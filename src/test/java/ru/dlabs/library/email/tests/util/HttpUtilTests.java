package ru.dlabs.library.email.tests.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.dlabs.library.email.util.ProtocolUtils.DEFAULT_ENCODING;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import ru.dlabs.library.email.util.ProtocolUtils;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-15</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Order(100)
public class HttpUtilTests {

    /**
     * The test for:
     * <ul>
     *     <li>{@link ProtocolUtils#contentTypeWithCharset(String)}</li>
     *     <li>{@link ProtocolUtils#contentTypeWithCharset(String, String)}</li>
     * </ul>
     */
    @Test
    public void contentTypeWithEncodingTest() {
        String testValue1 = "text/html; charset=" + DEFAULT_ENCODING.toLowerCase();
        String result1 = ProtocolUtils.contentTypeWithCharset("text/html");
        assertEquals(testValue1, result1);

        String testValue2 = "text/html; charset=" + DEFAULT_ENCODING.toLowerCase();
        String result2 = ProtocolUtils.contentTypeWithCharset(testValue2);
        assertEquals(testValue2, result2);

        String testValue3 = "text/html; charset=" + StandardCharsets.ISO_8859_1.name().toLowerCase();
        String result3 = ProtocolUtils.contentTypeWithCharset("text/html", StandardCharsets.ISO_8859_1.name());
        assertEquals(testValue3, result3);

        String testValue4 = "text/html; charset=" + StandardCharsets.ISO_8859_1.name().toLowerCase();
        String result4 = ProtocolUtils.contentTypeWithCharset(testValue4);
        assertEquals(testValue4, result4);
    }
}
