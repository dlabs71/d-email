package ru.dlabs.library.email.message;

import java.util.Arrays;
import lombok.Getter;

/**
 * https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types
 *
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-09-01
 */
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

    AttachmentType(String mimeTypePattern) {
        this.mimeTypePattern = mimeTypePattern;
    }

    public static AttachmentType find(String mimeType) {
        return Arrays.stream(AttachmentType.values())
            .filter(item -> !item.equals(UNKNOWN) && mimeType.matches(item.getMimeTypePattern()))
            .findFirst()
            .orElse(UNKNOWN);
    }
}
