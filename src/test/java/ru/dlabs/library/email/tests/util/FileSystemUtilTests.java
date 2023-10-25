package ru.dlabs.library.email.tests.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.dlabs.library.email.support.TestUtils.getResource;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.junit.jupiter.api.Test;
import ru.dlabs.library.email.mime.FileParametersDetector;
import ru.dlabs.library.email.support.ApacheTikaDetector;
import ru.dlabs.library.email.util.FileSystemUtils;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-24</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
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
        assertEquals(FileSystemUtils.detectFileEncoding(file_utf8, tikaDetector), StandardCharsets.UTF_8);
        assertEquals(FileSystemUtils.detectFileEncoding(file_iso88591, tikaDetector), StandardCharsets.ISO_8859_1);
        assertEquals(FileSystemUtils.detectFileEncoding(file_koi8r, tikaDetector), Charset.forName("KOI8-R"));
        assertEquals(FileSystemUtils.detectFileEncoding(file_utf16be, tikaDetector), StandardCharsets.UTF_16BE);


        assertEquals(FileSystemUtils.detectFileEncoding(file_utf8), Charset.defaultCharset());
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
        assertEquals(contentType, "application/msword");

        contentType = FileSystemUtils.detectFileMimeType(getResource("attachments/file.docx"));
        assertEquals(contentType, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");

        contentType = FileSystemUtils.detectFileMimeType(getResource("attachments/file.jpg"));
        assertEquals(contentType, "image/jpeg");

        contentType = FileSystemUtils.detectFileMimeType(getResource("attachments/file.pdf"));
        assertEquals(contentType, "application/pdf");

        contentType = FileSystemUtils.detectFileMimeType(getResource("attachments/file.png"));
        assertEquals(contentType, "image/png");

        contentType = FileSystemUtils.detectFileMimeType(getResource("attachments/file.rar"));
        assertEquals(contentType, "application/vnd.rar");

        contentType = FileSystemUtils.detectFileMimeType(getResource("attachments/file.rtf"));
        assertEquals(contentType, "application/rtf");

        contentType = FileSystemUtils.detectFileMimeType(getResource("attachments/file.svg"));
        assertEquals(contentType, "image/svg+xml");

        contentType = FileSystemUtils.detectFileMimeType(getResource("attachments/file.tgz"));
        assertTrue(Arrays.asList("application/gzip", "application/x-compressed-tar").contains(contentType));

        contentType = FileSystemUtils.detectFileMimeType(getResource("attachments/file.txt"));
        assertEquals(contentType, "text/plain");

        contentType = FileSystemUtils.detectFileMimeType(getResource("attachments/file.xls"));
        assertEquals(contentType, "application/vnd.ms-excel");

        contentType = FileSystemUtils.detectFileMimeType(getResource("attachments/file.xlsx"));
        assertEquals(contentType, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        contentType = FileSystemUtils.detectFileMimeType(getResource("attachments/file.zip"));
        assertEquals(contentType, "application/zip");
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
        assertEquals(contentType, "image/vnd.djvu");

        contentType = FileSystemUtils.detectFileMimeType(
            getResource("attachments/file.doc"),
            tikaDetector
        );
        assertEquals(contentType, "application/msword");

        contentType = FileSystemUtils.detectFileMimeType(
            getResource("attachments/file.docx"),
            tikaDetector
        );
        assertEquals(contentType, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");

        contentType = FileSystemUtils.detectFileMimeType(
            getResource("attachments/file.jpg"),
            tikaDetector
        );
        assertEquals(contentType, "image/jpeg");

        contentType = FileSystemUtils.detectFileMimeType(
            getResource("attachments/file.pdf"),
            tikaDetector
        );
        assertEquals(contentType, "application/pdf");

        contentType = FileSystemUtils.detectFileMimeType(
            getResource("attachments/file.png"),
            tikaDetector
        );
        assertEquals(contentType, "image/png");

        contentType = FileSystemUtils.detectFileMimeType(
            getResource("attachments/file.rar"),
            tikaDetector
        );
        assertEquals(contentType, "application/x-rar-compressed; version=5");

        contentType = FileSystemUtils.detectFileMimeType(
            getResource("attachments/file.rtf"),
            tikaDetector
        );
        assertEquals(contentType, "application/rtf");

        contentType = FileSystemUtils.detectFileMimeType(
            getResource("attachments/file.svg"),
            tikaDetector
        );
        assertEquals(contentType, "image/svg+xml");

        contentType = FileSystemUtils.detectFileMimeType(
            getResource("attachments/file.tgz"),
            tikaDetector
        );
        assertEquals(contentType, "application/gzip");

        contentType = FileSystemUtils.detectFileMimeType(
            getResource("attachments/file.txt"),
            tikaDetector
        );
        assertEquals(contentType, "text/plain");

        contentType = FileSystemUtils.detectFileMimeType(
            getResource("attachments/file.xls"),
            tikaDetector
        );
        assertEquals(contentType, "application/vnd.ms-excel");

        contentType = FileSystemUtils.detectFileMimeType(
            getResource("attachments/file.xlsx"),
            tikaDetector
        );
        assertEquals(contentType, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        contentType = FileSystemUtils.detectFileMimeType(
            getResource("attachments/file.zip"),
            tikaDetector
        );
        assertEquals(contentType, "application/zip");
    }
}
