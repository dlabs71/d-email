package ru.dlabs.library.email.type;

import java.util.Arrays;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * It's the enum with attachment types, which are based on MIME types.
 * <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types">MIME types</a>
 *
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-09-01</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Slf4j
@Getter
public enum AttachmentType {
    IMAGE("image/.*"),
    VIDEO("video/.*"),
    AUDIO("audio/.*"),
    APPLICATION("application/.*"),
    FONT("font/.*"),
    MODEL("model/.*"),
    TEXT("text/.*"),
    UNKNOWN(null);

    private final String mimeTypePattern;

    /**
     * The constructor of this enum.
     *
     * @param mimeTypePattern a regex pattern of MIME types
     */
    AttachmentType(String mimeTypePattern) {
        this.mimeTypePattern = mimeTypePattern;
    }

    /**
     * Finds the attachment type by the string of a MIME type using the Regex mechanism.
     *
     * @param mimeType the string of a MIME type
     *
     * @return {@link AttachmentType}
     */
    public static AttachmentType find(String mimeType) {
        log.debug("Tries to look up the mime type in the AttachmentType. Mime type is {}", mimeType);
        return Arrays.stream(AttachmentType.values())
            .filter(item -> !item.equals(UNKNOWN) && mimeType.matches(item.getMimeTypePattern()))
            .findFirst()
            .orElse(UNKNOWN);
    }
}
