package ru.dlabs.library.email.message;

import static ru.dlabs.library.email.utils.EmailMessageUtils.DEFAULT_CONTENT_TYPE;
import static ru.dlabs.library.email.utils.EmailMessageUtils.DEFAULT_ENCODING;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-09-01
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "baseMessageBuilder")
public class BaseMessage implements Message {

    private Integer id;

    private String subject;
    private String content;

    private Set<EmailParticipant> recipientEmail = new HashSet<>();
    private EmailParticipant sender = null;

    private List<EmailAttachment> attachments = new ArrayList<>();

    private String encoding = DEFAULT_ENCODING;
    private String contentType = DEFAULT_CONTENT_TYPE;
    private Integer size;

    private LocalDateTime sentDate;
    private LocalDateTime receivedDate;
}
