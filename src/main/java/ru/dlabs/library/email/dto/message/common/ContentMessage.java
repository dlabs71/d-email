package ru.dlabs.library.email.dto.message.common;

import lombok.Getter;
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
    private final String encoding;

    private String contentType;

    public ContentMessage(String data, String contentType) {
        this.data = data;
        this.contentType = contentType;
        this.encoding = HttpUtils.defineEncodingFromHeaderValue(contentType);
    }

    public ContentMessage(String data, String contentType, String encoding) {
        this.data = data;
        this.contentType = contentType;
        this.encoding = encoding;
        if (encoding != null) {
            this.contentType = HttpUtils.contentTypeWithEncoding(contentType, encoding);
        }
    }
}
