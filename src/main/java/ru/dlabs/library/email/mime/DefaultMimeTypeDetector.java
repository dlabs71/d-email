package ru.dlabs.library.email.mime;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;

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

    public final static DefaultMimeTypeDetector instance = new DefaultMimeTypeDetector();
    public final static String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    public static DefaultMimeTypeDetector getInstance() {
        return instance;
    }

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
