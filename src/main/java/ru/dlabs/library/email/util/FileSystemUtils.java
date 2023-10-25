package ru.dlabs.library.email.util;

import static ru.dlabs.library.email.util.HttpUtils.DEFAULT_BINARY_CONTENT_TYPE;

import java.io.File;
import java.nio.charset.Charset;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.dlabs.library.email.mime.DefaultFileParametersDetector;
import ru.dlabs.library.email.mime.FileParametersDetector;

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

    public Charset detectFileEncoding(File file) {
        return detectFileEncoding(file, null);
    }

    public Charset detectFileEncoding(File file, FileParametersDetector detector) {
        if (file == null || !file.exists()) {
            return null;
        }
        if (detector == null) {
            detector = DefaultFileParametersDetector.getInstance();
        }
        Charset encoding = detector.detectEncoding(file);
        if (encoding == null) {
            encoding = Charset.defaultCharset();
        }
        return encoding;
    }

    public String detectFileMimeType(File file) {
        return detectFileMimeType(file, null);
    }

    public String detectFileMimeType(File file, FileParametersDetector detector) {
        if (file == null || !file.exists()) {
            return null;
        }
        if (detector == null) {
            detector = DefaultFileParametersDetector.getInstance();
        }
        String mimeType = detector.detectMimeType(file);

        if (mimeType == null) {
            mimeType = DEFAULT_BINARY_CONTENT_TYPE;
        }
        return mimeType;
    }
}
