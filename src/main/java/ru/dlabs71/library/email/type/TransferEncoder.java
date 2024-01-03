package ru.dlabs71.library.email.type;

import java.util.Arrays;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * The enum of supported values of Content-Transfer-Encoding header.
 * You can read of this header here - <a href="https://www.w3.org/Protocols/rfc1341/5_Content-Transfer-Encoding.html">Content-Transfer-Encoding</a>
 *
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-16</div>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Slf4j
@Getter
public enum TransferEncoder {

    SEVEN_BIT("7bit"),
    EIGHT_BIT("8bit"),
    BINARY("binary");

    private final String name;

    /**
     * The constructor of this enum.
     *
     * @param name a value of Content-Transfer-Encoding header
     */
    TransferEncoder(String name) {
        this.name = name;
    }

    /**
     * Returns default value for a Content-Transfer-Encoding header.
     */
    public static TransferEncoder byDefault() {
        return TransferEncoder.EIGHT_BIT;
    }

    /**
     * Finds corresponding enum value for string in the argument.
     *
     * @param value a value of a Content-Transfer-Encoding header.
     *
     * @return a value of a Content-Transfer-Encoding header or null
     */
    public static TransferEncoder forName(String value) {
        log.debug("Tries to look up the Content Transfer Encoding in the TransferEncoder. Value is {}", value);
        if (value == null) {
            return null;
        }
        return Arrays.stream(values())
            .filter(item -> item.getName().equals(value))
            .findFirst()
            .orElse(null);
    }
}
