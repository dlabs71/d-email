package ru.dlabs.library.email.util;

import java.io.File;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;
import org.apache.velocity.runtime.resource.loader.JarResourceLoader;
import ru.dlabs.library.email.exception.TemplateCreationException;

/**
 * It's the helper for creating formatted strings using the Velocity engine.
 *
 * <p>For more information about the velocity template engine, use the link:
 * <a href="https://velocity.apache.org/engine/1.7/user-guide.html#what-is-velocity">Apache Velocity Project</a>
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-08-27</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Slf4j
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
     * @param template the object template (see also {@link Template})
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
     *                       of these substrings: 'file://', 'classpath:', 'jar:file:'.
     *
     * @return template object (see also {@link Template})
     *
     * @throws TemplateCreationException this exception will occur if the path to the template isn't valid
     */
    public Template createTemplate(String pathToTemplate) throws TemplateCreationException {
        if (pathToTemplate.startsWith("file://")) {
            return createFileTemplate(pathToTemplate);
        } else if (pathToTemplate.startsWith("classpath:")) {
            return createClasspathTemplate(pathToTemplate);
        } else if (pathToTemplate.startsWith("jar:file:")) {
            return createJarFileTemplate(pathToTemplate);
        }
        throw new TemplateCreationException(
            "Template path must starts with 'file://', 'classpath:' or 'jar:file:'. It's using template path: "
                + pathToTemplate);
    }

    /**
     * This method creates a template object from classpath resource.
     *
     * @param pathTemplate a path to the classpath resource
     *
     * @return template object (see also {@link Template})
     */
    public Template createClasspathTemplate(String pathTemplate) {
        log.debug("A template will create from classpath source using the path equal to {}", pathTemplate);
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADERS, "classpath");
        velocityEngine.setProperty("resource.loader.classpath.class", ClasspathResourceLoader.class.getName());
        velocityEngine.init();

        TemplatePath templatePath = normalizeTemplatePath(pathTemplate);
        return velocityEngine.getTemplate(
            templatePath.getFullPath(),
            StandardCharsets.UTF_8.name()
        );
    }

    /**
     * This method creates a template object from the file system resource.
     *
     * @param pathTemplate a path to the file system resource
     *
     * @return template object (see also {@link Template})
     */
    public Template createFileTemplate(String pathTemplate) {
        log.debug("A template will create from file system source using the path equal to {}", pathTemplate);
        VelocityEngine velocityEngine = new VelocityEngine();
        TemplatePath templatePath = normalizeTemplatePath(pathTemplate);
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADERS, "file");
        velocityEngine.setProperty("resource.loader.file.class", FileResourceLoader.class.getName());
        velocityEngine.setProperty("resource.loader.file.path", templatePath.getPathToDir());
        velocityEngine.setProperty("resource.loader.file.cache", false);
        velocityEngine.setProperty("resource.loader.file.modification_check_interval", 0);
        velocityEngine.init();
        return velocityEngine.getTemplate(templatePath.getTemplateName(), StandardCharsets.UTF_8.name());
    }

    /**
     * This method creates a template object from the file system resource.
     *
     * @param pathTemplate a path to the file system resource
     *
     * @return template object (see also {@link Template})
     */
    public Template createJarFileTemplate(String pathTemplate) {
        log.debug("A template will create from jar using the path equal to {}", pathTemplate);
        VelocityEngine velocityEngine = new VelocityEngine();
        TemplatePath templatePath = normalizeTemplatePath(pathTemplate);
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADERS, "jar");
        velocityEngine.setProperty("resource.loader.jar.class", JarResourceLoader.class.getName());
        velocityEngine.setProperty("resource.loader.jar.path", templatePath.getPathToDir());
        velocityEngine.init();
        return velocityEngine.getTemplate(templatePath.getTemplateName(), StandardCharsets.UTF_8.name());
    }

    /**
     * This method normalizes a template path. It'll remove different prefixes and split the incoming string
     * into a path to a directory and the name of a file in this directory.
     *
     * <p>Prefixes are supported: 'file:///', 'classpath:', 'jar:file:'
     *
     * @param source a path to the resource
     *
     * @return the normalized path as object {@link TemplatePath}
     *
     * @throws java.nio.file.InvalidPathException if the parameter 'source' has incorrect value
     */
    public TemplatePath normalizeTemplatePath(String source) {
        log.debug("Starts template path normalizing for {}", source);
        if (source.startsWith("file://")) {
            source = source.replace("file://", "");
        }
        if (source.startsWith("classpath:")) {
            source = source.replace("classpath:", "");
        }
        if (source.startsWith("jar:file:")) {
            String[] parts = source.split("!");
            return new TemplatePath(parts[1], parts[0]);
        }
        log.debug("Normalized path is {}", source);
        source = Paths.get(source).toString();
        if (!source.contains(File.separator)) {
            return new TemplatePath(source, "");
        }

        return createTemplatePath(source);
    }

    /**
     * This method creates the object TemplatePath from a string path to the resource.
     * It contains the path to the directory and the filename.
     *
     * @param source a path to the resource
     *
     * @return object TemplatePath
     */
    private static TemplatePath createTemplatePath(String source) {
        log.debug("Starts creating instance of the TemplatePath class for {}", source);
        String[] paths = source.split("[\\\\/]");
        String[] pathToDirectory = Arrays.copyOf(paths, paths.length - 1);
        String filename = paths[paths.length - 1];
        log.debug("Create a TemplatePath from filename={} and pathToDirectory={}", filename, pathToDirectory);
        return new TemplatePath(filename, String.join(File.separator, pathToDirectory));
    }

    /**
     * The class describes a path to template.
     */
    @Getter
    @RequiredArgsConstructor
    public static class TemplatePath {

        /** A name for a template file. **/
        private final String templateName;

        /** A path to a directory. **/
        private final String pathToDir;

        public String getFullPath() {
            return pathToDir + File.separator + templateName;
        }
    }
}
