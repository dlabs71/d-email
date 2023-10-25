package ru.dlabs.library.email.support;

import java.util.Properties;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import ru.dlabs.library.email.tests.client.receiver.ReceiveTestUtils;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-09-26</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@UtilityClass
public class PropUtils {

    @SneakyThrows
    public Properties loadPropertiesFromFile(String fileName) {
        Properties properties = new Properties();
        properties.load(ReceiveTestUtils.class.getClassLoader().getResourceAsStream(fileName));
        return properties;
    }
}
