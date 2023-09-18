package ru.dlabs.library.email.dto.message.common;

import lombok.Builder;
import lombok.Getter;
import ru.dlabs.library.email.type.AttachmentType;

/**
 * This class described an email attachment
 *
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-09-01
 */
@Getter
@Builder
public class EmailAttachment {

    private String name;
    private byte[] data;
    private AttachmentType type;
    private String contentType;
    private Long size;
}
