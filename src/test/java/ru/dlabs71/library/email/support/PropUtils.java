package ru.dlabs71.library.email.support;

import com.sun.javafx.fxml.PropertyNotFoundException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import ru.dlabs71.library.email.tests.client.receiver.utils.ReceiveTestUtils;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-09-26</div>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Slf4j
@UtilityClass
public class PropUtils {

    private final static Map<String, Properties> cache = new HashMap<>();

    @SneakyThrows
    public Properties loadPropertiesFromFile(String fileName) {
        log.info("Load properties from the file" + fileName);
        if (cache.containsKey(fileName)) {
            log.info("Returns the properties from the cache. Filename = " + fileName);
            return cache.get(fileName);
        }
        Properties properties = new Properties();
        properties.load(ReceiveTestUtils.class.getClassLoader().getResourceAsStream(fileName));

        Enumeration<Object> enumeration = properties.keys();
        while (enumeration.hasMoreElements()) {
            String key = (String) enumeration.nextElement();
            String value = properties.getProperty(key);
            if (value.startsWith("${") && value.endsWith("}")) {
                value = value.substring(2, value.length() - 1);
                String envValue = System.getenv(value);
                if (envValue == null || envValue.isEmpty()) {
                    envValue = System.getProperty(value);
                    if (envValue == null || envValue.isEmpty()) {
                        throw new PropertyNotFoundException(String.format(
                            "Environment variable/runnable parameter '%s' is not found.",
                            value
                        ));
                    }
                }
                log.info(String.format("Environment variable/runnable parameter was found: %s=%s", value, envValue));
                properties.setProperty(key, envValue);
            }
        }

        cache.put(fileName, properties);
        return properties;
    }
}
