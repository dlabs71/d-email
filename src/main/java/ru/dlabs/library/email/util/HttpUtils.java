package ru.dlabs.library.email.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import lombok.experimental.UtilityClass;

/**
 * The utility class contains helpful methods for work with different parts of HTTP.
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

    public static final String CONTENT_TYPE_HDR = "Content-type";
    public static final String CONTENT_TRANSFER_ENCODING_HDR = "Content-Transfer-Encoding";
    public static final String DEFAULT_ENCODING = StandardCharsets.UTF_8.name();
    public static final String TEXT_CONTENT_TYPE = "text/plain";
    public static final String HTML_CONTENT_TYPE = "text/html";
    public static final String DEFAULT_BINARY_CONTENT_TYPE = "application/octet-stream";

    /**
     * Returns the string value for the Content-Type header with the 'charset' directive.
     * For example: text/html; charset=utf-8
     *
     * @see HttpUtils#contentTypeWithCharset(String, String)
     */
    public String contentTypeWithCharset(String contentType, Charset charset) {
        return contentTypeWithCharset(contentType, charset.displayName());
    }

    /**
     * Returns the string value for the Content-Type header with the 'charset' directive.
     * For example: text/html; charset=utf-8
     *
     * @param contentType data for the 'media-type' directive. A MIME type of resource.
     * @param charset     the charset encoding standard.
     *
     * @return a prepared string for use as a Content-Type header value
     */
    public String contentTypeWithCharset(String contentType, String charset) {
        if (contentType.contains("charset")) {
            return contentType;
        }
        if (charset == null) {
            return contentType;
        }
        return contentType + "; charset=" + charset.toLowerCase();
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
    public String contentTypeWithCharset(String contentType) {
        return contentTypeWithCharset(contentType, DEFAULT_ENCODING);
    }

    /**
     * Returns value from 'charset' directive from a Content-Type header value.
     *
     * @param contentTypeValue a Content-Type header value
     *
     * @return value from 'charset' directive. For example: utf-8
     */
    public String defineCharsetFromHeaderValue(String contentTypeValue) {
        if (contentTypeValue == null || contentTypeValue.isEmpty()) {
            return null;
        }
        if (contentTypeValue.contains("charset")) {
            String value = contentTypeValue.split("charset")[1].replaceAll("\\s", "").replaceFirst("=", "").trim();
            return value.split(";")[0];
        }
        return null;
    }
}
