package ru.dlabs.library.email.tests;

import org.junit.jupiter.api.Test;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-11-03</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
public class Test1 {

    private String value = "value";
    private Integer value1 = 123456;

    @Test
    public void test() {
        System.out.println("Source: " + value);
        getString(value);

        System.out.println("Source: " + value1);
        getString(value1);
    }

    public void getString(String v) {
        v += "1";
        System.out.println(v);
        System.out.println(value);

        String val = value;
        val+=123;
        System.out.println(val);
        System.out.println(value);
    }

    public void getString(Integer v) {
        v += 23;
        System.out.println(v);
        System.out.println(value1);
    }
}
