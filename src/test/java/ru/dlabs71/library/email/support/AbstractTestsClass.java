package ru.dlabs71.library.email.support;

import java.util.Properties;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-09-26</div>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
public abstract class AbstractTestsClass {

    protected final Integer sendDelayAfter;

    public AbstractTestsClass() {
        Properties properties = PropUtils.loadPropertiesFromFile("tests.properties");
        sendDelayAfter = Integer.valueOf(properties.getProperty("send.delay.after", "1000"));
    }
}
