package ru.dlabs.library.email.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Utility class for IO operations
 *
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-09-06
 */
public class IOUtils {

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
}
