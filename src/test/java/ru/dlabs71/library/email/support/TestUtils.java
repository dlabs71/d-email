package ru.dlabs71.library.email.support;

import java.io.File;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-25</div>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@UtilityClass
public class TestUtils {

    @SneakyThrows
    public static File getResource(String path) {
        return new File(TestUtils.class.getClassLoader().getResource(path).toURI());
    }
}
