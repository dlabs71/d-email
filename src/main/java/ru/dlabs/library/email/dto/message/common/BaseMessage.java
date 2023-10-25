package ru.dlabs.library.email.dto.message.common;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.dlabs.library.email.type.TransferEncoder;

/**
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-09-01
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BaseMessage implements Message {

    private Integer id;

    private String subject;
    private List<ContentMessage> contents = new ArrayList<>();

    private Set<EmailParticipant> recipients = new HashSet<>();
    private EmailParticipant sender = null;

    private List<EmailAttachment> attachments = new ArrayList<>();

    private TransferEncoder transferEncoder = TransferEncoder.byDefault();
    private Integer size;
    private boolean seen = false;

    private LocalDateTime sentDate;
    private LocalDateTime receivedDate;

    public void addContent(ContentMessage content) {
        if (this.contents == null) {
            this.contents = new ArrayList<>();
        }
        this.contents.add(content);
    }

    public void addAllContent(Collection<ContentMessage> contents) {
        if (this.contents == null) {
            this.contents = new ArrayList<>();
        }
        this.contents.addAll(contents);
    }

    public String getAllContentsAsString() {
        return this.getAllContentsAsString("\n");
    }

    public String getAllContentsAsString(String delimiter) {
        return this.getContents().stream().map(ContentMessage::getData).collect(Collectors.joining(delimiter));
    }
}
