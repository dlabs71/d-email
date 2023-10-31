package ru.dlabs.library.email.dto.message.incoming;

import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import ru.dlabs.library.email.dto.message.common.BaseMessage;
import ru.dlabs.library.email.dto.message.common.ContentMessage;
import ru.dlabs.library.email.type.ContentMessageType;

/**
 * This class describes a default incoming email message.
 * It extends {@link BaseMessage} and also implements {@link IncomingMessage} interface.
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-09-06</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
public class DefaultIncomingMessage extends BaseMessage implements IncomingMessage {

    /**
     * The default constructor.
     *
     * @param baseMessage instance of the {@link BaseMessage} class for a base this class
     */
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

    /**
     * Returns all the html contents.
     */
    @Override
    public List<ContentMessage> getHtmlContents() {
        return this.getContents().stream()
            .filter(item -> ContentMessageType.HTML.equals(item.getType()))
            .collect(Collectors.toList());
    }

    /**
     * Returns all the text contents.
     */
    @Override
    public List<ContentMessage> getTextContents() {
        return this.getContents().stream()
            .filter(item -> ContentMessageType.TEXT.equals(item.getType()))
            .collect(Collectors.toList());
    }

    /**
     * Returns all the html contents as one string.
     */
    @Override
    public String getHtmlContentsAsString() {
        return this.getHtmlContentsAsString("\n");
    }

    /**
     * Returns all the html contents as one string, separated by a delimiter, which is set up in the parameter.
     */
    @Override
    public String getHtmlContentsAsString(String delimiter) {
        return this.getHtmlContents().stream()
            .map(ContentMessage::getData)
            .collect(Collectors.joining(delimiter));
    }

    /**
     * Returns all the text contents as one string, separated by default delimiter.
     */
    @Override
    public String getTextContentsAsString() {
        return this.getTextContentsAsString("\n");
    }

    /**
     * Returns all the text contents as one string, separated by a delimiter, which is set up in the parameter.
     */
    @Override
    public String getTextContentsAsString(String delimiter) {
        return this.getTextContents().stream()
            .map(ContentMessage::getData)
            .collect(Collectors.joining(delimiter));
    }
}
