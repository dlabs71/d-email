package ru.dlabs.library.email.tests.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.dlabs.library.email.util.HttpUtils.DEFAULT_ENCODING;

import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import ru.dlabs.library.email.util.HttpUtils;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-15</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
public class HttpUtilTests {

    /**
     * The test for:
     * <ul>
     *     <li>{@link HttpUtils#contentTypeWithCharset(String)}</li>
     *     <li>{@link HttpUtils#contentTypeWithCharset(String, String)}</li>
     * </ul>
     */
    @Test
    public void contentTypeWithEncodingTest() {
        String testValue1 = "text/html; charset=" + DEFAULT_ENCODING.toLowerCase();
        String result1 = HttpUtils.contentTypeWithCharset("text/html");
        assertEquals(result1, testValue1);

        String testValue2 = "text/html; charset=" + DEFAULT_ENCODING.toLowerCase();
        String result2 = HttpUtils.contentTypeWithCharset(testValue2);
        assertEquals(result2, testValue2);

        String testValue3 = "text/html; charset=" + StandardCharsets.ISO_8859_1.name().toLowerCase();
        String result3 = HttpUtils.contentTypeWithCharset("text/html", StandardCharsets.ISO_8859_1.name());
        assertEquals(result3, testValue3);

        String testValue4 = "text/html; charset=" + StandardCharsets.ISO_8859_1.name().toLowerCase();
        String result4 = HttpUtils.contentTypeWithCharset(testValue4);
        assertEquals(result4, testValue4);
    }
}
