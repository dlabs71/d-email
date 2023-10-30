package ru.dlabs.library.email.util;

import static ru.dlabs.library.email.util.HttpUtils.DEFAULT_BINARY_CONTENT_TYPE;

import java.io.File;
import java.nio.charset.Charset;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.dlabs.library.email.mime.DefaultFileParametersDetector;
import ru.dlabs.library.email.mime.FileParametersDetector;

/**
 * The utility class is for working with a file system and its files.
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

    /**
     * Tries to predict encoding of a file.
     *
     * @see FileSystemUtils#detectFileEncoding(File, FileParametersDetector)
     */
    public Charset detectFileEncoding(File file) {
        return detectFileEncoding(file, null);
    }

    /**
     * Tries to predict encoding of a file.
     *
     * @param file     a file for prediction
     * @param detector an implementation of {@link FileParametersDetector} class.
     *                 If null, it'll use {@link DefaultFileParametersDetector}
     *
     * @return {@link Charset} class is the corresponding encoding of the file.
     *         If the detector cannot define an encoding, then it returns the system default encoding.
     */
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

    /**
     * Tries to predict MIME type of file.
     *
     * @see FileSystemUtils#detectFileMimeType(File, FileParametersDetector)
     */
    public String detectFileMimeType(File file) {
        return detectFileMimeType(file, null);
    }

    /**
     * Tries to predict MIME type of file.
     *
     * @param file     a file for prediction
     * @param detector an implementation of {@link FileParametersDetector} class.
     *                 If null, it'll use {@link DefaultFileParametersDetector}
     *
     * @return MIME type of file. If the detector cannot define the MIME type,
     *         then it returns the default value ({@link HttpUtils#DEFAULT_BINARY_CONTENT_TYPE})
     */
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
