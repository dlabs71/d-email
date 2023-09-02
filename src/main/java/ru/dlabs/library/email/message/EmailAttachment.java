package ru.dlabs.library.email.message;

import lombok.Builder;
import lombok.Getter;

/**
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
    private Integer size;
}
