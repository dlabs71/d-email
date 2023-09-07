package ru.dlabs.library.email.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import org.junit.jupiter.api.Test;
import ru.dlabs.library.email.exception.TemplateCreationException;

public class TemplateUtilsTests {

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
}
