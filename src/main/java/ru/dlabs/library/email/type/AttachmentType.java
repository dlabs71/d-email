package ru.dlabs.library.email.type;

import java.util.Arrays;
import lombok.Getter;

/**
 * It's the enum with attachment types, which are based on MIME types
 * <p>
 * https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types
 *
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-09-01
 */
@Getter
public enum AttachmentType {
    IMAGE("image/.*"), VIDEO("video/.*"), AUDIO("audio/.*"), APPLICATION("application/.*"), FONT("font/.*"), MODEL(
        "model/.*"), TEXT("text/.*"), UNKNOWN(null);

    private final String mimeTypePattern;

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
        return Arrays.stream(AttachmentType.values())
            .filter(item -> !item.equals(UNKNOWN) && mimeType.matches(item.getMimeTypePattern()))
            .findFirst()
            .orElse(UNKNOWN);
    }
}
