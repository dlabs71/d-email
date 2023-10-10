package ru.dlabs.library.email.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Arrays;
import lombok.SneakyThrows;
import org.apache.tika.Tika;
import org.junit.jupiter.api.Test;
import ru.dlabs.library.email.dto.message.common.EmailAttachment;
import ru.dlabs.library.email.exception.AttachmentException;
import ru.dlabs.library.email.mime.MimeTypeDetector;
import ru.dlabs.library.email.type.AttachmentType;

/**
 * @author Ivanov Danila
 * @since 0.0.1
 * Project name: d-email
 * Creation date: 2023-09-18
 */
public class AttachmentUtilTests {

    /**
     * The test for:
     * <ul>
     *     <li>{@link AttachmentUtils#createFile(String)}</li>
     * </ul>
     */
    @Test
    public void createFileTest() throws IOException {
        assertThrows(
            AttachmentException.class,
            () -> AttachmentUtils.createFile("/random-name-file.txt")
        );

        assertThrows(
            AttachmentException.class,
            () -> AttachmentUtils.createFile("random-name-file.txt")
        );

        File file = new File(Paths.get(System.getProperty("java.io.tmpdir"), "d-email-file.txt").toString());
        file.createNewFile();
        assertDoesNotThrow(
            () -> AttachmentUtils.createFile("file://" + file.getAbsolutePath())
        );
        file.deleteOnExit();

        File result1 = assertDoesNotThrow(
            () -> AttachmentUtils.createFile("classpath:template.txt")
        );
        assertTrue(result1.exists());
        assertTrue(result1.length() > 0);

        File result2 = assertDoesNotThrow(
            () -> AttachmentUtils.createFile("classpath:template/template.txt")
        );
        assertTrue(result2.exists());
        assertTrue(result2.length() > 0);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link AttachmentUtils#create(String)}</li>
     *     <li>{@link AttachmentUtils#create(String, MimeTypeDetector)}</li>
     * </ul>
     */
    @Test
    public void createAttachmentTest() throws URISyntaxException {
        MimeTypeDetector detector = new TikaMimeTypeDetector();

        URL urlFile1 = AttachmentUtilTests.class.getClassLoader().getResource("template.txt");
        File sourceFile1 = new File(urlFile1.toURI());

        URL urlFile2 = AttachmentUtilTests.class.getClassLoader().getResource("attachments/file.jpg");
        File sourceFile2 = new File(urlFile2.toURI());

        EmailAttachment result1 = assertDoesNotThrow(
            () -> AttachmentUtils.create("classpath:template.txt")
        );
        assertEquals(result1.getSize(), sourceFile1.length());
        assertEquals(result1.getName(), sourceFile1.getName());
        assertEquals(result1.getType(), AttachmentType.TEXT);
        assertEquals(result1.getData().length, sourceFile1.length());

        EmailAttachment result2 = assertDoesNotThrow(
            () -> AttachmentUtils.create("classpath:attachments/file.jpg")
        );
        assertEquals(result2.getSize(), sourceFile2.length());
        assertEquals(result2.getName(), sourceFile2.getName());
        assertEquals(result2.getType(), AttachmentType.IMAGE);
        assertEquals(result2.getData().length, sourceFile2.length());

        EmailAttachment result3 = assertDoesNotThrow(
            () -> AttachmentUtils.create("classpath:template.txt", detector)
        );
        assertEquals(result3.getSize(), sourceFile1.length());
        assertEquals(result3.getName(), sourceFile1.getName());
        assertEquals(result3.getType(), AttachmentType.TEXT);
        assertEquals(result3.getData().length, sourceFile1.length());

        EmailAttachment result4 = assertDoesNotThrow(
            () -> AttachmentUtils.create("classpath:attachments/file.jpg", detector)
        );
        assertEquals(result4.getSize(), sourceFile2.length());
        assertEquals(result4.getName(), sourceFile2.getName());
        assertEquals(result4.getType(), AttachmentType.IMAGE);
        assertEquals(result4.getData().length, sourceFile2.length());
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link AttachmentUtils#createContentTypeString(File, MimeTypeDetector)}</li>
     * </ul>
     */
    @Test
    public void createContentTypeStringTikaTest() {
        MimeTypeDetector detector = new TikaMimeTypeDetector();

        String contentType = AttachmentUtils.createContentTypeString(
            this.getResource("attachments/file.djvu"),
            detector
        );
        assertEquals(contentType, "image/vnd.djvu");

        contentType = AttachmentUtils.createContentTypeString(this.getResource("attachments/file.doc"), detector);
        assertEquals(contentType, "application/msword");

        contentType = AttachmentUtils.createContentTypeString(this.getResource("attachments/file.docx"), detector);
        assertEquals(contentType, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");

        contentType = AttachmentUtils.createContentTypeString(this.getResource("attachments/file.jpg"), detector);
        assertEquals(contentType, "image/jpeg");

        contentType = AttachmentUtils.createContentTypeString(this.getResource("attachments/file.pdf"), detector);
        assertEquals(contentType, "application/pdf");

        contentType = AttachmentUtils.createContentTypeString(this.getResource("attachments/file.png"), detector);
        assertEquals(contentType, "image/png");

        contentType = AttachmentUtils.createContentTypeString(this.getResource("attachments/file.rar"), detector);
        assertEquals(contentType, "application/x-rar-compressed; version=5");

        contentType = AttachmentUtils.createContentTypeString(this.getResource("attachments/file.rtf"), detector);
        assertEquals(contentType, "application/rtf");

        contentType = AttachmentUtils.createContentTypeString(this.getResource("attachments/file.svg"), detector);
        assertEquals(contentType, "image/svg+xml");

        contentType = AttachmentUtils.createContentTypeString(this.getResource("attachments/file.tgz"), detector);
        assertEquals(contentType, "application/gzip");

        contentType = AttachmentUtils.createContentTypeString(this.getResource("attachments/file.txt"), detector);
        assertEquals(contentType, "text/plain; charset=utf8");

        contentType = AttachmentUtils.createContentTypeString(this.getResource("attachments/file.xls"), detector);
        assertEquals(contentType, "application/vnd.ms-excel");

        contentType = AttachmentUtils.createContentTypeString(this.getResource("attachments/file.xlsx"), detector);
        assertEquals(contentType, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        contentType = AttachmentUtils.createContentTypeString(this.getResource("attachments/file.zip"), detector);
        assertEquals(contentType, "application/zip");
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link AttachmentUtils#createContentTypeString(File)}</li>
     * </ul>
     */
    @Test
    public void createContentTypeStringDefaultTest() {
        String contentType = AttachmentUtils.createContentTypeString(this.getResource("attachments/file.djvu"));
        assertTrue(Arrays.asList("image/vnd.djvu", "image/vnd.djvu+multipage").contains(contentType));

        contentType = AttachmentUtils.createContentTypeString(this.getResource("attachments/file.doc"));
        assertEquals(contentType, "application/msword");

        contentType = AttachmentUtils.createContentTypeString(this.getResource("attachments/file.docx"));
        assertEquals(contentType, "application/vnd.openxmlformats-officedocument.wordprocessingml.document");

        contentType = AttachmentUtils.createContentTypeString(this.getResource("attachments/file.jpg"));
        assertEquals(contentType, "image/jpeg");

        contentType = AttachmentUtils.createContentTypeString(this.getResource("attachments/file.pdf"));
        assertEquals(contentType, "application/pdf");

        contentType = AttachmentUtils.createContentTypeString(this.getResource("attachments/file.png"));
        assertEquals(contentType, "image/png");

        contentType = AttachmentUtils.createContentTypeString(this.getResource("attachments/file.rar"));
        assertEquals(contentType, "application/vnd.rar");

        contentType = AttachmentUtils.createContentTypeString(this.getResource("attachments/file.rtf"));
        assertEquals(contentType, "application/rtf");

        contentType = AttachmentUtils.createContentTypeString(this.getResource("attachments/file.svg"));
        assertEquals(contentType, "image/svg+xml");

        contentType = AttachmentUtils.createContentTypeString(this.getResource("attachments/file.tgz"));
        assertTrue(Arrays.asList("application/gzip", "application/x-compressed-tar").contains(contentType));

        contentType = AttachmentUtils.createContentTypeString(this.getResource("attachments/file.txt"));
        assertEquals(contentType, "text/plain; charset=utf8");

        contentType = AttachmentUtils.createContentTypeString(this.getResource("attachments/file.xls"));
        assertEquals(contentType, "application/vnd.ms-excel");

        contentType = AttachmentUtils.createContentTypeString(this.getResource("attachments/file.xlsx"));
        assertEquals(contentType, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        contentType = AttachmentUtils.createContentTypeString(this.getResource("attachments/file.zip"));
        assertEquals(contentType, "application/zip");
    }

    private static class TikaMimeTypeDetector implements MimeTypeDetector {

        private final Tika tika = new Tika();

        @Override
        @SneakyThrows
        public String detect(File file) {
            return tika.detect(file);
        }
    }

    @SneakyThrows
    private File getResource(String path) {
        return new File(getClass().getClassLoader().getResource(path).toURI());
    }
}
