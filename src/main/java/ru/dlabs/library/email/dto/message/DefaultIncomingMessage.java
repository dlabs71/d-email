package ru.dlabs.library.email.dto.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.dlabs.library.email.dto.message.api.IncomingMessage;
import ru.dlabs.library.email.dto.message.common.BaseMessage;

/**
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-09-06
 */
@Getter
@Setter
@AllArgsConstructor
public class DefaultIncomingMessage extends BaseMessage implements IncomingMessage {

    private String htmlContent;
    private boolean seen = false;

    public DefaultIncomingMessage(BaseMessage baseMessage) {
        super(
            baseMessage.getId(),
            baseMessage.getSubject(),
            baseMessage.getContent(),
            baseMessage.getRecipientEmail(),
            baseMessage.getSender(),
            baseMessage.getAttachments(),
            baseMessage.getEncoding(),
            baseMessage.getContentType(),
            baseMessage.getSize(),
            baseMessage.getSentDate(),
            baseMessage.getReceivedDate()
        );
    }

    public DefaultIncomingMessage(BaseMessage baseMessage, String htmlContent, boolean seen) {
        this(baseMessage);
        this.htmlContent = htmlContent;
        this.seen = seen;
    }

    public DefaultIncomingMessage(BaseMessage baseMessage, String htmlContent) {
        this(baseMessage);
        this.htmlContent = htmlContent;
    }
}
