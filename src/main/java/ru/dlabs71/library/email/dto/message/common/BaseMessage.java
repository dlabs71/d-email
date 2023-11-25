package ru.dlabs71.library.email.dto.message.common;

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
import ru.dlabs71.library.email.type.TransferEncoder;

/**
 * This class describes a base email message. It's implementation of {@link Message} interface.
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-09-02</div>
 *
 * @author Ivanov Danila
 * @since 1.0.0
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
    private Long size = 0L;
    private boolean seen = false;

    private LocalDateTime sentDate;
    private LocalDateTime receivedDate;

    /**
     * Add new content to the list contents.
     */
    public void addContent(ContentMessage content) {
        if (this.contents == null) {
            this.contents = new ArrayList<>();
        }
        this.contents.add(content);
    }

    /**
     * Add several contents to the list contents.
     */
    public void addAllContent(Collection<ContentMessage> contents) {
        if (this.contents == null) {
            this.contents = new ArrayList<>();
        }
        this.contents.addAll(contents);
    }

    /**
     * Returns all the contents as one string, separated by '\n'.
     */
    public String getAllContentsAsString() {
        return this.getAllContentsAsString("\n");
    }

    /**
     * Returns all the contents as one string, separated by a delimiter, which is set up in the parameter.
     */
    public String getAllContentsAsString(String delimiter) {
        return this.getContents().stream().map(ContentMessage::getData).collect(Collectors.joining(delimiter));
    }

    @Override
    public String toString() {
        return "BaseMessage{"
            + "subject='" + subject + '\''
            + ", content=" + contents
            + ", recipients=" + recipients
            + ", sender=" + sender
            + ", attachments=" + attachments
            + ", transferEncoder=" + transferEncoder
            + '}';
    }
}
