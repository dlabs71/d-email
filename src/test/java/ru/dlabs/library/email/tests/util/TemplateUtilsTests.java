package ru.dlabs.library.email.tests.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.velocity.Template;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import ru.dlabs.library.email.exception.TemplateCreationException;
import ru.dlabs.library.email.util.TemplateUtils;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-08-29</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Order(140)
public class TemplateUtilsTests {

    /**
     * The test for:
     * <ul>
     *     <li>{@link TemplateUtils#createTemplate(String)}</li>
     * </ul>
     */
    @Test
    public void templateCreationTest() throws IOException {
        assertThrows(
            TemplateCreationException.class,
            () -> TemplateUtils.createTemplate("/template.txt")
        );

        assertThrows(
            TemplateCreationException.class,
            () -> TemplateUtils.createTemplate("template.txt")
        );

        File file = new File(Paths.get(System.getProperty("java.io.tmpdir"), "d-email-template.txt").toString());
        file.createNewFile();
        assertDoesNotThrow(
            () -> TemplateUtils.createTemplate("file://" + file.getAbsolutePath())
        );
        file.deleteOnExit();

        assertDoesNotThrow(
            () -> TemplateUtils.createTemplate("classpath:template.txt")
        );

        assertDoesNotThrow(
            () -> TemplateUtils.createTemplate("classpath:template-test/template-in-folder.txt")
        );

        String newPathToJar = Paths.get(System.getProperty("java.io.tmpdir"), "template.jar").toString();
        File tmpFile = new File(newPathToJar);
        file.createNewFile();
        FileOutputStream outputStream = new FileOutputStream(tmpFile);
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("template-test/template.jar");
        IOUtils.copy(inputStream, outputStream);
        outputStream.close();
        inputStream.close();
        assertDoesNotThrow(
            () -> TemplateUtils.createTemplate("jar:file:" + newPathToJar + "!/template-in-folder.txt")
        );
        file.deleteOnExit();
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link TemplateUtils#construct(String, Map)}</li>
     * </ul>
     */
    @Test
    public void templateTxtTest() throws TemplateCreationException {
        HashMap<String, Object> params = new HashMap<>();
        params.put("templateName", "Template");
        params.put("className", "TemplateUtils");
        String content = TemplateUtils.construct(
            "classpath:template-test/template-in-folder.txt",
            params
        );

        assertEquals(
            "It's a template with the name Template for testing a utility class with the name TemplateUtils;",
            content
        );
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link TemplateUtils#construct(Template, Map)}</li>
     * </ul>
     */
    @Test
    public void templateHtmlTest() throws TemplateCreationException {
        HashMap<String, Object> params = new HashMap<>();
        params.put("header", "Header Template");
        params.put("content", "It's the content of an HTML page.");
        String content = TemplateUtils.construct("classpath:template-test/template-in-folder.html", params);

        assertEquals(
            "<div><h1>Header Template</h1><div><p>It's the content of an HTML page.</p></div></div>",
            content
        );
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link TemplateUtils#normalizeTemplatePath(String)}</li>
     * </ul>
     */
    @Test
    @DisabledIfSystemProperty(named = "file.separator", matches = "[\\\\]", disabledReason = "File separator has to be '/'")
    public void normalizeFileTemplatePathTest() {
        String path1 = "file:///home/project/data/file.txt";
        String path2 = "/home/project/data/file.txt";
        String path3 = "classpath:template/file.txt";
        String path4 = "classpath:file.txt";
        String path5 = "file.txt";
        String path6 = "directory/file.txt";
        String path7 = "file:///home/project/data data/file file.txt";
        String path8 = "file:///home///project//data//file.txt";
        String path9 = "jar:file:/template/archive.jar!/dir/file.txt";

        TemplateUtils.TemplatePath templatePath = TemplateUtils.normalizeTemplatePath(path1);
        assertEquals("/home/project/data", templatePath.getPathToDir());
        assertEquals("file.txt", templatePath.getTemplateName());

        templatePath = TemplateUtils.normalizeTemplatePath(path2);
        assertEquals("/home/project/data", templatePath.getPathToDir());
        assertEquals("file.txt", templatePath.getTemplateName());

        templatePath = TemplateUtils.normalizeTemplatePath(path3);
        assertEquals("template", templatePath.getPathToDir());
        assertEquals("file.txt", templatePath.getTemplateName());

        templatePath = TemplateUtils.normalizeTemplatePath(path4);
        assertEquals("", templatePath.getPathToDir());
        assertEquals("file.txt", templatePath.getTemplateName());

        templatePath = TemplateUtils.normalizeTemplatePath(path5);
        assertEquals("", templatePath.getPathToDir());
        assertEquals("file.txt", templatePath.getTemplateName());

        templatePath = TemplateUtils.normalizeTemplatePath(path6);
        assertEquals("directory", templatePath.getPathToDir());
        assertEquals("file.txt", templatePath.getTemplateName());

        templatePath = TemplateUtils.normalizeTemplatePath(path7);
        assertEquals("/home/project/data data", templatePath.getPathToDir());
        assertEquals("file file.txt", templatePath.getTemplateName());

        templatePath = TemplateUtils.normalizeTemplatePath(path8);
        assertEquals("/home/project/data", templatePath.getPathToDir());
        assertEquals("file.txt", templatePath.getTemplateName());

        templatePath = TemplateUtils.normalizeTemplatePath(path9);
        assertEquals("jar:file:/template/archive.jar", templatePath.getPathToDir());
        assertEquals("/dir/file.txt", templatePath.getTemplateName());
    }
}
