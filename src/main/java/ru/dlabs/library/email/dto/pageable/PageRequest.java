package ru.dlabs.library.email.dto.pageable;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The page request class. This class contains information about the start and end of data selection.
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
public class PageRequest {

    /**
     * Index of an element.
     */
    private int start;

    /**
     * Length of a data selection.
     */
    private int length;

    public static PageRequest of(int start, int length) {
        return new PageRequest(start, length);
    }

    /**
     * Calculates the next start index.
     *
     * @return current instance of the class {@link PageRequest}
     */
    public synchronized PageRequest incrementStart() {
        this.start = this.getEnd() + 1;
        return this;
    }

    /**
     * Gets end index of the data selection.
     *
     * @return int index
     */
    public int getEnd() {
        return this.getStart() + this.getLength() - 1;
    }
}
