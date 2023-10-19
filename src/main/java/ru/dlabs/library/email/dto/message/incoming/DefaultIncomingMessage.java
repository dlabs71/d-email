package ru.dlabs.library.email.dto.message.incoming;

import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import ru.dlabs.library.email.dto.message.common.BaseMessage;
import ru.dlabs.library.email.dto.message.common.ContentMessage;
import ru.dlabs.library.email.dto.message.common.ContentMessageType;

/**
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-09-06
 */
public class DefaultIncomingMessage extends BaseMessage implements IncomingMessage {

    public DefaultIncomingMessage(@NonNull BaseMessage baseMessage) {
        super(
            baseMessage.getId(),
            baseMessage.getSubject(),
            baseMessage.getContents(),
            baseMessage.getRecipients(),
            baseMessage.getSender(),
            baseMessage.getAttachments(),
            baseMessage.getTransferEncoder(),
            baseMessage.getSize(),
            baseMessage.isSeen(),
            baseMessage.getSentDate(),
            baseMessage.getReceivedDate()
        );
    }

    @Override
    public List<ContentMessage> getHtmlContents() {
        return this.getContents().stream()
            .filter(item -> ContentMessageType.HTML.equals(item.getType()))
            .collect(Collectors.toList());
    }

    @Override
    public List<ContentMessage> getTextContents() {
        return this.getContents().stream()
            .filter(item -> ContentMessageType.TEXT.equals(item.getType()))
            .collect(Collectors.toList());
    }

    public String getHtmlContentsAsString() {
        return this.getHtmlContentsAsString("\n");
    }

    public String getHtmlContentsAsString(String delimiter) {
        return this.getHtmlContents().stream()
            .map(ContentMessage::getData)
            .collect(Collectors.joining(delimiter));
    }

    public String getTextContentsAsString() {
        return this.getTextContentsAsString("\n");
    }

    public String getTextContentsAsString(String delimiter) {
        return this.getTextContents().stream()
            .map(ContentMessage::getData)
            .collect(Collectors.joining(delimiter));
    }
}
