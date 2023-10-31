package ru.dlabs.library.email.dto.message.common;

import lombok.Builder;
import lombok.Getter;
import ru.dlabs.library.email.type.AttachmentType;
import ru.dlabs.library.email.util.AttachmentUtils;

/**
 * This class describes an email message attachment.
 *
 * <p>For simplifying attachment creation, use the {@link AttachmentUtils} utility class.
 *
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-09-01</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Getter
@Builder
public class EmailAttachment {

    private String name;
    private byte[] data;
    private AttachmentType type;
    private String contentType;
    private Integer size;
}
