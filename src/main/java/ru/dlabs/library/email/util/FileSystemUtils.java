package ru.dlabs.library.email.util;

import static ru.dlabs.library.email.util.HttpUtils.DEFAULT_BINARY_CONTENT_TYPE;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.dlabs.library.email.mime.DefaultMimeTypeDetector;
import ru.dlabs.library.email.mime.MimeTypeDetector;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-15</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Slf4j
@UtilityClass
public class FileSystemUtils {

    public String defineFileEncoding(File file) {
        try {
            FileInputStream is = new FileInputStream(file);
            InputStreamReader reader = new InputStreamReader(is);
            return reader.getEncoding();
        } catch (IOException ex) {
            log.error(ex.getLocalizedMessage(), ex);
        }
        return null;
    }

    public String defineFileMimeType(File file) {
        return defineFileMimeType(file, null);
    }

    public String defineFileMimeType(File file, MimeTypeDetector detector) {
        if (detector == null) {
            detector = DefaultMimeTypeDetector.getInstance();
        }
        String mimeType = detector.detect(file);

        if (mimeType == null) {
            mimeType = DEFAULT_BINARY_CONTENT_TYPE;
        }
        return mimeType;
    }
}
