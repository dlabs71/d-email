package ru.dlabs.library.email.dto.message.common;

import java.nio.charset.Charset;
import lombok.Getter;
import ru.dlabs.library.email.type.ContentMessageType;
import ru.dlabs.library.email.util.AttachmentUtils;
import ru.dlabs.library.email.util.HttpUtils;

/**
 * This class describes an email message content (text of html).
 *
 * <p>For simplifying attachment creation, use the {@link AttachmentUtils} utility class.
 *
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-16</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Getter
public class ContentMessage {

    private final String data;
    private final Charset charset;
    private final ContentMessageType type;
    private final Integer size;
    private final String contentType;

    /**
     * The constructor of this class.
     *
     * <p>It tries to get charset from corresponding directive in a Content-Type header.
     *
     * @param data        a content message
     * @param contentType a value of a Content-Type header
     */
    public ContentMessage(String data, String contentType) {
        this.data = data;
        this.contentType = contentType;
        this.type = ContentMessageType.forContentType(contentType);
        String charset = HttpUtils.defineCharsetFromHeaderValue(contentType);
        if (charset != null) {
            this.charset = Charset.forName(charset);
        } else {
            this.charset = Charset.defaultCharset();
        }
        this.size = data.getBytes(this.charset).length;
    }

    /**
     * The constructor of this class.
     *
     * @param data        a content message
     * @param contentType a value of a Content-Type header
     * @param charset     a charset of the data parameter
     */
    public ContentMessage(String data, String contentType, Charset charset) {
        this.data = data;

        if (charset != null) {
            this.contentType = HttpUtils.contentTypeWithCharset(contentType, charset);
        } else {
            this.contentType = contentType;
        }

        this.type = ContentMessageType.forContentType(contentType);

        if (charset == null) {
            this.charset = Charset.defaultCharset();
        } else {
            this.charset = charset;
        }

        this.size = data.getBytes(this.charset).length;
    }
}
