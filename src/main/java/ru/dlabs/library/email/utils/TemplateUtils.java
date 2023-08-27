package ru.dlabs.library.email.utils;

import static org.apache.commons.lang.CharEncoding.UTF_8;

import java.io.StringWriter;
import java.util.Map;
import lombok.experimental.UtilityClass;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

/**
 * @author Ivanov Danila
 * @version 1.0
 */
@UtilityClass
public class TemplateUtils {

    public String construct(String pathToTemplate, Map<String, String> params) {
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        velocityEngine.init();

        Template template = velocityEngine.getTemplate(pathToTemplate, UTF_8);
        VelocityContext context = new VelocityContext(params);

        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        return writer.toString();
    }
}
