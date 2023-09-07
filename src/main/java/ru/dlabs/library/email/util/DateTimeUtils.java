package ru.dlabs.library.email.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.experimental.UtilityClass;

/**
 * Utility class for Date and Time operations
 *
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-09-01
 */
@UtilityClass
public class DateTimeUtils {

    public LocalDateTime convert(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }
}
