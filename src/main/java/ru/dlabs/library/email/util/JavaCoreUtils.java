package ru.dlabs.library.email.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.experimental.UtilityClass;

/**
 * The utility class contains helpful method for working with Java Core.
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-25</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@UtilityClass
public class JavaCoreUtils {

    /**
     * It converts Input Stream to byte array.
     *
     * @param in sourced input stream
     *
     * @return byte array
     *
     * @throws IOException error in the read input stream
     */
    public static byte[] toByteArray(InputStream in) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int len;
        while ((len = in.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        return os.toByteArray();
    }

    /**
     * Converts Date to LocalDateTime.
     *
     * @param date input date
     *
     * @return LocalDateTime
     */
    public LocalDateTime convert(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * If the input parameter 'object' is null, then throw an IllegalArgumentException.
     *
     * @param object  an object being checked
     * @param argName Name parameter for a message of the exception
     *
     * @throws IllegalArgumentException if the 'object' parameter is null
     */
    public void notNullArgument(Object object, String argName) {
        if (object == null) {
            throw new IllegalArgumentException(argName + "is marked non-null but is null");
        }
    }
}
