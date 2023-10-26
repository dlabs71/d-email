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
    private String folderName;

    public static <T> PageResponse<T> of(List<T> data, int totalCount) {
        return new PageResponse<>(data, totalCount, null);
    }

    public static <T> PageResponse<T> of(List<T> data, int totalCount, String folderName) {
        return new PageResponse<>(data, totalCount, folderName);
    }
}
