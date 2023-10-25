package ru.dlabs.library.email.tests.pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import ru.dlabs.library.email.dto.pageable.PageRequest;

/**
 * <p>
 * <div><strong>Project name:</strong> d-email</div>
 * <div><strong>Creation date:</strong> 2023-10-12</div>
 * </p>
 *
 * @author Ivanov Danila
 * @since 1.0.0
 */
public class PageRequestTest {

    /**
     * The test for:
     * <ul>
     *     <li>{@link PageRequest#getEnd()}</li>
     * </ul>
     */
    @Test
    public void getEndTest() {
        assertEquals(PageRequest.of(0, 10).getEnd(), 9);
        assertEquals(PageRequest.of(10, 10).getEnd(), 19);
        assertEquals(PageRequest.of(2, 7).getEnd(), 8);
    }

    /**
     * The test for:
     * <ul>
     *     <li>{@link PageRequest#incrementStart()}</li>
     * </ul>
     */
    @Test
    public void incrementStartTest() {
        assertEquals(PageRequest.of(0, 10).incrementStart().getStart(), 10);
        assertEquals(PageRequest.of(10, 10).incrementStart().getStart(), 20);
        assertEquals(PageRequest.of(2, 7).incrementStart().getStart(), 9);
    }
}
