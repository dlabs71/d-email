package ru.dlabs71.library.email.dto.message.incoming;

import java.util.List;
import ru.dlabs71.library.email.dto.message.common.ContentMessage;
import ru.dlabs71.library.email.dto.message.common.Message;

/**
 * This interface defines any incoming email message. Extends by the {@link Message} interface.
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-09-07</div>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
public interface IncomingMessage extends Message {

    /**
     * Returns all the html contents.
     */
    List<ContentMessage> getHtmlContents();

    /**
     * Returns all the text contents.
     */
    List<ContentMessage> getTextContents();

    /**
     * Returns all the text contents as one string, separated by default delimiter.
     */
    String getTextContentsAsString();

    /**
     * Returns all the text contents as one string, separated by a delimiter, which is set up in the parameter.
     */
    String getTextContentsAsString(String delimiter);

    /**
     * Returns all the html contents as one string.
     */
    String getHtmlContentsAsString();

    /**
     * Returns all the html contents as one string, separated by a delimiter, which is set up in the parameter.
     */
    String getHtmlContentsAsString(String delimiter);
}
