package ru.dlabs71.library.email.util;

import static ru.dlabs71.library.email.util.ProtocolUtils.DEFAULT_BINARY_CONTENT_TYPE;

import java.io.File;
import java.nio.charset.Charset;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.dlabs71.library.email.mime.DefaultFileParametersDetector;
import ru.dlabs71.library.email.mime.FileParametersDetector;

/**
 * The utility class is for working with a file system and its files.
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-15</div>
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
     *     If the detector cannot define an encoding, then it returns the system default encoding.
     */
    public Charset detectFileEncoding(File file, FileParametersDetector detector) {
        log.debug("Tries to detect content encoding for file {}. Using detector is {}", file, detector);
        if (file == null || !file.exists()) {
            log.debug("An attempt to detect file content encoding failed because the file is null or doesn't exist");
            return null;
        }
        if (detector == null) {
            detector = DefaultFileParametersDetector.getInstance();
        }
        Charset encoding = detector.detectEncoding(file);
        if (encoding == null) {
            encoding = Charset.defaultCharset();
        }
        log.debug("File encoding is {}", encoding);
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
     *     then it returns the default value ({@link ProtocolUtils#DEFAULT_BINARY_CONTENT_TYPE})
     */
    public String detectFileMimeType(File file, FileParametersDetector detector) {
        log.debug("Tries to detect MIME type for file {}. Using detector is {}", file, detector);
        if (file == null || !file.exists()) {
            log.debug("An attempt to detect file MINE type failed because the file is null or doesn't exist");
            return null;
        }
        if (detector == null) {
            detector = DefaultFileParametersDetector.getInstance();
        }
        String mimeType = detector.detectMimeType(file);

        if (mimeType == null) {
            mimeType = DEFAULT_BINARY_CONTENT_TYPE;
        }
        log.debug("File MIME type is {}", mimeType);
        return mimeType;
    }
}
