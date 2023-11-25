package ru.dlabs71.library.email.dto.message.common;

import lombok.Builder;
import lombok.Getter;
import ru.dlabs71.library.email.type.AttachmentType;
import ru.dlabs71.library.email.util.AttachmentUtils;

/**
 * This class describes an email message attachment.
 *
 * <p>For simplifying attachment creation, use the {@link AttachmentUtils} utility class.
 *
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-09-01</div>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Getter
@Builder
public class EmailAttachment {

    private final String name;
    private final byte[] data;
    private final AttachmentType type;
    private final String contentType;
    private final Integer size;

    @Override
    public String toString() {
        return "EmailAttachment{"
            + "name='" + name + '\''
            + ", contentType='" + contentType + '\''
            + ", size=" + size
            + '}';
    }
}
