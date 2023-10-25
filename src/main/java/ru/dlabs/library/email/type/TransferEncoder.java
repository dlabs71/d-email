package ru.dlabs.library.email.type;

import java.util.Arrays;
import lombok.Getter;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-16</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Getter
public enum TransferEncoder {

    SEVEN_BIT("7bit"),
    EIGHT_BIT("8bit"),
    BINARY("binary");

    private final String name;

    TransferEncoder(String name) {
        this.name = name;
    }

    public static TransferEncoder byDefault() {
        return TransferEncoder.EIGHT_BIT;
    }

    public static TransferEncoder forName(String value) {
        if (value == null) {
            return null;
        }
        return Arrays.stream(values()).filter(item -> item.getName().equals(value)).findFirst().orElse(null);
    }
}
