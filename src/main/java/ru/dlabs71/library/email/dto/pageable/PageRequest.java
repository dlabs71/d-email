package ru.dlabs71.library.email.dto.pageable;

import lombok.Getter;

/**
 * The page request class. This class contains information about the start and end of data selection.
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-09-02</div>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
@Getter
public class PageRequest {

    /**
     * Index of an element.
     */
    private int start;

    /**
     * Length of a data selection.
     */
    private final int length;

    private PageRequest(int start, int length) {
        this.start = start;
        this.length = length;
    }

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
    public final int getEnd() {
        return this.getStart() + this.getLength() - 1;
    }

    @Override
    public String toString() {
        return "Page<" + start + ", " + length + ">";
    }
}
