package ru.dlabs.library.email.util;

import java.nio.charset.StandardCharsets;
import lombok.experimental.UtilityClass;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-15</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@UtilityClass
public class HttpUtils {

    public final static String CONTENT_TYPE_HDR = "Content-type";
    public final static String FORMAT_HDR = "format";
    public final static String CONTENT_TRANSFER_ENCODING_HDR = "Content-Transfer-Encoding";
    public final static String DEFAULT_ENCODING = StandardCharsets.UTF_8.name();
    public final static String DEFAULT_CONTENT_TYPE = "text/plain; charset=" + DEFAULT_ENCODING;
    public final static String TEXT_CONTENT_TYPE = "text/plain";
    public final static String HTML_CONTENT_TYPE = "text/html";
    public final static String DEFAULT_BINARY_CONTENT_TYPE = "application/octet-stream";

    /**
     * Returns the string value for the Content-Type header with the 'charset' directive.
     * For example: text/html; charset=utf-8
     *
     * @param contentType data for the 'media-type' directive. A MIME type of resource.
     * @param encoding    the charset encoding standard.
     *
     * @return a prepared string for use as a Content-Type header value
     */
    public String contentTypeWithEncoding(String contentType, String encoding) {
        if (contentType.contains("charset")) {
            return contentType;
        }
        if (encoding == null) {
            return contentType;
        }
        return contentType + "; charset=" + encoding.toLowerCase();
    }

    /**
     * Returns the string value for the Content-Type header with the 'charset' directive.
     * For example: text/html; charset=utf-8
     * It uses the {@link HttpUtils#DEFAULT_ENCODING} constant value
     *
     * @param contentType data for the 'media-type' directive. A MIME type of resource.
     *
     * @return a prepared string for use as a Content-Type header value
     */
    public String contentTypeWithEncoding(String contentType) {
        return contentTypeWithEncoding(contentType, DEFAULT_ENCODING);
    }

    public String defineEncodingFromHeaderValue(String contentTypeValue) {
        if (contentTypeValue == null || contentTypeValue.isEmpty()) {
            return null;
        }
        if (contentTypeValue.contains("charset")) {
            String value = contentTypeValue.split("charset")[1]
                .replaceAll("\\s", "")
                .trim();
            return value.split(";")[0];
        }
        return null;
    }
}
