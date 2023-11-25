package ru.dlabs71.library.email.type;

import static ru.dlabs71.library.email.util.ProtocolUtils.HTML_CONTENT_TYPE;
import static ru.dlabs71.library.email.util.ProtocolUtils.TEXT_CONTENT_TYPE;

import java.util.Arrays;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * The enum contains all types of message content. It's active use in outgoing messages.
 *
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-18</div>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Slf4j
@Getter
public enum ContentMessageType {
    TEXT(TEXT_CONTENT_TYPE),
    HTML(HTML_CONTENT_TYPE);

    private final String mimeType;

    /**
     * The constructor of this enum.
     *
     * @param mimeType corresponding MIME type of message content
     */
    ContentMessageType(String mimeType) {
        this.mimeType = mimeType;
    }

    /**
     * Finds the corresponding enum value for the string (value of a Content-Type header) in the argument.
     *
     * @param contentType value of a Content-Type header
     *
     * @return an enum value or null
     */
    public static ContentMessageType forContentType(String contentType) {
        log.debug(
            "Tries to look up the content type in the ContentMessageType. String content type is {}",
            contentType
        );
        if (contentType == null) {
            return null;
        }
        return Arrays.stream(values())
            .filter(item -> contentType.contains(item.getMimeType()))
            .findFirst()
            .orElse(null);
    }
}
