package ru.dlabs.library.email.mime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Arrays;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-12</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
public class MimeTypeDetectorTest {

    /**
     * The test for:
     * <ul>
     *     <li>{@link DefaultMimeTypeDetector#detect(File)}</li>
     * </ul>
     */
    @Test
    public void detectTypeTest() {
        DefaultMimeTypeDetector detector = new DefaultMimeTypeDetector();

        String mimeType = detector.detect(this.getResource("attachments/file.doc"));
        assertEquals(mimeType, "application/msword");

        mimeType = detector.detect(this.getResource("attachments/file.docx"));
        assertEquals(mimeType, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");

        mimeType = detector.detect(this.getResource("attachments/file.jpg"));
        assertEquals(mimeType, "image/jpeg");

        mimeType = detector.detect(this.getResource("attachments/file.pdf"));
        assertEquals(mimeType, "application/pdf");

        mimeType = detector.detect(this.getResource("attachments/file.png"));
        assertEquals(mimeType, "image/png");

        mimeType = detector.detect(this.getResource("attachments/file.rar"));
        assertTrue(Arrays.asList("application/vnd.rar", "application/x-rar-compressed; version=5").contains(mimeType));

        mimeType = detector.detect(this.getResource("attachments/file.rtf"));
        assertEquals(mimeType, "application/rtf");

        mimeType = detector.detect(this.getResource("attachments/file.svg"));
        assertEquals(mimeType, "image/svg+xml");

        mimeType = detector.detect(this.getResource("attachments/file.tgz"));
        assertTrue(Arrays.asList("application/gzip", "application/x-compressed-tar").contains(mimeType));

        mimeType = detector.detect(this.getResource("attachments/file.txt"));
        assertEquals(mimeType, "text/plain");

        mimeType = detector.detect(this.getResource("attachments/file.xls"));
        assertEquals(mimeType, "application/vnd.ms-excel");

        mimeType = detector.detect(this.getResource("attachments/file.xlsx"));
        assertEquals(mimeType, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        mimeType = detector.detect(this.getResource("attachments/file.zip"));
        assertEquals(mimeType, "application/zip");
    }

    @SneakyThrows
    private File getResource(String path) {
        return new File(getClass().getClassLoader().getResource(path).toURI());
    }
}
