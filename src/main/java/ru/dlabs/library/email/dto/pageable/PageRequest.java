package ru.dlabs.library.email.dto.pageable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Ivanov Danila
 * Project name: d-email
 * Creation date: 2023-09-02
 */
@Getter
@Setter
@AllArgsConstructor
public class PageRequest {

    private int start;
    private int length;

    public static PageRequest of(int start, int length) {
        return new PageRequest(start, length);
    }

    public PageRequest incrementStart() {
        this.setStart(this.getEnd());
        return this;
    }

    public int getEnd() {
        return this.getStart() + this.getLength() - 1;
    }
}
