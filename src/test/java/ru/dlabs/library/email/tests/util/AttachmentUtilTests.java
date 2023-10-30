package ru.dlabs.library.email.tests.util;

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
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import ru.dlabs.library.email.dto.message.common.EmailAttachment;
import ru.dlabs.library.email.exception.AttachmentException;
import ru.dlabs.library.email.mime.FileParametersDetector;
import ru.dlabs.library.email.support.ApacheTikaDetector;
import ru.dlabs.library.email.support.TestUtils;
import ru.dlabs.library.email.type.AttachmentType;
import ru.dlabs.library.email.util.AttachmentUtils;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-09-18</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Order(150)
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
            () -> AttachmentUtils.createFile("classpath:template-test/template-in-folder.txt")
        );
        assertTrue(result2.exists());
        assertTrue(result2.length() > 0);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link AttachmentUtils#create(String)}</li>
     *     <li>{@link AttachmentUtils#create(String, FileParametersDetector)}</li>
     * </ul>
     */
    @Test
    public void createAttachmentTest() throws URISyntaxException {
        FileParametersDetector detector = new ApacheTikaDetector();

        URL urlFile1 = AttachmentUtilTests.class.getClassLoader().getResource("template.txt");
        File sourceFile1 = new File(urlFile1.toURI());

        URL urlFile2 = AttachmentUtilTests.class.getClassLoader().getResource("attachments/file.jpg");
        File sourceFile2 = new File(urlFile2.toURI());

        EmailAttachment result1 = assertDoesNotThrow(
            () -> AttachmentUtils.create("classpath:template.txt")
        );
        assertEquals((int) sourceFile1.length(), result1.getSize());
        assertEquals(sourceFile1.getName(), result1.getName());
        assertEquals(AttachmentType.TEXT, result1.getType());
        assertEquals(sourceFile1.length(), result1.getData().length);

        EmailAttachment result2 = assertDoesNotThrow(
            () -> AttachmentUtils.create("classpath:attachments/file.jpg")
        );
        assertEquals((int) sourceFile2.length(), result2.getSize());
        assertEquals(sourceFile2.getName(), result2.getName());
        assertEquals(AttachmentType.IMAGE, result2.getType());
        assertEquals(sourceFile2.length(), result2.getData().length);

        EmailAttachment result3 = assertDoesNotThrow(
            () -> AttachmentUtils.create("classpath:template.txt", detector)
        );
        assertEquals((int) sourceFile1.length(), result3.getSize());
        assertEquals(sourceFile1.getName(), result3.getName());
        assertEquals(AttachmentType.TEXT, result3.getType());
        assertEquals(sourceFile1.length(), result3.getData().length);

        EmailAttachment result4 = assertDoesNotThrow(
            () -> AttachmentUtils.create("classpath:attachments/file.jpg", detector)
        );
        assertEquals((int) sourceFile2.length(), result4.getSize());
        assertEquals(sourceFile2.getName(), result4.getName());
        assertEquals(AttachmentType.IMAGE, result4.getType());
        assertEquals(sourceFile2.length(), result4.getData().length);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link AttachmentUtils#createContentTypeForAttachment(File, FileParametersDetector)}</li>
     * </ul>
     */
    @Test
    public void createContentTypeStringTikaTest() {
        FileParametersDetector detector = new ApacheTikaDetector();

        String contentType = AttachmentUtils.createContentTypeForAttachment(
            TestUtils.getResource("attachments/file.djvu"),
            detector
        );
        assertEquals("image/vnd.djvu", contentType);

        contentType = AttachmentUtils.createContentTypeForAttachment(
            TestUtils.getResource("attachments/file.doc"),
            detector
        );
        assertEquals("application/msword", contentType);

        contentType = AttachmentUtils.createContentTypeForAttachment(
            TestUtils.getResource("attachments/file.docx"),
            detector
        );
        assertEquals("application/vnd.openxmlformats-officedocument.wordprocessingml.document", contentType);

        contentType = AttachmentUtils.createContentTypeForAttachment(
            TestUtils.getResource("attachments/file.jpg"),
            detector
        );
        assertEquals("image/jpeg", contentType);

        contentType = AttachmentUtils.createContentTypeForAttachment(
            TestUtils.getResource("attachments/file.pdf"),
            detector
        );
        assertEquals("application/pdf", contentType);

        contentType = AttachmentUtils.createContentTypeForAttachment(
            TestUtils.getResource("attachments/file.png"),
            detector
        );
        assertEquals("image/png", contentType);

        contentType = AttachmentUtils.createContentTypeForAttachment(
            TestUtils.getResource("attachments/file.rar"),
            detector
        );
        assertEquals("application/x-rar-compressed; version=5", contentType);

        contentType = AttachmentUtils.createContentTypeForAttachment(
            TestUtils.getResource("attachments/file.rtf"),
            detector
        );
        assertEquals("application/rtf", contentType);

        contentType = AttachmentUtils.createContentTypeForAttachment(
            TestUtils.getResource("attachments/file.svg"),
            detector
        );
        assertEquals("image/svg+xml", contentType);

        contentType = AttachmentUtils.createContentTypeForAttachment(
            TestUtils.getResource("attachments/file.tgz"),
            detector
        );
        assertEquals("application/gzip", contentType);

        contentType = AttachmentUtils.createContentTypeForAttachment(
            TestUtils.getResource("attachments/file.txt"),
            detector
        );
        assertEquals("text/plain; charset=utf-8", contentType);

        contentType = AttachmentUtils.createContentTypeForAttachment(
            TestUtils.getResource("attachments/file.xls"),
            detector
        );
        assertEquals("application/vnd.ms-excel", contentType);

        contentType = AttachmentUtils.createContentTypeForAttachment(
            TestUtils.getResource("attachments/file.xlsx"),
            detector
        );
        assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", contentType);

        contentType = AttachmentUtils.createContentTypeForAttachment(
            TestUtils.getResource("attachments/file.zip"),
            detector
        );
        assertEquals("application/zip", contentType);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link AttachmentUtils#createContentTypeForAttachment(File)}</li>
     * </ul>
     */
    @Test
    public void createContentTypeStringDefaultTest() {
        String contentType = AttachmentUtils.createContentTypeForAttachment(TestUtils.getResource(
            "attachments/file.djvu"));
        assertTrue(Arrays.asList("image/vnd.djvu", "image/vnd.djvu+multipage").contains(contentType));

        contentType = AttachmentUtils.createContentTypeForAttachment(TestUtils.getResource("attachments/file.doc"));
        assertEquals("application/msword", contentType);

        contentType = AttachmentUtils.createContentTypeForAttachment(TestUtils.getResource("attachments/file.docx"));
        assertEquals("application/vnd.openxmlformats-officedocument.wordprocessingml.document", contentType);

        contentType = AttachmentUtils.createContentTypeForAttachment(TestUtils.getResource("attachments/file.jpg"));
        assertEquals("image/jpeg", contentType);

        contentType = AttachmentUtils.createContentTypeForAttachment(TestUtils.getResource("attachments/file.pdf"));
        assertEquals("application/pdf", contentType);

        contentType = AttachmentUtils.createContentTypeForAttachment(TestUtils.getResource("attachments/file.png"));
        assertEquals("image/png", contentType);

        contentType = AttachmentUtils.createContentTypeForAttachment(TestUtils.getResource("attachments/file.rar"));
        assertEquals("application/vnd.rar", contentType);

        contentType = AttachmentUtils.createContentTypeForAttachment(TestUtils.getResource("attachments/file.rtf"));
        assertEquals("application/rtf", contentType);

        contentType = AttachmentUtils.createContentTypeForAttachment(TestUtils.getResource("attachments/file.svg"));
        assertEquals("image/svg+xml", contentType);

        contentType = AttachmentUtils.createContentTypeForAttachment(TestUtils.getResource("attachments/file.tgz"));
        assertTrue(Arrays.asList("application/gzip", "application/x-compressed-tar").contains(contentType));

        contentType = AttachmentUtils.createContentTypeForAttachment(TestUtils.getResource("attachments/file.txt"));
        assertEquals("text/plain; charset=utf-8", contentType);

        contentType = AttachmentUtils.createContentTypeForAttachment(TestUtils.getResource("attachments/file.xls"));
        assertEquals("application/vnd.ms-excel", contentType);

        contentType = AttachmentUtils.createContentTypeForAttachment(TestUtils.getResource("attachments/file.xlsx"));
        assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", contentType);

        contentType = AttachmentUtils.createContentTypeForAttachment(TestUtils.getResource("attachments/file.zip"));
        assertEquals("application/zip", contentType);
    }
}
