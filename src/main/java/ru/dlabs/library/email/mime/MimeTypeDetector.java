package ru.dlabs.library.email.mime;

import java.io.File;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-09-26</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
public interface MimeTypeDetector {

    String detect(File file);
}
