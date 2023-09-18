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
import org.junit.jupiter.api.Test;
import ru.dlabs.library.email.dto.message.common.EmailAttachment;
import ru.dlabs.library.email.exception.AttachmentException;
import ru.dlabs.library.email.type.AttachmentType;

/**
 * @author Ivanov Danila
 * @since 0.0.1
 * Project name: d-email
 * Creation date: 2023-09-18
 */
public class AttachmentUtilTests {

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

    @Test
    public void createAttachmentTest() throws URISyntaxException {
        URL urlFile = AttachmentUtilTests.class.getClassLoader().getResource("template.txt");
        File sourceFile = new File(urlFile.toURI());

        EmailAttachment result1 = assertDoesNotThrow(
            () -> AttachmentUtils.create("classpath:template.txt")
        );
        assertEquals(result1.getSize(), sourceFile.length());
        assertEquals(result1.getName(), sourceFile.getName());
        assertEquals(result1.getType(), AttachmentType.TEXT);
        assertEquals(result1.getData().length, sourceFile.length());
    }
}
