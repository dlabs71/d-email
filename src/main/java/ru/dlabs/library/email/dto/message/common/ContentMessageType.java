package ru.dlabs.library.email.dto.message.common;

import static ru.dlabs.library.email.util.HttpUtils.HTML_CONTENT_TYPE;
import static ru.dlabs.library.email.util.HttpUtils.TEXT_CONTENT_TYPE;

import java.util.Arrays;
import lombok.Getter;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-18</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Getter
public enum ContentMessageType {
    TEXT(TEXT_CONTENT_TYPE), HTML(HTML_CONTENT_TYPE);

    private final String mimeType;

    ContentMessageType(String mimeType) {
        this.mimeType = mimeType;
    }

    public static ContentMessageType forContentType(String contentType) {
        if (contentType == null) {
            return null;
        }
        return Arrays.stream(values())
            .filter(item -> contentType.contains(item.getMimeType()))
            .findFirst()
            .orElse(null);
    }
}
