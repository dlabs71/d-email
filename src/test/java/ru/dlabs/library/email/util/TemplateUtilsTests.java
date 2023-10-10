package ru.dlabs.library.email.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import org.apache.velocity.Template;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledIfSystemProperty;
import ru.dlabs.library.email.exception.TemplateCreationException;

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
            () -> TemplateUtils.createTemplate("classpath:template/template.txt")
        );
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
            "classpath:template.txt",
            params
        );

        assertEquals(
            content,
            "It's a template with the name Template for testing a utility class with the name TemplateUtils;"
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
        String content = TemplateUtils.construct("classpath:template.html", params);

        assertEquals(
            content.replace("   ", "").trim(),
            "<div>\n" +
                " <h1>Header Template</h1>\n" +
                " <div>\n" +
                "  <p>It's the content of an HTML page.</p>\n" +
                " </div>\n" +
                "</div>".replace("  ", "").trim()
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

        TemplateUtils.TemplatePath templatePath = TemplateUtils.normalizeTemplatePath(path1);
        assertEquals(templatePath.getPathToDir(), "/home/project/data");
        assertEquals(templatePath.getTemplateName(), "file.txt");

        templatePath = TemplateUtils.normalizeTemplatePath(path2);
        assertEquals(templatePath.getPathToDir(), "/home/project/data");
        assertEquals(templatePath.getTemplateName(), "file.txt");

        templatePath = TemplateUtils.normalizeTemplatePath(path3);
        assertEquals(templatePath.getPathToDir(), "template");
        assertEquals(templatePath.getTemplateName(), "file.txt");

        templatePath = TemplateUtils.normalizeTemplatePath(path4);
        assertEquals(templatePath.getPathToDir(), "");
        assertEquals(templatePath.getTemplateName(), "file.txt");

        templatePath = TemplateUtils.normalizeTemplatePath(path5);
        assertEquals(templatePath.getPathToDir(), "");
        assertEquals(templatePath.getTemplateName(), "file.txt");

        templatePath = TemplateUtils.normalizeTemplatePath(path6);
        assertEquals(templatePath.getPathToDir(), "directory");
        assertEquals(templatePath.getTemplateName(), "file.txt");

        templatePath = TemplateUtils.normalizeTemplatePath(path7);
        assertEquals(templatePath.getPathToDir(), "/home/project/data data");
        assertEquals(templatePath.getTemplateName(), "file file.txt");

        templatePath = TemplateUtils.normalizeTemplatePath(path8);
        assertEquals(templatePath.getPathToDir(), "/home/project/data");
        assertEquals(templatePath.getTemplateName(), "file.txt");
    }
}
