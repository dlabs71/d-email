package ru.dlabs.library.email.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import lombok.experimental.UtilityClass;

/**
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