package ru.dlabs.library.email.dto.message.incoming;

import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.dlabs.library.email.dto.message.common.BaseMessage;
import ru.dlabs.library.email.dto.message.common.ContentMessage;

/**
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-09-06
 */
@Getter
@Setter
@AllArgsConstructor
public class DefaultIncomingMessage extends BaseMessage implements IncomingMessage {

    private List<ContentMessage> htmlContents;

    public DefaultIncomingMessage(BaseMessage baseMessage) {
        super(
            baseMessage.getId(),
            baseMessage.getSubject(),
            baseMessage.getContents(),
            baseMessage.getRecipients(),
            baseMessage.getSender(),
            baseMessage.getAttachments(),
            baseMessage.getTransferEncoder(),
            baseMessage.getSize(),
            baseMessage.getSentDate(),
            baseMessage.getReceivedDate()
        );
    }

    public DefaultIncomingMessage(BaseMessage baseMessage, List<ContentMessage> htmlContents) {
        this(baseMessage);
        this.htmlContents = htmlContents;
    }

    public String getHtmlContentsAsString() {
        return this.getHtmlContentsAsString("\n");
    }

    public String getHtmlContentsAsString(String delimiter) {
        return this.htmlContents.stream()
            .map(ContentMessage::getData)
            .collect(Collectors.joining(delimiter));
    }
}
