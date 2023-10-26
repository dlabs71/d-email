package ru.dlabs.library.email.tests.mime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Arrays;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import ru.dlabs.library.email.mime.DefaultFileParametersDetector;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-12</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
public class FileParametersDetectorTest {

    /**
     * The test for:
     * <ul>
     *     <li>{@link DefaultFileParametersDetector#detectMimeType(File)}</li>
     * </ul>
     */
    @Test
    public void detectTypeTest() {
        DefaultFileParametersDetector detector = new DefaultFileParametersDetector();

        String mimeType = detector.detectMimeType(this.getResource("attachments/file.doc"));
        assertEquals("application/msword", mimeType);

        mimeType = detector.detectMimeType(this.getResource("attachments/file.docx"));
        assertEquals("application/vnd.openxmlformats-officedocument.wordprocessingml.document", mimeType);

        mimeType = detector.detectMimeType(this.getResource("attachments/file.jpg"));
        assertEquals("image/jpeg", mimeType);

        mimeType = detector.detectMimeType(this.getResource("attachments/file.pdf"));
        assertEquals("application/pdf", mimeType);

        mimeType = detector.detectMimeType(this.getResource("attachments/file.png"));
        assertEquals("image/png", mimeType);

        mimeType = detector.detectMimeType(this.getResource("attachments/file.rar"));
        assertTrue(Arrays.asList("application/vnd.rar", "application/x-rar-compressed; version=5").contains(mimeType));

        mimeType = detector.detectMimeType(this.getResource("attachments/file.rtf"));
        assertEquals("application/rtf", mimeType);

        mimeType = detector.detectMimeType(this.getResource("attachments/file.svg"));
        assertEquals("image/svg+xml", mimeType);

        mimeType = detector.detectMimeType(this.getResource("attachments/file.tgz"));
        assertTrue(Arrays.asList("application/gzip", "application/x-compressed-tar").contains(mimeType));

        mimeType = detector.detectMimeType(this.getResource("attachments/file.txt"));
        assertEquals("text/plain", mimeType);

        mimeType = detector.detectMimeType(this.getResource("attachments/file.xls"));
        assertEquals("application/vnd.ms-excel", mimeType);

        mimeType = detector.detectMimeType(this.getResource("attachments/file.xlsx"));
        assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", mimeType);

        mimeType = detector.detectMimeType(this.getResource("attachments/file.zip"));
        assertEquals("application/zip", mimeType);
    }

    @SneakyThrows
    private File getResource(String path) {
        return new File(getClass().getClassLoader().getResource(path).toURI());
    }
}
