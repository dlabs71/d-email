package ru.dlabs.library.email.message;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-09-02
 */
@Getter
@AllArgsConstructor
public class PageResponse<T> {

    private List<T> data;
    private int totalCount;

    public static <T> PageResponse<T> of(List<T> data, int totalCount) {
        return new PageResponse<>(data, totalCount);
    }
}
