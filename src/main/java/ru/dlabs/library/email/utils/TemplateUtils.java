package ru.dlabs.library.email.utils;

import java.io.File;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;
import ru.dlabs.library.email.exception.TemplateCreationException;

/**
 * It's the helper for creating formatted strings using the Velocity engine
 *
 * @author Ivanov Danila
 * @version 1.0
 */
@UtilityClass
public class TemplateUtils {

    /**
     * It returns the string constructed from the template and its parameters.
     *
     * @param pathToTemplate the path to a template
     * @param params         parameters of a template
     *
     * @return the converted string
     *
     * @throws TemplateCreationException this exception will occur if the path to the template isn't valid
     */
    public String construct(String pathToTemplate, Map<String, Object> params) throws TemplateCreationException {
        Template template = createTemplate(pathToTemplate);
        return construct(template, params);
    }

    /**
     * It returns the string constructed from the template and its parameters.
     *
     * @param template the object template {@see Template}
     * @param params   parameters of a template
     *
     * @return the converted string
     */
    public String construct(Template template, Map<String, Object> params) {
        VelocityContext context = new VelocityContext(params);
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }

    /**
     * This method creates a template object by using a path to the template.
     *
     * @param pathToTemplate a path to the template. This parameter must start with one
     *                       of these substrings: 'file://' or 'classpath:'.
     *
     * @return template object {@see Template}
     *
     * @throws TemplateCreationException this exception will occur if the path to the template isn't valid
     */
    public Template createTemplate(String pathToTemplate) throws TemplateCreationException {
        if (pathToTemplate.startsWith("file://")) {
            return createFileTemplate(pathToTemplate);
        } else if (pathToTemplate.startsWith("classpath:")) {
            return createClasspathTemplate(pathToTemplate);
        }
        throw new TemplateCreationException(
            "Template path must starts with 'file://' or 'classpath:'. It's using template path: " + pathToTemplate
        );
    }

    /**
     * This method creates a template object from classpath resource
     *
     * @param pathTemplate a path to the classpath resource
     *
     * @return template object {@see Template}
     */
    public Template createClasspathTemplate(String pathTemplate) {
        VelocityEngine velocityEngine = new VelocityEngine();
        TemplatePath templatePath = normalizeClasspathTemplatePath(pathTemplate);
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        velocityEngine.setProperty("file.resource.loader.path", templatePath.getPathToDir());
        velocityEngine.init();
        return velocityEngine.getTemplate(templatePath.getTemplateName(), StandardCharsets.UTF_8.name());
    }

    /**
     * This method creates a template object from the file system resource
     *
     * @param pathTemplate a path to the file system resource
     *
     * @return template object {@see Template}
     */
    public Template createFileTemplate(String pathTemplate) {
        VelocityEngine velocityEngine = new VelocityEngine();
        TemplatePath templatePath = normalizeFileTemplatePath(pathTemplate);
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "file");
        velocityEngine.setProperty("classpath.resource.loader.class", FileResourceLoader.class.getName());
        velocityEngine.setProperty("file.resource.loader.path", templatePath.getPathToDir());
        velocityEngine.setProperty("file.resource.loader.cache", false);
        velocityEngine.setProperty("file.resource.loader.modificationCheckInterval", 0);
        velocityEngine.init();
        return velocityEngine.getTemplate(templatePath.getTemplateName(), StandardCharsets.UTF_8.name());
    }

    /**
     * This method normalizes a template path, assuming it's the classpath resource
     *
     * @param source a path to the resource
     *
     * @return the normalized path
     */
    public TemplatePath normalizeClasspathTemplatePath(String source) {
        if (source.startsWith("classpath:")) {
            source = source.replace("classpath:", "");
        }
        if (!source.contains(File.separator)) {
            return new TemplatePath(source, "");
        }

        return createTemplatePath(source);
    }

    /**
     * This method normalizes a template path, assuming it's the file system resource
     *
     * @param source a path to the resource
     *
     * @return the normalized path
     */
    public TemplatePath normalizeFileTemplatePath(String source) {
        if (source.startsWith("file://")) {
            source = source.replace("file://", "");
        }
        if (!source.contains(File.separator)) {
            return new TemplatePath(source, "");
        }

        return createTemplatePath(source);
    }

    /**
     * This method creates the object TemplatePath from a string path to the resource.
     * It contains the path to the directory and the filename
     *
     * @param source a path to the resource
     *
     * @return object TemplatePath
     */
    private static TemplatePath createTemplatePath(String source) {
        String[] paths = source.split(File.separator);
        String[] pathToDirectory = Arrays.copyOf(paths, paths.length - 1);
        String filename = paths[paths.length - 1];
        return new TemplatePath(filename, String.join(File.separator, pathToDirectory));
    }

    @Getter
    @RequiredArgsConstructor
    public static class TemplatePath {

        private final String templateName;
        private final String pathToDir;
    }
}
