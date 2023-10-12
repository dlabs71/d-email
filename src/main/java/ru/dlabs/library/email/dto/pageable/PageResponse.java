package ru.dlabs.library.email.dto.pageable;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The page response class. This class contains the selected data and the total count of all the data
 * </p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-09-02</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
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
