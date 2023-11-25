package ru.dlabs71.library.email.mime;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.Getter;

/**
 * This class implements {@link FileParametersDetector} interface. Its purpose is to define MIMEType by a file.
 * It is based on two methods:
 *
 * <ol>
 *     <li>It probes the content type of a file using {@link Files#probeContentType(Path)}</li>
 *     <li>It tries to guess a content type using {@link URLConnection#guessContentTypeFromName(String)}</li>
 * </ol>
 *
 * <p>These methods are used in the same order as above in the text. If its can't define MIMEType
 * then the {@link DefaultFileParametersDetector#detectMimeType(File)} method will return
 * the default value (application/octet-stream).
 *
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-09-26</div>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
public class DefaultFileParametersDetector implements FileParametersDetector {

    @Getter
    private static final DefaultFileParametersDetector instance = new DefaultFileParametersDetector();
    public static final String DEFAULT_CONTENT_TYPE = "application/octet-stream";

    /**
     * Returns MIMEType by a file.
     *
     * <p>It uses the next methods: {@link Files#probeContentType(Path)} and
     * {@link URLConnection#guessContentTypeFromName(String)} or else returns the default value
     * from the constant {@link DefaultFileParametersDetector#DEFAULT_CONTENT_TYPE}
     *
     * @param file a file from which it will take MIMEType
     *
     * @return MIMEType as string
     */
    @Override
    public String detectMimeType(File file) {
        String contentType;
        try {
            contentType = Files.probeContentType(file.toPath());
        } catch (IOException e) {
            contentType = URLConnection.guessContentTypeFromName(file.getName());
        }
        return contentType != null ? contentType : DEFAULT_CONTENT_TYPE;
    }

    /**
     * Returns always system default charset.
     * You can override this method or create your own implementation of the {@link FileParametersDetector} interface.
     *
     * @param file a file for getting content charset
     *
     * @return system default charset
     */
    @Override
    public Charset detectEncoding(File file) {
        return Charset.defaultCharset();
    }
}
