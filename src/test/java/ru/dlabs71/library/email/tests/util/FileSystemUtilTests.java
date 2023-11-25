package ru.dlabs71.library.email.tests.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.dlabs71.library.email.support.TestUtils.getResource;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import ru.dlabs71.library.email.mime.FileParametersDetector;
import ru.dlabs71.library.email.support.ApacheTikaDetector;
import ru.dlabs71.library.email.util.FileSystemUtils;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-24</div>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Order(110)
public class FileSystemUtilTests {

    private final FileParametersDetector tikaDetector = new ApacheTikaDetector();

    /**
     * The test for:
     * <ul>
     *     <li>{@link FileSystemUtils#detectFileEncoding(File)}</li>
     *      <li>{@link FileSystemUtils#detectFileEncoding(File, FileParametersDetector)}</li>
     * </ul>
     */
    @Test
    public void detectFileEncodingTest() {
        File file_utf8 = getResource("attachments/encoding/file_utf8.data");
        File file_iso88591 = getResource("attachments/encoding/file_iso88591.data");
        File file_koi8r = getResource("attachments/encoding/file_koi8r.data");
        File file_utf16be = getResource("attachments/encoding/file_utf16be.data");

        assertNull(FileSystemUtils.detectFileEncoding(null));
        assertNull(FileSystemUtils.detectFileEncoding(new File("not existed file")));
        assertEquals(StandardCharsets.UTF_8, FileSystemUtils.detectFileEncoding(file_utf8, tikaDetector));
        assertEquals(StandardCharsets.ISO_8859_1, FileSystemUtils.detectFileEncoding(file_iso88591, tikaDetector));
        assertEquals(Charset.forName("KOI8-R"), FileSystemUtils.detectFileEncoding(file_koi8r, tikaDetector));
        assertEquals(StandardCharsets.UTF_16BE, FileSystemUtils.detectFileEncoding(file_utf16be, tikaDetector));


        assertEquals(Charset.defaultCharset(), FileSystemUtils.detectFileEncoding(file_utf8));
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link FileSystemUtils#detectFileMimeType(File)}</li>
     * </ul>
     */
    @Test
    public void detectFileMimeTypeTest() {
        String contentType = FileSystemUtils.detectFileMimeType(getResource("attachments/file.djvu"));
        assertTrue(Arrays.asList("image/vnd.djvu", "image/vnd.djvu+multipage").contains(contentType));

        contentType = FileSystemUtils.detectFileMimeType(getResource("attachments/file.doc"));
        assertEquals("application/msword", contentType);

        contentType = FileSystemUtils.detectFileMimeType(getResource("attachments/file.docx"));
        assertEquals("application/vnd.openxmlformats-officedocument.wordprocessingml.document", contentType);

        contentType = FileSystemUtils.detectFileMimeType(getResource("attachments/file.jpg"));
        assertEquals("image/jpeg", contentType);

        contentType = FileSystemUtils.detectFileMimeType(getResource("attachments/file.pdf"));
        assertEquals("application/pdf", contentType);

        contentType = FileSystemUtils.detectFileMimeType(getResource("attachments/file.png"));
        assertEquals("image/png", contentType);

        contentType = FileSystemUtils.detectFileMimeType(getResource("attachments/file.rar"));
        assertEquals("application/vnd.rar", contentType);

        contentType = FileSystemUtils.detectFileMimeType(getResource("attachments/file.rtf"));
        assertEquals("application/rtf", contentType);

        contentType = FileSystemUtils.detectFileMimeType(getResource("attachments/file.svg"));
        assertEquals("image/svg+xml", contentType);

        contentType = FileSystemUtils.detectFileMimeType(getResource("attachments/file.tgz"));
        assertTrue(Arrays.asList("application/gzip", "application/x-compressed-tar").contains(contentType));

        contentType = FileSystemUtils.detectFileMimeType(getResource("attachments/file.txt"));
        assertEquals("text/plain", contentType);

        contentType = FileSystemUtils.detectFileMimeType(getResource("attachments/file.xls"));
        assertEquals("application/vnd.ms-excel", contentType);

        contentType = FileSystemUtils.detectFileMimeType(getResource("attachments/file.xlsx"));
        assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", contentType);

        contentType = FileSystemUtils.detectFileMimeType(getResource("attachments/file.zip"));
        assertEquals("application/zip", contentType);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link FileSystemUtils#detectFileMimeType(File, FileParametersDetector)}</li>
     * </ul>
     */
    @Test
    public void detectFileMimeTypeTikaTest() {
        String contentType = FileSystemUtils.detectFileMimeType(
            getResource("attachments/file.djvu"),
            tikaDetector
        );
        assertEquals("image/vnd.djvu", contentType);

        contentType = FileSystemUtils.detectFileMimeType(
            getResource("attachments/file.doc"),
            tikaDetector
        );
        assertEquals("application/msword", contentType);

        contentType = FileSystemUtils.detectFileMimeType(
            getResource("attachments/file.docx"),
            tikaDetector
        );
        assertEquals("application/vnd.openxmlformats-officedocument.wordprocessingml.document", contentType);

        contentType = FileSystemUtils.detectFileMimeType(
            getResource("attachments/file.jpg"),
            tikaDetector
        );
        assertEquals("image/jpeg", contentType);

        contentType = FileSystemUtils.detectFileMimeType(
            getResource("attachments/file.pdf"),
            tikaDetector
        );
        assertEquals("application/pdf", contentType);

        contentType = FileSystemUtils.detectFileMimeType(
            getResource("attachments/file.png"),
            tikaDetector
        );
        assertEquals("image/png", contentType);

        contentType = FileSystemUtils.detectFileMimeType(
            getResource("attachments/file.rar"),
            tikaDetector
        );
        assertEquals("application/x-rar-compressed; version=5", contentType);

        contentType = FileSystemUtils.detectFileMimeType(
            getResource("attachments/file.rtf"),
            tikaDetector
        );
        assertEquals("application/rtf", contentType);

        contentType = FileSystemUtils.detectFileMimeType(
            getResource("attachments/file.svg"),
            tikaDetector
        );
        assertEquals("image/svg+xml", contentType);

        contentType = FileSystemUtils.detectFileMimeType(
            getResource("attachments/file.tgz"),
            tikaDetector
        );
        assertEquals("application/gzip", contentType);

        contentType = FileSystemUtils.detectFileMimeType(
            getResource("attachments/file.txt"),
            tikaDetector
        );
        assertEquals("text/plain", contentType);

        contentType = FileSystemUtils.detectFileMimeType(
            getResource("attachments/file.xls"),
            tikaDetector
        );
        assertEquals("application/vnd.ms-excel", contentType);

        contentType = FileSystemUtils.detectFileMimeType(
            getResource("attachments/file.xlsx"),
            tikaDetector
        );
        assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", contentType);

        contentType = FileSystemUtils.detectFileMimeType(
            getResource("attachments/file.zip"),
            tikaDetector
        );
        assertEquals("application/zip", contentType);
    }
}
