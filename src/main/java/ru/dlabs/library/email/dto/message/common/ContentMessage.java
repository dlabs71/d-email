package ru.dlabs.library.email.dto.message.common;

import java.nio.charset.Charset;
import lombok.Getter;
import ru.dlabs.library.email.type.ContentMessageType;
import ru.dlabs.library.email.util.HttpUtils;

/**
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

    private String contentType;

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
    }

    public ContentMessage(String data, String contentType, Charset charset) {
        this.data = data;
        this.contentType = contentType;
        this.type = ContentMessageType.forContentType(contentType);
        if (charset == null) {
            this.charset = Charset.defaultCharset();
        } else {
            this.charset = charset;
        }

        if (charset != null) {
            this.contentType = HttpUtils.contentTypeWithCharset(contentType, charset);
        }
    }
}
