package ru.dlabs71.library.email.dto.pageable;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import lombok.Getter;

/**
 * The page response class. This class contains the selected data and the total count of all the data.
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-09-02</div>
 *
 * @param <T> a type of data in the list
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Getter
public class PageResponse<T> {

    /**
     * Result of a selection.
     */
    private final List<T> data;

    /**
     * Total amount of objects in a storage.
     */
    private final int totalCount;

    /**
     * Extra information about results or query.
     */
    private final Map<String, Object> metadata;

    /**
     * Constructor of this class.
     *
     * @param data       Result of a selection.
     * @param totalCount Total amount of objects in a storage.
     * @param metadata   Extra information about results or query.
     */
    public PageResponse(List<T> data, int totalCount, Map<String, Object> metadata) {
        this.data = data;
        this.totalCount = totalCount;

        if (metadata == null) {
            this.metadata = Collections.emptyMap();
        } else {
            this.metadata = metadata;
        }
    }

    /**
     * Constructor of this class. Metadata is empty Map.
     *
     * @param data       Result of a selection.
     * @param totalCount Total amount of objects in a storage.
     */
    public PageResponse(List<T> data, int totalCount) {
        this.data = data;
        this.totalCount = totalCount;
        this.metadata = Collections.emptyMap();
    }

    /**
     * Returns a new instance of {@link PageResponse}. Metadata is empty Map.
     *
     * @param data       list with objects
     * @param totalCount total amount of objects in a storage
     * @param <T>        a type of data in the list
     */
    public static <T> PageResponse<T> of(List<T> data, int totalCount) {
        return new PageResponse<>(data, totalCount);
    }

    /**
     * Returns a new instance of {@link PageResponse}.
     *
     * @param data       list with objects
     * @param totalCount total amount of objects in a storage
     * @param metadata   extra information about results or query.
     * @param <T>        a type of data in the list
     */
    public static <T> PageResponse<T> of(List<T> data, int totalCount, Map<String, Object> metadata) {
        return new PageResponse<>(data, totalCount, metadata);
    }
}
