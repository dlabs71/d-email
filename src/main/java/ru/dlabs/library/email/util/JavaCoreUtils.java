package ru.dlabs.library.email.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.experimental.UtilityClass;

/**
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
     * It converts Input Stream to byte array
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

    public LocalDateTime convert(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }
}
