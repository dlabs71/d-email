package ru.dlabs.library.email.mime;

import java.io.File;
import java.nio.charset.Charset;

/**
 * Interface for an implementing file parameter detector, such as: MIME type, content charset.
 * You can use this interface to create your own detector, which will be based on, for example,
 * Apache Tika or another project.
 *
 * <p>By default, we provide you {@link DefaultFileParametersDetector}.
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-09-26</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
public interface FileParametersDetector {

    /**
     * Returns a MIME type of file as a string.
     *
     * @param file a file for getting a MIME type
     *
     * @return a MIME type of file as a string
     */
    String detectMimeType(File file);

    /**
     * Returns charset of content the file.
     *
     * @param file a file for getting content charset
     *
     * @return charset of content in the file
     */
    Charset detectEncoding(File file);
}
