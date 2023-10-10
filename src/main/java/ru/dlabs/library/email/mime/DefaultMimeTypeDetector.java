package ru.dlabs.library.email.mime;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import lombok.Getter;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-09-26</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
public class DefaultMimeTypeDetector implements MimeTypeDetector {

    @Getter
    public final static DefaultMimeTypeDetector instance = new DefaultMimeTypeDetector();
    public final static String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    @Override
    public String detect(File file) {
        String contentType;
        try {
            contentType = Files.probeContentType(file.toPath());
        } catch (IOException e) {
            contentType = URLConnection.guessContentTypeFromName(file.getName());
        }
        return contentType != null ? contentType : DEFAULT_CONTENT_TYPE;
    }
}
