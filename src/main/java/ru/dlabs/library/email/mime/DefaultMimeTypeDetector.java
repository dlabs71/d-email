package ru.dlabs.library.email.mime;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.Getter;

/**
 * This class implements {@link MimeTypeDetector} interface. Its purpose is to define MIMEType by a file.
 * It is based on two methods:
 * <ol>
 *     <li>It probes the content type of a file using {@link Files#probeContentType(Path)}</lo>
 *     <li>It tries to guess a content type using {@link URLConnection#guessContentTypeFromName(String)}</lo>
 * </ol>
 * <p>
 * These methods are used in the same order as above in the text. If its can't define MIMEType
 * then the {@link DefaultMimeTypeDetector#detect(File)} method will return
 * the default value (application/octet-stream).
 *
 *
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
    public static final DefaultMimeTypeDetector instance = new DefaultMimeTypeDetector();
    public static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    /**
     * Returns MIMEType by a file.
     * <p>
     * It uses the next methods: {@link Files#probeContentType(Path)} and
     * {@link URLConnection#guessContentTypeFromName(String)} or else returns the default value
     * from the constant {@link DefaultMimeTypeDetector#DEFAULT_CONTENT_TYPE}
     *
     * @param file a file from which it will take MIMEType
     *
     * @return MIMEType as string
     */
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
