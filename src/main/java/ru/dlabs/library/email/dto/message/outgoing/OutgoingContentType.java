package ru.dlabs.library.email.dto.message.outgoing;

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
public enum OutgoingContentType {
    TEXT(HttpUtils.TEXT_CONTENT_TYPE), HTML(HttpUtils.HTML_CONTENT_TYPE);

    private final String contentType;

    OutgoingContentType(String contentType) { this.contentType = contentType; }
}
